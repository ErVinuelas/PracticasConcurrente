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
import mensajes.MensajeActualizarListaUsuarios;
import mensajes.MensajeConexion;
import mensajes.MensajeEmitirFichero;
import mensajes.MensajePedirFichero;
import mensajes.MensajePreparadoCS;
import mensajes.MensajePreparadoSC;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;
import mensajes.TipoMensaje;

//TODO arreglar los booleanos de los mensajes,están mezclados
public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;

	protected Usuario user;
	protected Usuario usuario;

	public OyenteCliente(Socket sc, Usuario user) {
		this.sc = sc;
		this.user = user;

		Log.debug("iniciando oyente", sc);
		try {
			fOut = new ObjectOutputStream(sc.getOutputStream());
			fIn = new ObjectInputStream(sc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.debug("oyente iniciado", sc);
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
							usuario = mc.getUser();
							usuario.IP = sc.getInetAddress().toString();
							Servidor.userLst.put(usuario.nombre,usuario);
							Servidor.flujoLst.put(usuario.nombre, new Flujos(fIn, fOut));
							for(String arc : usuario.archivos) {
								Servidor.fileToUser.put(arc, usuario.nombre);
							}
						} else {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							fOut.flush();
							fOut.close();
							fIn.close();
							sc.close();
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
						
						MensajePedirFichero mf = (MensajePedirFichero)m;
						
                        // Decidir quien manda fichero(emisor)
                        String userId = Servidor.fileToUser.get(mf.getFileName());
                        Log.debug("pedido " + mf.getFileName() + " que pertenece a " + userId, sc);

                        // Mandar mensaje al emisor para que cree el emisor
                        Servidor.flujoLst.get(userId).getFout().writeObject(new MensajeEmitirFichero(mf.getFileName(), usuario.nombre, false));
						break;
						
                    case PREPARADO_CS:
                    	
                    	MensajePreparadoCS mp = (MensajePreparadoCS)m;
                    	
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                    	Servidor.flujoLst.get(mp.getUser()).getFout().writeObject(new MensajePreparadoSC(mp.getIP(), mp.getPort(), mp.getFileName()));
					
                        break;
                    
                    case ACTUALIZAR_LISTA:
                    	
                    	MensajeActualizarListaUsuarios ma = (MensajeActualizarListaUsuarios)m;
                    	
                    	//Actualizamos la lista de usuarios
                    	
                    	Servidor.userLst.get(ma.idCliente).addFile(ma.nombreArchivo);
                    	
                    	//También actualizamos la lista de dependencia
                    	
                    	//Servidor.fileToUser.get(ma.nombreArchivo) = ma.idCliente;
                    	
                    	fOut.writeObject(new MensajeActualizarListaUsuarios(ma.idCliente, ma.nombreArchivo, true));
                        
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

	public ObjectOutputStream getFout() {
		return fOut;
	}

}
