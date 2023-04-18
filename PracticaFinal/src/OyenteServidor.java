import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.lang.model.util.ElementScanner6;

import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class OyenteServidor extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected ObjectOutputStream fOut;
	// protected Servidor servidor;
	protected int id;
	protected Usuario user;

	public OyenteServidor(Socket sc, Usuario user) {
		this.sc = sc;
		this.user = user;
		// this.servidor = servidor;
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
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, user.getName()));
			Log.debug("Esperando confirmacion de canal preparado...", sc);
			while (sigue) {
				Mensaje m = (Mensaje) fIn.readObject();
				switch (m.getTipo()) {
					case CONEXION:
						MensajeConexion mc = (MensajeConexion) m;
						if (mc.getMessage() == TipoConexion.CERRAR) {
							if (!mc.isACK()) {
								Log.debug("Cerrando canal...", sc);
								fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user.getName()));
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
						break;
					case PEDIR_FICHERO:
						break;
					case EMITIR_FICHERO:
						break;
					default:
						Log.error("Mensaje no reconocido", sc);
				}
			}
		} catch (Exception e) {
			Log.error("error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, user.getName()));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

	public int hashCode() {
		return id;
	}

}