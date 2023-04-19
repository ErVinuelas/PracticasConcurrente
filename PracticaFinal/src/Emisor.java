import java.io.IOException;
import java.net.ServerSocket;

public class Emisor extends Thread {
	private int port;
	private String IP;
	
	private ServerSocket ss;
	private String file;
	
	public Emisor(int puerto, String IP, String file) {
		this.port = puerto;
		this.IP = IP;
		this.file = file;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fOut = new ObjetOutputStream(ss.getOutputStream());
		fIn = new ObjectInputStream(ss.getInputStream());
	}
	
	public run 
}
