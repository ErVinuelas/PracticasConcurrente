import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

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
	public HashMap<String, fileManager> archivos;

	// Clase que guarda la informacion del sistema de lectura escritura para cada
	// archivo
	public class fileManager {
		private Semaphore testigo;
		private Semaphore reader;
		private Semaphore writer;

		private int numberReaders;
		private int delayedReaders;
		private int numberWriters;
		private int delayedWriters;

		private String archivo;

		public fileManager(String archivo) {
			this.archivo = archivo;
			numberReaders = 0;
			delayedReaders = 0;
			numberWriters = 0;
			delayedWriters = 0;

			testigo = new Semaphore(1);
			reader = new Semaphore(0);
			writer = new Semaphore(0);
		}

		public void write(String val) {
			try {	testigo.acquire();	} catch (InterruptedException e) {	e.printStackTrace();	}

			if (numberReaders > 0 || numberWriters > 0) {
				delayedWriters++;
				testigo.release();
				try {	writer.acquire(); // Paso de testigo
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			numberWriters++; // Tengo el mutex testigo
			testigo.release(); // Liberamos mutex

			archivo = val;

			try {
				testigo.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			numberWriters--;
			if (delayedWriters > 0) {
				delayedReaders--;
				reader.release(); // Paso del testigo a un lector
			} else {
				testigo.release(); // Soltamos el mutex
			}
		}

		public String read() {
			String res;
			try {
				testigo.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (numberWriters > 0) {

				delayedReaders = delayedReaders + 1;
				testigo.release(); // Paso testigo E

				try {
					reader.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			numberReaders++;
			if (delayedReaders > 0) {
				delayedReaders = delayedReaders - 1;
				reader.release();
			} // Si hay readers esperando, despierto en cadena
			else {
				testigo.release();
			} // Sino, libero el mutex

			res = archivo;

			try {
				testigo.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			numberReaders--;
			if (numberReaders == 0 && delayedWriters > 0) {
				delayedWriters--;
				writer.release(); // Paso testigo
			} else {
				testigo.release();
			}
			return res;
		}

	}

	// Información del usuario que hay asociado al cliente.
	protected Usuario yo;

	// Flujos asociados al cliente
	protected ObjectOutputStream fOut;

	// Proceso OyenteServidor asociado
	protected OyenteServidor hilo;

	// Entrada de usuario
	protected Scanner scan;

	// Semáforo que nos permite regular la interfaz gráfica.
	protected Semaphore viaLibre;

	public Cliente() {
		scan = new Scanner(System.in);
		archivos = new HashMap<String, fileManager>();
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

			Log.console("Introduce el contenido del archivo");
			String ruta = scan.nextLine();
			archivos.put(archivo, new fileManager(ruta));

			Log.console("Quieres compartir otro archivo? (s/n)");
			respuesta = scan.nextLine();
		}

		Log.console("Introduce la direccion del servidor");
		dir = scan.nextLine();

		Log.console("Introduce el puerto del servidor");
		port = scan.nextInt();

		// Inicialización del resto de elementos del cliente.
		sc = new Socket(dir, port);
		hilo = new OyenteServidor(sc, yo, viaLibre, this);
		hilo.start();
		viaLibre.acquire();
		fOut = hilo.getFout();
	}

	public void loop() throws IOException, InterruptedException {
		boolean sigue = true;
		// Menú interactivo con el usuario.
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
