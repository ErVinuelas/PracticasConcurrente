package data;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 7531143353322080241L;
	public String IP;
	public String nombre;
	public int puerto;
	public ArrayList<String> archivos;

	public Usuario(String nombre, String IP, int puerto) {
		this.IP = IP;
		this.nombre = nombre;
		this.puerto = puerto;
		archivos = new ArrayList<String>();
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
