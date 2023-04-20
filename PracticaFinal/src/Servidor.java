import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.Usuario;

public class Servidor {

	protected static Set<Thread> threads;
	protected static ServerSocket ss;

	public static TreeMap<String, Usuario> userLst = new TreeMap<String, Usuario>();
	public static TreeMap<String, Flujos> flujoLst = new TreeMap<String, Flujos>();

	public static TreeMap<String, Set<String>> fileToUser = new TreeMap<String, Set<String>>();

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
