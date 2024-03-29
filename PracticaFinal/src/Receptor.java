import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import locks.Lock;
import mensajes.Mensaje;
import mensajes.MensajeArchivo;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;
import mensajes.TipoMensaje;

public class Receptor extends Thread {

	private Lock viaLibre;
	private Socket sc;
	private Cliente cli;

	private ObjectInputStream fIn;
	private ObjectOutputStream fOut;

	public Receptor(String IP, int puerto, Lock viaLibre, Cliente cli) {
		this.viaLibre = viaLibre;
		this.cli = cli;
		try {
			sc = new Socket(IP, puerto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			fOut = new ObjectOutputStream(sc.getOutputStream());

			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, null));

			Log.debug("Esperando confirmacion de canal preparado...", sc);

			fIn = new ObjectInputStream(sc.getInputStream());

			Mensaje m = (Mensaje) fIn.readObject();
			if (m.getTipo() != TipoMensaje.CONEXION) {
				throw new UnsupportedOperationException("Operación no soportada(1).");
			}

			MensajeConexion mc = (MensajeConexion) m;
			if (mc.getMessage() != TipoConexion.ABRIR || !mc.isACK()) {
				throw new UnsupportedOperationException("Operación no soportada.(2)");
			}

			m = (Mensaje) fIn.readObject();
			while (m.getTipo() != TipoMensaje.ARCHIVO) {
				m = (Mensaje) fIn.readObject();
			}

			MensajeArchivo ma = (MensajeArchivo) m;

			System.out.println("Archivo: " + ma.getNombreArchivo());
			System.out.println("Mensaje: " + ma.getMensaje() + "\n");

			// Guardamos el nuevo archivo
			cli.archivos.put(ma.getNombreArchivo(), ma.getMensaje());
			viaLibre.releaseLock(1);

			fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, null));

			m = (Mensaje) fIn.readObject();
			if (m.getTipo() != TipoMensaje.CONEXION || !((MensajeConexion) m).isACK()
					|| ((MensajeConexion) m).getMessage() != TipoConexion.CERRAR) {
				throw new UnsupportedOperationException("Operación no soportada.(3)");
			}

			fOut.flush();
			fOut.close();
			fIn.close();
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
