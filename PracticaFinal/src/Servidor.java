import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Servidor {

	protected Set<Thread> threads;
	protected ServerSocket ss;

    //Son estáticos para que podamos acceder a ellos desde las clases de oyente.
    public static TreeMap<String, Usuario> userLst;
    public static TreeMap<String, Flujos> flujoList;

	public Servidor() {
		threads = new TreeSet<Thread>();
		try {
			ss = new ServerSocket(1234);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listen() {
		int i = 0;
		while (true) {
			Socket sc;
			try {
				sc = ss.accept();
				OyenteCliente oc = new OyenteCliente(sc);
				i++;
				// serv.threads.add(oc);
				oc.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getData(String key) {
		return "you have requested the data for key: " + key;
	}

	public static void main(String[] args) {
		Servidor serv = new Servidor();
		serv.listen();
	}

}
