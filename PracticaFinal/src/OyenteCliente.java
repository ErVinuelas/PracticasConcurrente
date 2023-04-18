import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import data.Usuario;
import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;

	protected Usuario user;

	public OyenteCliente(Socket sc, Usuario user) {
		this.sc = sc;
		Log.debug("iniciando oyente", sc);
		try {
			fOut = new ObjectOutputStream(sc.getOutputStream());
			fIn = new ObjectInputStream(sc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.debug("oyente iniciado", sc);
	}

	public ObjectOutputStream getFout() {
		return fOut;
	}

	public void run() {
		try {
			boolean stop = false;

			while (!stop) {
				Mensaje m = (Mensaje) fIn.readObject();
				Log.debug("mensaje recibido de tipo " + m.getTipo().toString(), sc);
				switch (m.getTipo()) {
					case CONEXION:

						MensajeConexion mc = (MensajeConexion) m;
						if (mc.getMessage() == TipoConexion.ABRIR) {
							Log.debug("Canal preparado", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, user));

							// Actualizamos la tabla de usuarios
							//Servidor.userLst.put(new Usuario(user.nombre, sc.getInetAddress().toString(), user.puerto));
						} else {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							stop = true;
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
			Log.debug("Canal Cerrado", sc);
			fIn.close();
		} catch (Exception e) {
			Log.error("error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, user));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

}
