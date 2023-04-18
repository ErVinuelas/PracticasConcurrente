import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String IP;
    private String nombre;
    private int puerto;

    public Usuario(String IP, String nombre, int puerto) {
        this.IP = IP;
        this.nombre = nombre;
        this.puerto = puerto;
    }
}
