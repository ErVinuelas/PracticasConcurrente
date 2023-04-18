import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class Cliente {

	protected Socket sc;

	public Cliente() {
	}

	public void interfaz() throws UnknownHostException, IOException {
		String nombre, dir;
		int port;
		Scanner scan = new Scanner(System.in);
		System.out.println("Introduce tu nombre:");
		nombre = scan.nextLine();
		System.out.println("Introduce la direccion del servidor:");
		dir = scan.nextLine();
		System.out.println("Introduce el puerto del servidor:");
		port = scan.nextInt();
		Socket sc = new Socket(dir, port);
		Log.debug("Se inicia socket", sc);
		OyenteServidor hilo = new OyenteServidor(sc);
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
			oos.writeObject(new MensajeConexion(TipoConexion.CERRAR, false));
			break;
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		Cliente cli = new Cliente();
		cli.interfaz();
	}

}
