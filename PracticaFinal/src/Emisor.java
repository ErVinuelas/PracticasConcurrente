import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeArchivo;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;
import mensajes.TipoMensaje;

public class Emisor extends Thread {

	private int port;
	private ServerSocket ss;
	private String file;
	private String fileName;

	protected ObjectInputStream fIn;
	protected volatile ObjectOutputStream fOut;

	public Emisor(int puerto, String file, String fileName) throws IOException {
		this.port = puerto;
		this.file = file;
		this.fileName = fileName;
	}

	public void run() {
		try {
			this.ss = new ServerSocket(port);
			Socket s = ss.accept();

			fIn = new ObjectInputStream(s.getInputStream());

			Log.debug("Esperando al receptor...", s);

			Mensaje m = (Mensaje) fIn.readObject();
			if (m.getTipo() != TipoMensaje.CONEXION || m.isACK()
					|| ((MensajeConexion) m).getMessage() != TipoConexion.ABRIR) {
				throw new UnsupportedOperationException("Operacion no soportada.(1)");
			}

			// Mensaje de confirmación
			fOut = new ObjectOutputStream(s.getOutputStream());
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, true, null));

			// Mensaje en el que enviamos el archivo
			fOut.writeObject(new MensajeArchivo(file, fileName, false));

			m = (Mensaje) fIn.readObject();
			if (m.getTipo() != TipoMensaje.CONEXION || ((MensajeConexion) m).isACK()
					|| ((MensajeConexion) m).getMessage() != TipoConexion.CERRAR) {
				throw new UnsupportedOperationException("Operacion no soportada.(2)");
			}

			// Mensaje de confirmación de cerrar el canal
			fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, null));
			fOut.flush();

			fOut.close();
			fIn.close();
			s.close();
			ss.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
