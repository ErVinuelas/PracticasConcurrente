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
							serv.userLst.put(auxuser.nombre,auxuser);
							
							//Tabla flujo
							serv.flujoLst.put(auxuser.nombre, new Flujos(fIn, fOut));
							
							//Tabla de fileToUser
							for(String arc : auxuser.archivos) {
								serv.fileToUser.add(arc, auxuser.nombre);
							}
						} else {	//Cerrar conexión
							Log.debug("Cerrando canal...", sc);
							Usuario exitUser = mc.getUser();
							
							serv.userLst.remove(exitUser.nombre);
							
							//Retiramos sus archivos
							for(String file : exitUser.archivos) 
								serv.fileToUser.remove(file, exitUser.nombre);
							
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
	                        fOut.writeObject(new MensajeSolicListaUsuar(serv.userLst.getTabla(), true));
							fOut.flush();
							fOut.reset();
						}
						break;
						
					case PEDIR_FICHERO:
						
						MensajePedirFichero mf = (MensajePedirFichero) m;
						
                        // Decidir quien manda fichero(emisor)
						Log.debug("pedido " + mf.getFileName(), sc);
                        String userId = serv.fileToUser.getFirst(mf.getFileName());
                        
                        Log.debug("pedido " + mf.getFileName() + " que pertenece a " + userId, sc);

                        // Mandar mensaje al emisor para que cree el emisor
                        
                        ObjectOutputStream fOutAux = serv.flujoLst.get(userId).getFout();
                        
                        fOutAux.writeObject(new MensajeEmitirFichero(mf.getFileName(), usuario, false));
						fOutAux.flush();
						fOutAux.reset();
						break;
						
                    case PREPARADO_CS:
                    	
                    	MensajePreparadoCS mp = (MensajePreparadoCS)m;
                    	
                        ObjectOutputStream fOutAuxPrep = serv.flujoLst.get(mp.getUser()).getFout();
                        //Mandamos mensaje de preparado con puerto e ip del emisor
                    	fOutAuxPrep.writeObject(new MensajePreparadoSC(mp.getIP(), mp.getPort(), mp.getFileName()));
						fOutAuxPrep.flush();
						fOutAuxPrep.reset();
					
                        break;
                    
                    case ACTUALIZAR_LISTA:
                    	
                    	MensajeActualizarListaUsuarios ma = (MensajeActualizarListaUsuarios)m;
                    	
                    	Log.debug(ma.idCliente + " ha incorporado a sus archivos " + ma.nombreArchivo, sc);
                    	
                    	//Actualizamos la lista de usuarios
                    	serv.userLst.get(ma.idCliente).addFile(ma.nombreArchivo);
                    	
                    	//También actualizamos la lista de dependencia
                    	
                    	serv.fileToUser.add(ma.nombreArchivo, usuario);
                    	
                    	//Avisamos de que se ha completado con exito
                    	
                    	fOut.writeObject(new MensajeActualizarListaUsuarios(ma.idCliente, ma.nombreArchivo, true));
						fOut.flush();
						fOut.reset();
						break;
                        
                    default:
						Log.error("Mensaje no reconocido: " + m.getTipo().toString(), sc);
				}
			}
			
			Log.debug("Canal Cerrado", sc);
			fIn.close();
			
		} catch (Exception e) {
			Log.error("error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				
				Usuario exitUser = serv.userLst.get(usuario);
				serv.userLst.remove(usuario);
				
				//Retiramos sus archivos
				for(String file : exitUser.archivos) {
					serv.fileToUser.remove(file, exitUser.nombre);
				}
				
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
				fOut.flush();
				fOut.close();
				fIn.close();
				
				//Retiramos la entrada de el flujo para este cliente
				serv.flujoLst.remove(usuario);
				
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
