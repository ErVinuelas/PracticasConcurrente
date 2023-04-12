import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GestionClienteServidor extends Thread implements Runnable{

    protected Socket sc;
    protected PrintWriter salidaServidor;
    protected BufferedReader salidaCliente;
    protected Servidor servidor;
    
    public GestionClienteServidor(Servidor servidor, Socket sc) {
        this.sc = sc;
        this.servidor = servidor;
    }

    public void run() {
    	try {
	        salidaCliente = new BufferedReader(new InputStreamReader(sc.getInputStream()));
	        String mensaje = salidaCliente.readLine();
	        salidaServidor = new PrintWriter(sc.getOutputStream(), true);
	        salidaServidor.println(servidor.getData(mensaje));
	        salidaServidor.flush();
    	}
	    catch(Exception e) {}
    }
    
}
