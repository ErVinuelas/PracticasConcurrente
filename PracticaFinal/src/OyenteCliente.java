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
import mensajes.MensajeEmitirFichero;
import mensajes.MensajePedirFichero;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;


//TODO arreglar los booleanos de los mensajes,están mezclados
public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;

    //Atributo que guarda el último mensaje que le llega a OyenteCliente
    public volatile Mensaje lastMessage;

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
							Usuario usuario = mc.getUser();
							usuario.IP = sc.getInetAddress().toString();
							Servidor.userLst.put(usuario.nombre,usuario);
						} else {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							stop = true;
						}

						break;
					case PEDIR_LISTA:
						if(m.isACK()) {
							Log.debug("He recibido una lista de usuarios, que hago con eso, me lo como?", sc);
						}
						else {
							//Mandamos la lista de usuarios
	                        Log.debug("Mandando la lista de usuarios", sc);
	                        fOut.writeObject(new MensajeSolicListaUsuar(Servidor.userLst, true));
						}
						break;
						
					case PEDIR_FICHERO:
                        // Decidir quien manda fichero(emisor)
                        String userId = Servidor.userToFile.get(m.getFileName());

                        // Mandar mensaje al emisor para que cree el emisor
                        Servidor.flujoLst.get(userId).writeObject(new MensajeEmitirFichero(TipoConexion.ABRIR, false, userId, m.getFileName()));
						break;
                    case PREPARADO_CS:
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                        fOut.writeObject(new MensajePreparadoSC(TipoConexion.PREPARADO_SC, true, m.getIP(), m.getPort());
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
