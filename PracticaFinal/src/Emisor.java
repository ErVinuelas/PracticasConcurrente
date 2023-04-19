import java.io.IOException;
import java.net.ServerSocket;

public class Emisor {
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
	}
}
