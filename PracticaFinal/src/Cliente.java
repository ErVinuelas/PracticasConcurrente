import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

	protected Socket sc;
	protected PrintWriter salidaCliente;
	protected BufferedReader salidaServidor;
	
	public Cliente() {
		
	}

	public String ask(String dir, int port, String file){
		try {
			sc = new Socket(dir, port);
			salidaCliente = new PrintWriter(sc.getOutputStream(), true);
	        salidaCliente.println(file);
			salidaServidor = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			String mensaje = salidaServidor.readLine();
			salidaCliente.flush();
			return mensaje;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "Error de conexion";
	}
	
	public static void main(String[] args) {
		Cliente cli = new Cliente();
		System.out.println(cli.ask("localhost", 9999, "hola"));
	}

}
