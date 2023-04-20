import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeSet;

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
	protected String usuario;

	public OyenteCliente(Socket sc, Usuario user) {
		this.sc = sc;
		this.user = user;

		Log.debug("iniciando oyente", sc);
	}

	public void run() {
		try {
			boolean stop = false;
			fIn = new ObjectInputStream(sc.getInputStream());

			while (!stop) {
				Mensaje m = (Mensaje) fIn.readObject();
				Log.debug("mensaje recibido de tipo " + m.getTipo().toString(), sc);

				switch (m.getTipo()) {
					case CONEXION:

						MensajeConexion mc = (MensajeConexion) m;
						
						if (mc.getMessage() == TipoConexion.ABRIR) {
							fOut = new ObjectOutputStream(sc.getOutputStream());
							Log.debug("Canal preparado", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, user));
							
							// Actualizamos la tabla de usuarios
							usuario = mc.getUser().nombre;
							Usuario auxuser = mc.getUser();
							auxuser.IP = sc.getInetAddress().toString();
							Servidor.userLst.put(auxuser.nombre,auxuser);
							Servidor.flujoLst.put(auxuser.nombre, new Flujos(fIn, fOut));
							for(String arc : auxuser.archivos) {
								if(!Servidor.fileToUser.containsKey(arc))
									Servidor.fileToUser.put(arc, new TreeSet<String>());
								Servidor.fileToUser.get(arc).add(auxuser.nombre);
							}
						} else {
							Log.debug("Cerrando canal...", sc);
							Usuario exitUser = mc.getUser();
							Servidor.userLst.remove(exitUser.nombre);
							//retiramos sus archivos
							for(String file : exitUser.archivos) 
								if(Servidor.fileToUser.get(file).size()>=0){
									Servidor.fileToUser.get(file).remove(exitUser.nombre);
									if(Servidor.fileToUser.get(file).size()==0) {
										Servidor.fileToUser.remove(file);
									}
								}
							
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
                        String userId = Servidor.fileToUser.get(mf.getFileName()).iterator().next();
                        Log.debug("pedido " + mf.getFileName() + " que pertenece a " + userId, sc);

                        // Mandar mensaje al emisor para que cree el emisor
                        Servidor.flujoLst.get(userId).getFout().writeObject(new MensajeEmitirFichero(mf.getFileName(), usuario, false));
						break;
						
                    case PREPARADO_CS:
                    	
                    	MensajePreparadoCS mp = (MensajePreparadoCS)m;
                    	
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                    	Servidor.flujoLst.get(mp.getUser()).getFout().writeObject(new MensajePreparadoSC(mp.getIP(), mp.getPort(), mp.getFileName()));
					
                        break;
                    
                    case ACTUALIZAR_LISTA:
                    	
                    	MensajeActualizarListaUsuarios ma = (MensajeActualizarListaUsuarios)m;
                    	
                    	Log.debug(ma.idCliente + " ha incorporado a sus archivos " + ma.nombreArchivo, sc);
                    	
                    	//Actualizamos la lista de usuarios
                    	Servidor.userLst.get(ma.idCliente).addFile(ma.nombreArchivo);
                    	
                    	//También actualizamos la lista de dependencia
                    	
                    	if(!Servidor.fileToUser.containsKey(ma.nombreArchivo))
							Servidor.fileToUser.put(ma.nombreArchivo, new TreeSet<String>());
                    	Servidor.fileToUser.get(ma.nombreArchivo).add(usuario);
                    	
                    	//avisamos de que se ha completado con exito
                    	
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
				Usuario exitUser = Servidor.userLst.get(usuario);
				Servidor.userLst.remove(usuario);
				//retiramos sus archivos
				for(String file : exitUser.archivos) 
					if(Servidor.fileToUser.get(file).size()>=0){
						Servidor.fileToUser.get(file).remove(exitUser.nombre);
						if(Servidor.fileToUser.get(file).size()==0) {
							Servidor.fileToUser.remove(file);
						}
					}
				
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
				fOut.flush();
				fOut.close();
				fIn.close();
				sc.close();
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
				
			}
		}
	}

	public ObjectOutputStream getFout() {
		return fOut;
	}

}
