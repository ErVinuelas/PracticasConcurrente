package data;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public String IP;
    public String nombre;
    public int puerto;
    public ArrayList<String> archivos;

    public Usuario(String IP, String nombre, int puerto) {
        this.IP = IP;
        this.nombre = nombre;
        this.puerto = puerto;
        archivos = new ArrayList<String>();
    }

    public void addFile(String nombre) {
        archivos.put(nombre);
    }
}
