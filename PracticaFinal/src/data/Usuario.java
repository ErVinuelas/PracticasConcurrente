package data;

import java.io.Serializable;
import java.util.ArrayList;

import locks.LockNoSize;
import locks.LockTicketNoSize;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 7531143353322080241L;
	public String IP;
	public String nombre;
	private int puerto;
	public ArrayList<String> archivos;
	private LockNoSize port;

	public Usuario(String nombre, String IP, int puerto) {
		this.IP = IP;
		this.nombre = nombre;
		this.puerto = puerto;
		port = new LockTicketNoSize();
		archivos = new ArrayList<String>();
	}
	
	public int getNextPort() {
		try {
			port.takeLock();
			this.puerto++;
			port.releaseLock();
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
