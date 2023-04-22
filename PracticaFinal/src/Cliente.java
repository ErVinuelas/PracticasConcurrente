import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import data.Usuario;
import mensajes.MensajeConexion;
import mensajes.MensajePedirFichero;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;

public class Cliente {

	protected Socket sc;

	// Guarda los archivos y su dirección asociada, nosotros almacenamos
	// directamente el string que hay
	// asociado a un nombre de fichero.
	public HashMap<String, String> archivos;

	// Información del usuario que hay asociado al cliente.
	protected Usuario yo;

	// Flujos asociados al cliente
	protected ObjectOutputStream fOut;
	// TODO: Por que no hay fIn? Hay alguna razón o es un error?

	// Proceso OyenteServidor asociado
	protected OyenteServidor hilo;

	// Entrada de usuario
	protected Scanner scan;

	// Semáforo que nos permite regular el flujo de archivos.
	protected Semaphore viaLibre;

	public Cliente() {
		scan = new Scanner(System.in);
		archivos = new HashMap<String, String>();
		viaLibre = new Semaphore(0);
	}

	// Inicializamos el cliente
	public void init() throws UnknownHostException, IOException, InterruptedException {
		String nombre, dir;
		int port;

		// Menú interactivo inicial con el usuario
		Log.console("Introduce tu nombre");
		nombre = scan.nextLine();

		yo = new Usuario(nombre, "localhost", 5000);

		Log.console("Quieres compartir algun archibo? (s/n)");
		String respuesta = scan.nextLine();

		while (respuesta.equals("s")) {
			Log.console("Introduce el nombre del archivo");
			String archivo = scan.nextLine();
			yo.addFile(archivo);
			
			Log.console("Introduce la ruta del archivo");
			String ruta = scan.nextLine();
			archivos.put(archivo, ruta);
			
			Log.console("Quieres compartir otro archivo? (s/n)");
			respuesta = scan.nextLine();
		}

		Log.console("Introduce la direccion del servidor");
		dir = scan.nextLine();

		Log.console("Introduce el puerto del servidor");
		port = scan.nextInt();

		//Inicialización del resto de elementos del cliente.
		sc = new Socket(dir, port);
		hilo = new OyenteServidor(sc, yo, viaLibre, this);
		hilo.start();
		viaLibre.acquire();
		fOut = hilo.getFout();
	}

	public void loop() throws IOException, InterruptedException {
		boolean sigue = true;
		//Menú interactivo con el usuario.
		while (sigue) {
			Log.console(
					"Que quieres hacer?\n\t 1. Descargar un archivo\n\t 2. Ver Usuarios conectados\n\t 3. Salir \n");
			int respuesta = scan.nextInt();
			scan.nextLine();
			switch (respuesta) {
			case 1:
				Log.console("Introduce el nombre del archivo que quieres descargar");
				String archibo = scan.nextLine();
				fOut.writeObject(new MensajePedirFichero(archibo, false));
				fOut.flush();
				fOut.reset();
				viaLibre.acquire();
				break;
			case 2:
				fOut.writeObject(new MensajeSolicListaUsuar(null, false));
				fOut.flush();
				fOut.reset();
				viaLibre.acquire();
				break;
			case 3:
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, yo));
				fOut.flush();
				fOut.reset();
				sigue = false;
				break;
			default:
				System.out.println("Opcion no valida");
				break;
			}

		}
	}

	public void interfaz() throws UnknownHostException, IOException, InterruptedException {
		this.init();
		System.out.println("--------------------");
		this.loop();
		hilo.join();
	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Cliente cli = new Cliente();
		cli.interfaz();
	}

}
