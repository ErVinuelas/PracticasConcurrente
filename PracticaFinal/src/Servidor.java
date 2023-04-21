import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.Usuario;

public class Servidor {

	protected ServerSocket ss;

	public TreeMap<String, Usuario> userLst = new TreeMap<String, Usuario>();
	public TreeMap<String, Flujos> flujoLst = new TreeMap<String, Flujos>();

	public TreeMap<String, Set<String>> fileToUser = new TreeMap<String, Set<String>>();

	public Servidor(int port) {
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
				OyenteCliente oc = new OyenteCliente(sc, this);
				i++;
				oc.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Servidor serv = new Servidor(4200);
		serv.listen();
	}

}
