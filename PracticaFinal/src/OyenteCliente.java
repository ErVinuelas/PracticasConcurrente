import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

//TODO arreglar los booleanos de los mensajes,están mezclados
public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;

	protected Servidor serv;
	protected String usuario;

	public OyenteCliente(Socket sc, Servidor serv) {
		this.sc = sc;
		this.serv = serv;

		Log.debug("iniciando oyente", sc);
	}

	public void run() {
		try {
			boolean stop = false;
			//Creamos el canal de comunicación
			fIn = new ObjectInputStream(sc.getInputStream());

			while (!stop) {
				
				Mensaje m = (Mensaje) fIn.readObject();
				Log.debug("mensaje recibido de tipo " + m.getTipo().toString(), sc);

				switch (m.getTipo()) {
					case CONEXION:

						MensajeConexion mc = (MensajeConexion) m;
						
						if (mc.getMessage() == TipoConexion.ABRIR) {	//Abrir conexión
							//Creamos el canal de salida
							fOut = new ObjectOutputStream(sc.getOutputStream());
							Log.debug("Canal preparado", sc);
							//Mandamos confirmación
							fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, null));
							fOut.flush();
							fOut.reset();
							
							// Creamos variables auxiliares para almacenar los datos
							usuario = mc.getUser().nombre;
							Usuario auxuser = mc.getUser();
							auxuser.IP = sc.getInetAddress().toString();
							
							//Modificamos las tablas de manera concurrente
							
							//Tabla usuarios
							serv.solicitarEscrituraUser();
							serv.userLst.put(auxuser.nombre,auxuser);
							serv.terminarEscrituraUser();
							
							//Tabla flujo
							serv.solicitarEscrituraFlujo();
							serv.flujoLst.put(auxuser.nombre, new Flujos(fIn, fOut));
							serv.terminarEscrituraFlujo();
							
							//Tabla de fileToUser
							for(String arc : auxuser.archivos) {
								
								serv.solicitarEscrituraFileToUser();
								if(!serv.fileToUser.containsKey(arc))
									serv.fileToUser.put(arc, new TreeSet<String>());
								serv.fileToUser.get(arc).add(auxuser.nombre);
								serv.terminarEscrituraFileToUser();
							}
						} else {	//Cerrar conexión
							Log.debug("Cerrando canal...", sc);
							Usuario exitUser = mc.getUser();
							
							serv.solicitarEscrituraUser();
							serv.userLst.remove(exitUser.nombre);
							serv.terminarEscrituraUser();
							
							//Retiramos sus archivos
							for(String file : exitUser.archivos) 
								if(serv.fileToUser.get(file).size()>=0){
									serv.solicitarEscrituraFileToUser();
									serv.fileToUser.get(file).remove(exitUser.nombre);
									serv.terminarEscrituraFileToUser();
									
									if(serv.fileToUser.get(file).size()==0) {
										serv.solicitarEscrituraFileToUser();
										serv.fileToUser.remove(file);
										serv.terminarEscrituraFileToUser();
									}
								}
							
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
							fOut.flush();
							
							//Cerramos los canales
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
	                        /*Log.debug("Usuarios:", sc);
	                        for (Usuario u : serv.userLst.values()) {
								Log.debug("Usuario:\n" + u.toString(),sc);
							}*/
	                        fOut.writeObject(new MensajeSolicListaUsuar(serv.userLst, true));
							fOut.flush();
							fOut.reset();
						}
						break;
						
					case PEDIR_FICHERO:
						
						MensajePedirFichero mf = (MensajePedirFichero) m;
						
                        // Decidir quien manda fichero(emisor)
						serv.solicitarLecturaFileToUser();
                        String userId = serv.fileToUser.get(mf.getFileName()).iterator().next();
                        serv.terminarLecturaFileToUser();
                        
                        Log.debug("pedido " + mf.getFileName() + " que pertenece a " + userId, sc);

                        // Mandar mensaje al emisor para que cree el emisor
                        
                        serv.solicitarLecturaFlujo();
                        ObjectOutputStream fOutAux = serv.flujoLst.get(userId).getFout();
                        serv.terminarLecturaFlujo();
                        
                        fOutAux.writeObject(new MensajeEmitirFichero(mf.getFileName(), usuario, false));
						fOutAux.flush();
						fOutAux.reset();
						break;
						
                    case PREPARADO_CS:
                    	
                    	MensajePreparadoCS mp = (MensajePreparadoCS)m;
                    	
                    	
                        serv.solicitarLecturaFlujo();
                        ObjectOutputStream fOutAuxPrep = serv.flujoLst.get(mp.getUser()).getFout();
                        serv.terminarLecturaFlujo();
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                    	fOutAuxPrep.writeObject(new MensajePreparadoSC(mp.getIP(), mp.getPort(), mp.getFileName()));
						fOutAuxPrep.flush();
						fOutAuxPrep.reset();
					
                        break;
                    
                    case ACTUALIZAR_LISTA:
                    	
                    	MensajeActualizarListaUsuarios ma = (MensajeActualizarListaUsuarios)m;
                    	
                    	Log.debug(ma.idCliente + " ha incorporado a sus archivos " + ma.nombreArchivo, sc);
                    	
                    	//Actualizamos la lista de usuarios
                    	serv.solicitarEscrituraUser();
                    	serv.userLst.get(ma.idCliente).addFile(ma.nombreArchivo);
                    	serv.terminarEscrituraUser();
                    	
                    	//También actualizamos la lista de dependencia
                    	
                    	serv.solicitarEscrituraFileToUser();
                    	if(!serv.fileToUser.containsKey(ma.nombreArchivo))
							serv.fileToUser.put(ma.nombreArchivo, new TreeSet<String>());
                    	serv.terminarEscrituraFileToUser();
                    	
                    	serv.solicitarEscrituraFileToUser();
                    	serv.fileToUser.get(ma.nombreArchivo).add(usuario);
                    	serv.terminarEscrituraFileToUser();
                    	
                    	//Avisamos de que se ha completado con exito
                    	
                    	fOut.writeObject(new MensajeActualizarListaUsuarios(ma.idCliente, ma.nombreArchivo, true));
						fOut.flush();
						fOut.reset();
                        
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
				
				serv.solicitarEscrituraUser();
				Usuario exitUser = serv.userLst.get(usuario);
				serv.userLst.remove(usuario);
				serv.terminarEscrituraUser();
				
				//Retiramos sus archivos
				for(String file : exitUser.archivos) {
					serv.solicitarEscrituraFileToUser();
				
					if(serv.fileToUser.get(file).size()>=0){
						serv.fileToUser.get(file).remove(exitUser.nombre);
						if(serv.fileToUser.get(file).size()==0) {
							serv.fileToUser.remove(file);
						}
					}
					
					serv.terminarEscrituraFileToUser();
				}
				
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
				fOut.flush();
				fOut.close();
				fIn.close();
				
				//Retiramos la entrada de el flujo para este cliente
				serv.solicitarEscrituraFlujo();
				serv.flujoLst.remove(usuario);
				serv.terminarEscrituraFlujo();
				
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
