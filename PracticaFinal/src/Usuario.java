import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.Serializable;

public class Usuario implements Serializable{
    private int id_usr;
    private String IP;
    private int puerto;

    protected PrintWriter fout;
	protected BufferedReader fin;

}
