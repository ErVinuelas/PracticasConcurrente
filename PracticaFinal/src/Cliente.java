import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

	protected Socket sc;
	protected PrintWriter salidaCliente;
	protected BufferedReader salidaServidor;

	public Cliente() {

	}

	/*
	 * public String ask(String dir, int port, String file) {
	 * try {
	 * sc = new Socket(dir, port);
	 * new OyenteCliente(sc).start();
	 * return "";
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * }
	 * return "Error de conexion";
	 * }
	 */

	public static void main(String[] args) throws UnknownHostException, IOException {
		Cliente cli = new Cliente();
		Scanner scan = new Scanner(System.in);
		System.out.println("Introduce tu nombre:");
		String nombre = scan.nextLine();
		System.out.println("iniciando socket...");
		Socket sc = new Socket("localhost", 1234);
		System.out.println("socket iniciado");
		new OyenteServidor(sc).start();
		while(true);
	}

}
