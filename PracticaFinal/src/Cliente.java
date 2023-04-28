import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;

import data.DirectorioConcurrente;
import data.Usuario;
import locks.Lock;
import locks.LockRompeEmpate;
import mensajes.MensajeConexion;
import mensajes.MensajePedirFichero;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;

public class Cliente {

	protected Socket sc;

	// Guarda los archivos y su dirección asociada, nosotros almacenamos
	// directamente el string que hay
	// asociado a un nombre de fichero.
	public DirectorioConcurrente archivos;

	// Información del usuario que hay asociado al cliente.
	protected Usuario yo;

	// Flujos asociados al cliente
	protected ObjectOutputStream fOut;

	// Proceso OyenteServidor asociado
	protected OyenteServidor hilo;

	// Entrada de usuario
	protected Scanner scan;

	// Semáforo que nos permite regular la interfaz gráfica.
	protected Lock viaLibre;

	public Cliente() {
		scan = new Scanner(System.in);
		archivos = new DirectorioConcurrente();
		viaLibre = new LockRompeEmpate(2);
	}

	// para debug
	public Cliente(int id, String nombre, String dir, int port, HashMap<String, String> archivos,
			List<String> archivosPedir, int numPedirUsuarios, boolean ends)
			throws UnknownHostException, IOException, InterruptedException {
		viaLibre = new LockRompeEmpate(2);
		
		//Creamos usuario
		yo = new Usuario(nombre, "localhost", id * 100 + 4000);
		this.archivos = new DirectorioConcurrente();
		for (String s : archivos.keySet()) {
			this.archivos.put(s, archivos.get(s));
			yo.addFile(s);
		}
		
		sc = new Socket(dir, port);
		hilo = new OyenteServidor(sc, yo, viaLibre, this);
		hilo.takeLock();
		hilo.start();
		
		viaLibre.takeLock(0);
		
		fOut = hilo.getFout();
		for (String s : archivosPedir) {
			viaLibre.releaseLock(0);
			hilo.takeLock();
			fOut.writeObject(new MensajePedirFichero(s, false));
			viaLibre.takeLock(0);
			fOut.flush();
			fOut.reset();
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		for (int i = 0; i < numPedirUsuarios; i++) {
			viaLibre.releaseLock(0);
			hilo.takeLock();
			fOut.writeObject(new MensajeSolicListaUsuar(null, false));
			viaLibre.takeLock(0);
			fOut.flush();
			fOut.reset();
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				//
			}
		}
		if (ends)
			fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, yo));
		else
			while (true)
				;
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
			archivos.put(archivo, ruta);

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
		hilo.takeLock();
		hilo.start();
		viaLibre.takeLock(0);
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
				viaLibre.releaseLock(0);
				hilo.takeLock();
				fOut.writeObject(new MensajePedirFichero(archibo, false));
				viaLibre.takeLock(0);
				fOut.flush();
				fOut.reset();

				break;
			case 2:
				viaLibre.releaseLock(0);
				hilo.takeLock();
				fOut.writeObject(new MensajeSolicListaUsuar(null, false));
				viaLibre.takeLock(0);
				fOut.flush();
				fOut.reset();
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
