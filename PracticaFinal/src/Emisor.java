import java.io.IOException;
import java.net.ServerSocket;

public class Emisor extends Thread {
	
	private int port;
	private String IP;
	private ServerSocket ss;
	private String file;
	private String fileName;
	
	public Emisor(int puerto, String IP, String file, String fileName) {
		this.port = puerto;
		this.IP = IP;
		this.file = file;
		this.fileName = fileName;
		this.ss = new ServerSocket(port);
		
		fOut = new ObjetOutputStream(ss.getOutputStream());
		fIn = new ObjectInputStream(ss.getInputStream());
	}
	
	public void run() {
		Log.debug("Esperando al receptor...", ss);
		Mensaje m  = (Mensaje) fIn.readObject();
		
		while(m.getTipo() != TipoMensaje.CONEXION) {
			m = (Mensaje) fIn.readObject();
		}
		
		MensajeConexion mc = (MensajeConexion) m;
		if(mc.getTipo() != TipoConexion.ABRIR) {
			//Aqui va un error
		}
		
		//Mensaje de confirmación
		fout.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, null));
		//Cambiar constructor para añadir el nombre del archivo
		fOut.writeObject(new MensajeArchivo(file, null, false));
	}
}
