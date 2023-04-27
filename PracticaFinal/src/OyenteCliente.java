import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.TreeSet;

import data.Usuario;
import data.FlujosConcurrentes;
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

public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;

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
			ObjectInputStream fIn = new ObjectInputStream(sc.getInputStream());
			Mensaje m1 = (Mensaje) fIn.readObject();

			if(m1.getTipo()!=TipoMensaje.CONEXION){
				//Cerramos el canal
				Log.error("Se esperaba mensaje de tipo CONEXION pero se recibio mensaje de tipo "+ m1.getTipo().toString(), sc);
				fIn.close();
				sc.close();
				return;
			}

			MensajeConexion mc1 = (MensajeConexion) m1;

			if (mc1.getMessage() != TipoConexion.ABRIR){
				//Cerramos el canal
				Log.error("Se esperaba mensaje de tipo ABRIR CONEXION pero se recibio mensaje de tipo CERRAR", sc);
				fIn.close();
				sc.close();
				return;
			}

			//Creamos el canal de salida
			ObjectOutputStream fOut = new ObjectOutputStream(sc.getOutputStream());
			Log.debug("Canal preparado", sc);
			//Mandamos confirmación
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, null));
			
			// Creamos variables auxiliares para almacenar los datos
			usuario = mc1.getUser().nombre;
			Usuario auxuser = mc1.getUser();
			auxuser.IP = sc.getInetAddress().toString();
			
			//Modificamos las tablas de manera concurrente
			
			//Tabla usuarios
			serv.userLst.put(auxuser.nombre,auxuser);
			
			//Tabla flujo
			serv.flujoLst.put(auxuser.nombre, new FlujosConcurrentes(fIn, fOut));
			
			//Tabla de fileToUser
			for(String arc : auxuser.archivos) {
				serv.fileToUser.add(arc, auxuser.nombre);
			}

			while (!stop) {
				Log.debug("Esperando mensaje...", sc);
				Mensaje m = (Mensaje) serv.flujoLst.get(usuario).readObject();
				Log.debug("mensaje recibido de tipo " + m.getTipo().toString(), sc);

				switch (m.getTipo()) {
					case CONEXION:

						MensajeConexion mc = (MensajeConexion) m;
						
						if (mc.getMessage() == TipoConexion.ABRIR) {	//Abrir conexión
							Log.error("Se ha recibido un segundo mensaje de tipo ABRIR CONEXION", sc);
							throw new Exception("Se ha recibido un segundo mensaje de tipo ABRIR CONEXION");
						} else {	//Cerrar conexión
							Log.debug("Cerrando canal...", sc);
							Usuario exitUser = mc.getUser();
							
							serv.userLst.remove(exitUser.nombre);
							
							//Retiramos sus archivos
							for(String file : exitUser.archivos) 
								serv.fileToUser.remove(file, exitUser.nombre);
							
							serv.flujoLst.get(usuario).writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
							
							//Cerramos los canales
							serv.flujoLst.get(usuario).close();
							serv.flujoLst.remove(usuario);
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
	                        serv.flujoLst.get(usuario).writeObject(new MensajeSolicListaUsuar(serv.userLst.getTabla(), true));
						}
						break;
						
					case PEDIR_FICHERO:
						
						MensajePedirFichero mf = (MensajePedirFichero) m;
						
                        // Decidir quien manda fichero(emisor)
						Log.debug("pedido " + mf.getFileName(), sc);
                        String userId = serv.fileToUser.getFirst(mf.getFileName());
                        
                        Log.debug("pedido " + mf.getFileName() + " que pertenece a " + userId, sc);

                        // Mandar mensaje al emisor para que cree el emisor
                        
                        serv.flujoLst.get(userId).writeObject(new MensajeEmitirFichero(mf.getFileName(), usuario, false));
						Log.debug("peticion de archivo enviado a "+userId, sc);
						break;
						
                    case PREPARADO_CS:
                    	
                    	MensajePreparadoCS mp = (MensajePreparadoCS)m;
                    	
                        serv.flujoLst.get(mp.getUser()).writeObject(new MensajePreparadoSC(mp.getIP(), mp.getPort(), mp.getFileName()));
					
                        break;
                    
                    case ACTUALIZAR_LISTA:
                    	
                    	MensajeActualizarListaUsuarios ma = (MensajeActualizarListaUsuarios)m;
                    	
                    	Log.debug(ma.idCliente + " ha incorporado a sus archivos " + ma.nombreArchivo, sc);
                    	
                    	//Actualizamos la lista de usuarios
                    	serv.userLst.get(ma.idCliente).addFile(ma.nombreArchivo);
                    	
                    	//También actualizamos la lista de dependencia
                    	
                    	serv.fileToUser.add(ma.nombreArchivo, usuario);
                    	
                    	//Avisamos de que se ha completado con exito
                    	
                    	serv.flujoLst.get(usuario).writeObject(new MensajeActualizarListaUsuarios(ma.idCliente, ma.nombreArchivo, true));
						break;
                        
                    default:
						Log.error("Mensaje no reconocido: " + m.getTipo().toString(), sc);
				}
			}
			
			Log.debug("Canal Cerrado", sc);
			
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
				
				serv.flujoLst.get(usuario).writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
				serv.flujoLst.get(usuario).close();
				
				//Retiramos la entrada de el flujo para este cliente
				serv.flujoLst.remove(usuario);
				
				sc.close();
				
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
				
			}
		}
	}

}
