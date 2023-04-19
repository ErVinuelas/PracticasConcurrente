import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.lang.model.util.ElementScanner6;

import data.Usuario;
import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;

public class OyenteServidor extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected ObjectOutputStream fOut;

	protected int id;
	protected Usuario user;
	protected Cliente cliente;
	
	protected Semaphore viaLibre;

	public OyenteServidor(Socket sc, Usuario user, Semaphore viaLibre, Cliente cliente) {
		this.sc = sc;
		this.user = user;
		this.viaLibre = viaLibre;
		this.cliente = cliente;
		Log.debug("iniciando oyente", sc);
		try {
			fIn = new ObjectInputStream(sc.getInputStream());
			fOut = new ObjectOutputStream(sc.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.debug("oyente iniciado", sc);
		// this.id = id;
	}

	public ObjectOutputStream getFout() {
		return fOut;
	}

	public void run() {
		try {
			boolean sigue = true;
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, user));
			Log.debug("Esperando confirmacion de canal preparado...", sc);
			while (sigue) {
				Mensaje m = (Mensaje) fIn.readObject();
				switch (m.getTipo()) {

					case CONEXION:
						MensajeConexion mc = (MensajeConexion) m;
						if (mc.getMessage() == TipoConexion.CERRAR) {
							if (!mc.isACK()) {
								Log.debug("Cerrando canal...", sc);
								fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							} else {
								Log.debug("Canal cerrado", sc);
							}
							sigue = false;
							break;
						} else {
							Log.debug("Canal preparado", sc);
						}
						break;

					case PEDIR_LISTA:
						MensajeSolicListaUsuar ms = (MensajeSolicListaUsuar) m;
						if (!ms.isACK()) {
							Log.error("Error al solicitar lista de usuarios: yo no soy un servidor", sc);
						} else {
							Map<String, Usuario> usrs = ms.getUsuarios();
							Log.debug("Lista de usuarios recibida", sc);
							for (Usuario u : usrs.values()) {
								System.out.println("Usuario:\n" + u.toString());
							}
							viaLibre.release();
						}
						break;

					case EMITIR_FICHERO:
						//Mandamos mensaje de confirmación al servidor. Tenemos que crear un emisor que gestione
						//la conexión p2p y devolvemos el nombre
						
						//Cargamos el mensaje a mandar para pasarselo al emisor
						String file = cliente.archivos.get(m.getFileName());
						
						Emisor emisor = new Emisor(user.port, user.IP, file);
						
						fout.writeObject(new MensajePreparadoCS(m.getUserName(), user.IP, user.port, true));
						break;
						
					case PREPARADO_SC:
						//Crear el receptor(nuevo thread)
						

					default:
						Log.error("Mensaje no reconocido", sc);
				}
			}
		} catch (Exception e) {
			Log.error("Error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, user));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

	public int hashCode() {
		return id;
	}

}
