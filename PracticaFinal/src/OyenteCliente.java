import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class OyenteCliente extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream salidaServidor;
	protected volatile ObjectOutputStream salidaCliente;

	public OyenteCliente(Socket sc) {
		this.sc = sc;
		Log.debug("iniciando oyente", sc);
		try {
			salidaCliente = new ObjectOutputStream(sc.getOutputStream());
			salidaServidor = new ObjectInputStream(sc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.debug("oyente iniciado", sc);
	}

	public ObjectOutputStream getFout() {
		return salidaCliente;
	}

	public void run() {
		try {
			boolean stop = false;
			while (!stop) {
				Mensaje m = (Mensaje) salidaServidor.readObject();
				Log.debug("mensaje recibido de tipo " + m.getTipo().toString(), sc);
				switch (m.getTipo()) {
					case CONEXION:
						MensajeConexion mc = (MensajeConexion) m;
						if (mc.getMessage() == TipoConexion.ABRIR) {
							Log.debug("Canal preparado", sc);
							salidaCliente.writeObject(new MensajeConexion(TipoConexion.ABRIR, true));
						} else {
							Log.debug("Cerrando canal...", sc);
							salidaCliente.writeObject(new MensajeConexion(TipoConexion.CERRAR, true));
							stop = true;
						}
						break;
					case PEDIR_LISTA:
						break;
					case PEDIR_FICHERO:
						break;
					case EMITIR_FICHERO:
						break;
					default:
						Log.error("Mensaje no reconocido", sc);
				}
			}
			Log.debug("Canal Cerrado", sc);
			salidaServidor.close();
		} catch (Exception e) {
			Log.error("error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				salidaCliente.writeObject(new MensajeConexion(TipoConexion.CERRAR, false));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

}
