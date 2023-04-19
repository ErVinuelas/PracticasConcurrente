import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

import mensajes.Mensaje;
import mensajes.MensajeArchivo;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;
import mensajes.TipoMensaje;

public class Receptor extends Thread {

	private String IP;
	private int puerto;
	
	private Semaphore viaLibre;
	private Socket sc;
	
	private ObjectInputStream fIn;
	private ObjectOutputStream fOut;

    public Receptor(String IP, int puerto, Semaphore viaLibre){
        this.IP = IP;
        this.puerto = puerto;
        this.viaLibre = viaLibre;
        try {
			sc = new Socket(IP, puerto);
			fIn = new ObjectInputStream(sc.getInputStream());
	        fOut = new ObjectOutputStream(sc.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void run(){
    	try {
	        fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, null));
			Log.debug("Esperando confirmacion de canal preparado...", sc);
	        Mensaje m = (Mensaje) fIn.readObject();
	        if(m.getTipo()!=TipoMensaje.CONEXION){
	            
	        }
	        MensajeConexion mc = (MensajeConexion) m;
	        if (mc.getMessage() != TipoConexion.ABRIR || !mc.isACK()) {
	            
	        }
	        m = (Mensaje) fIn.readObject();
	        while(m.getTipo()!=TipoMensaje.ARCHIVO){
	            m = (Mensaje) fIn.readObject();
	        }
	        MensajeArchivo ma = (MensajeArchivo) m;
	        System.out.println("Archivo: " + ma.getNombreArchivo());
	        System.out.println("Mensaje: " + ma.getMensaje() + "\n");
	        viaLibre.release();
	        fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, null));
	        m = (Mensaje) fIn.readObject();
	        if(m.getTipo()!=TipoMensaje.CONEXION || !((MensajeConexion) m).isACK() || ((MensajeConexion) m).getMessage()!=TipoConexion.CERRAR){
	           
	        }
	        sc.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

}
