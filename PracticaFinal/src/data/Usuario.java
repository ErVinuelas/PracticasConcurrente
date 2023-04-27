package data;

import java.io.Serializable;
import java.util.ArrayList;
import locks.Lock;

import locks.LockTicketNoMaxSize;
public class Usuario implements Serializable {

	private static final long serialVersionUID = 7531143353322080241L;
	public String IP;
	public String nombre;
	private int puerto;
	public ArrayList<String> archivos;
	private Lock port;

	public Usuario(String nombre, String IP, int puerto) {
		this.IP = IP;
		this.nombre = nombre;
		this.puerto = puerto;
		port = new LockTicketNoMaxSize();
		archivos = new ArrayList<String>();
	}
	
	public int getNextPort() {
		try {
			port.takeLock(0);
			this.puerto++;
			port.releaseLock(0);
			return this.puerto;
		}catch(Exception e) {
			return -1;
		}
	}

	public void addFile(String nombre) {
		archivos.add(nombre);
	}

	public String toString() {
		String out = "\tNombre: " + nombre + "\n\tIP: " + IP + "\n\tPuerto: " + Integer.toString(puerto)
				+ "\n\tNum archivos: " + archivos.size();
		out = out + "\n\tArchivos:\n";
		for (String a : archivos) {
			out = out + "\t\t" + a + "\n";
		}
		return out;
	}

}
