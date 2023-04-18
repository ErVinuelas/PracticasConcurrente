import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import data.Usuario;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class Cliente {

	protected Socket sc;
	protected PrintWriter fout;
	protected BufferedReader fin;
	protected Usuario yo;
	protected HashMap<String, String> archivos;

	public Cliente() {
	}

	public void interfaz() throws UnknownHostException, IOException {
		String nombre, dir;
		int port;
		Scanner scan = new Scanner(System.in);

		System.out.println("Introduce tu nombre:");
		nombre = scan.nextLine();
		yo=new Usuario(nombre, "localhost", 1000);

		System.out.println("Quieres compartir algun archibo? (s/n)");
		String respuesta = scan.nextLine();
		while(respuesta.equals("s")){
			System.out.println("Introduce el nombre del archivo:");
			String archivo = scan.nextLine();
			yo.addFile(archivo);
			System.out.println("Introduce la ruta del archivo:");
			String ruta = scan.nextLine();
			archivos.put(archivo, ruta);
			System.out.println("Quieres compartir otro archivo? (s/n)");
			respuesta = scan.nextLine();
		}

		System.out.println("Introduce la direccion del servidor:");
		dir = scan.nextLine();

		System.out.println("Introduce el puerto del servidor:");
		port = scan.nextInt();

		Socket sc = new Socket(dir, port);
		Log.debug("Se inicia socket", sc);
		OyenteServidor hilo = new OyenteServidor(sc, yo);
		hilo.start();
		ObjectOutputStream oos = hilo.getFout();
		System.out.println("--------------------");
		while (true) {
			System.out.println("Introduce el nombre del archivo que quieres descargar:");
			String archivo = scan.nextLine();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			oos.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, yo));
			break;
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		Cliente cli = new Cliente();
		cli.interfaz();
	}

}
