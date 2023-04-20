import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeArchivo;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;
import mensajes.TipoMensaje;

public class Emisor extends Thread {
	
	private int port;
	private ServerSocket ss;
	private String file;
	private String fileName;
	
	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;
	
	public Emisor(int puerto, String file, String fileName) throws IOException {
		this.port = puerto;
		this.file = file;
		this.fileName = fileName;
	}
	
	public void run() {
		try {
			this.ss = new ServerSocket(port);
			Socket s = ss.accept();
			
			fIn = new ObjectInputStream(s.getInputStream());
			
			Mensaje m  = (Mensaje) fIn.readObject();
			
			if(m.getTipo() != TipoMensaje.CONEXION || m.isACK() || ((MensajeConexion) m).getMessage() != TipoConexion.ABRIR) {
				
			}
			
			Log.debug("Esperando al receptor...", s);
			//Mensaje de confirmación
			fOut = new ObjectOutputStream(s.getOutputStream());
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, null));
			
			//Cambiar constructor para añadir el nombre del archivo
			fOut.writeObject(new MensajeArchivo(file, fileName, false));
			
			m = (Mensaje) fIn.readObject();
	        if(m.getTipo()!=TipoMensaje.CONEXION || ((MensajeConexion) m).isACK() || ((MensajeConexion) m).getMessage()!=TipoConexion.CERRAR){
	           
	        }
	        fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, null));
	        fOut.flush();
	        fOut.close();
	        fIn.close();
	        s.close();
	        ss.close();
		}catch(Exception e) {
			
		}
	}
}
