import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OyenteServidor extends Thread implements Runnable {

	protected Servidor serv;
	protected ServerSocket ss;

	public OyenteServidor(Servidor serv) {
		super();
		this.serv = serv;
		try {
			ss = new ServerSocket(9999);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			Socket sc;
			try {
				sc = ss.accept();
				GestionClienteServidor oc = new GestionClienteServidor(serv, sc);
				//serv.threads.add(oc);
				oc.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
