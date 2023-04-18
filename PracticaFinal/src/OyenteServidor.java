import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.TipoConexion;

public class OyenteServidor extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream salidaCliente;
	protected ObjectOutputStream salidaServidor;
	// protected Servidor servidor;
	protected int id;

	public OyenteServidor(Socket sc) {
		this.sc = sc;
		// this.servidor = servidor;
		Log.debug("iniciando oyente", sc);
		try {
			salidaCliente = new ObjectInputStream(sc.getInputStream());
			salidaServidor = new ObjectOutputStream(sc.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.debug("oyente iniciado", sc);
		// this.id = id;
	}

	public void run() {
		try {
			Log.debug("Canal preparado", sc);
			salidaServidor.writeObject(new MensajeConexion(TipoConexion.ABRIR, false));
			while (true) {
				Mensaje m = (Mensaje) salidaCliente.readObject();
				switch (m.getTipo()) {
					case CONEXION:
						break;
					case LISTA:
						break;
					case PEDIR:
						break;
					default:
				}
			}
		} catch (Exception e) {
		}
	}

	public int hashCode() {
		return id;
	}

}
