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
	protected ObjectInputStream fIn;
	protected ObjectOutputStream fOut;

    protected Usuario user;

	public OyenteCliente(Socket sc, Usuario user) {
		this.sc = sc;
		System.out.println("iniciando hilo...");
		try {
			salidaCliente = new ObjectOutputStream(sc.getOutputStream());
			salidaServidor = new ObjectInputStream(sc.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("hilo iniciado");
	}

	public void run() {
		try {
			boolean stop = false;

			while (!stop) {
				Mensaje m = (Mensaje) salidaServidor.readObject();

				switch (m.getTipo()) {  //Distinguimos entre los diferentes tipos de mensajes
					case CONEXION:

						MensajeConexion mc = (MensajeConexion) m;
						if (mc.getMessage() == TipoConexion.ABRIR) {
							System.out.println("Conexion iniciada");
							salidaCliente.writeObject(new MensajeConexion(TipoConexion.ABRIR, true));

                            //Actualizamos la tabla de usuarios
                            Servidor.userLst.put(user, );
						} else {
                            System.out.println("Conexion cerrada");
							salidaCliente.writeObject(new MensajeConexion(TipoConexion.CERRAR, true));
							stop = true;
						}

						break;
					case LISTA:
						break;
					case PEDIR:
						break;
					default:
						System.out.println("Mensaje no reconocido");
				}
			}
			salidaServidor.close();
		} catch (Exception e) {
		}
	}

}
