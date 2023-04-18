import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
							fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, user.getNombre()));

							// Actualizamos la tabla de usuarios
							usuario = new Usuario(user.getNombre(), sc.getInetAddress(), sc.getPort());
							Servidor.userLst.put(user);
						} else {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user.getNombre()));
							stop = true;
						}

						break;
					case PEDIR_LISTA:
                        //Mandamos la lista de usuarios
                        Log.debug("Mandando la lista de usuarios", sc);
                        fOut.writeObject(Servidor.userLst);
                        fout.writeObject(new MensajePedirFichero(TipoConexion.PEDIR_FICHERO, true));
						break;
					case PEDIR_FICHERO:
                        // Decidir quien manda fichero(emisor)
                        String userId = Servidor.userToFile.get(m.getFileName());


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
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, user.getNombre()));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

}
