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
							Usuario usuario = new Usuario(user.nombre, sc.getInetAddress().toString(), user.puerto);
							Servidor.userLst.put(user);
						} else {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							stop = true;
						}

						break;
					case PEDIR_LISTA:
                        //Mandamos la lista de usuarios
                        Log.debug("Mandando la lista de usuarios", sc);
                        fOut.writeObject(Servidor.userLst);
                        fOut.writeObject(new MensajePedirFichero(TipoConexion.ABRIR, true));
						break;
					case PEDIR_FICHERO:
                        // Decidir quien manda fichero(emisor)
                        String userId = Servidor.userToFile.get(m.getFileName());

                        // Mandar mensaje al emisor para que cree el emisor
                        Servidor.flujoLst.get(userId).writeObject(new MensajeEmitirFichero(TipoConexion.ABRIR, false, userId, m.getFileName()));
                        
                        //TODO: implementar semaforo para que controle que el Oyente quede pendiente del ultimo mensaje que le ha llegado al oyente cliente asociado
                        //al cliente que va a emitir el fichero.

                        // Mandar mensaje a receptor para que cree el receptore inicie conexion con la IP y el puerto que le vamos a pasar asociado al mensaje
                        fOut.writeObject(new Mensaje(TipoConexion.ABRIR, true));
						break;
                    case PREPARADO_CS:
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                        fOut.writeObject(new MensajePreparadoSC(TipoConexion.PREPARADO_SC, true, Servidor.userLst.get(m.getFileName()).getIp(), Servidor.userLst.get(m.getFileName()).getPort()));
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
