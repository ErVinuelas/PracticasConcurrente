import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.Usuario;

public class Servidor {

	protected static Set<Thread> threads;
	protected static ServerSocket ss;

    public static TreeMap<String, Usuario> userLst = new TreeMap<String, Usuario>();
    //public TreeMap<String, Flujo> flujoLst; 

    public static TreeMap<String, String> userToFile = new TreeMap<String, String>();
    public static TreeMap<String, String> fileToUser = new TreeMap<String, String>();


	public Servidor(int port) {
		threads = new TreeSet<Thread>();
		try {
			ss = new ServerSocket(port);
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
				OyenteCliente oc = new OyenteCliente(sc, null);
				i++;
				// serv.threads.add(oc);
				oc.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getData(String key) {
		return "you have requested the data for key: " + key;
	}

	public static void main(String[] args) {
		Servidor serv = new Servidor(4200);
		serv.listen();
	}

}
