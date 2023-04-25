import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeMap;

import data.DiccionarioConcurrente;
import data.Usuario;

public class Servidor {

	protected ServerSocket ss;

	//Tenemos tres tablas de datos que tenemos que proteger mediante un monitor
	//Hay una cola por cada tabla
	
	//Tablas
	public volatile DiccionarioConcurrente<String, Usuario> userLst;
	public volatile DiccionarioConcurrente<String, Flujos> flujoLst;

	public volatile DiccionarioConcurrente<String, Set<String>> fileToUser;

	public Servidor(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Creamos la tablas concurrentes
		this.userLst = new DiccionarioConcurrente<String ,Usuario>(new TreeMap<String, Usuario>());
		this.flujoLst = new DiccionarioConcurrente<String, Flujos>(new TreeMap<String, Flujos>());
		this.fileToUser = new DiccionarioConcurrente<String, Set<String>>(new TreeMap<String, Set<String>>());
	}
	
	public static void main(String[] args) {
		Servidor serv = new Servidor(5200);
		serv.listen();
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

}
