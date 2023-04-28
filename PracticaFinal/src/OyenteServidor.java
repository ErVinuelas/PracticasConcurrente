import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import data.Usuario;
import locks.Lock;
import mensajes.Mensaje;
import mensajes.MensajeActualizarListaUsuarios;
import mensajes.MensajeConexion;
import mensajes.MensajeEmitirFichero;
import mensajes.MensajePreparadoCS;
import mensajes.MensajePreparadoSC;
import mensajes.MensajeSolicListaUsuar;
import mensajes.TipoConexion;

public class OyenteServidor extends Thread implements Runnable {

	protected Socket sc;
	protected ObjectInputStream fIn;
	protected ObjectOutputStream fOut;

	protected int id;
	protected Usuario user;
	protected Cliente cliente;

	protected Lock viaLibre;

	public OyenteServidor(Socket sc, Usuario user, Lock viaLibre, Cliente cliente) {
		this.sc = sc;
		this.user = user;
		this.viaLibre = viaLibre;
		this.cliente = cliente;
		Log.debug("iniciando oyente", sc);
	}

	public void takeLock() {
		viaLibre.takeLock(1);
	}

	public void run() {
		try {
			boolean sigue = true;
			// Creamos canal de salida
			fOut = new ObjectOutputStream(sc.getOutputStream());
			fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, user));
			fOut.flush();
			fOut.reset();
			Log.debug("Esperando confirmacion de canal preparado...", sc);

			// Creamos canal de entrada
			fIn = new ObjectInputStream(sc.getInputStream());
			MensajeConexion mc1 = (MensajeConexion) fIn.readObject();

			if (!(mc1.getMessage() == TipoConexion.ABRIR && mc1.isACK())) {
				Log.error("Mensaje inesperado al iniciar conexion con el servidor, se cancela comunicación", sc);
				throw new UnsupportedOperationException();
			}
			Log.debug("canal preparado", sc);
			viaLibre.releaseLock(1);

			while (sigue) {
				Log.debug("Esperando mensaje...", sc);
				Mensaje m = (Mensaje) fIn.readObject();
				Log.debug("Mensaje recibido: " + m.getTipo().toString(), sc);
				switch (m.getTipo()) {

				case CONEXION:
					MensajeConexion mc = (MensajeConexion) m;
					if (mc.getMessage() == TipoConexion.CERRAR) {
						if (!mc.isACK()) {
							Log.debug("Cerrando canal...", sc);
							fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, true, user));
							fOut.flush();
							fOut.reset();
						} else {
							Log.debug("Canal cerrado", sc);
						}
						sigue = false;
					}
					break;

				case PEDIR_LISTA:
					MensajeSolicListaUsuar ms = (MensajeSolicListaUsuar) m;
					if (!ms.isACK()) {
						Log.error("Error al solicitar lista de usuarios: yo no soy un servidor", sc);
					} else {
						Map<String, Usuario> usrs;
						usrs = ms.getUsuarios();

						Log.debug("Lista de usuarios recibida", sc);

						for (Usuario u : usrs.values()) {
							System.out.println("Usuario:\n" + u.toString());
						}
						usrs.clear();
						viaLibre.releaseLock(1);
					}
					break;

				case EMITIR_FICHERO:

					MensajeEmitirFichero mef = (MensajeEmitirFichero) m;

					// Mandamos mensaje de confirmación al servidor. Tenemos que crear un emisor que
					// gestione
					// la conexión p2p y devolvemos el nombre

					// Cargamos el mensaje a mandar para pasarselo al emisor
					String file = cliente.archivos.get(mef.getFileName());
					int puerto = user.getNextPort();

					Log.debug("Se ha pedido el archivo " + mef.getFileName() + " que contiene " + file, sc);

					Emisor emisor = new Emisor(puerto, file, mef.getFileName());

					Log.debug("Se inicia un emisor con puerto " + puerto, sc);

					emisor.start();

					fOut.writeObject(new MensajePreparadoCS(mef.getUser(), user.IP, puerto, mef.getFileName()));
					fOut.flush();
					fOut.reset();
					break;

				case PREPARADO_SC:
					// Crear el receptor(nuevo thread)
					MensajePreparadoSC preparado = (MensajePreparadoSC) m;
					// Receptor receptor = new Receptor(preparado.getIP(), preparado.getPort(),
					// viaLibre);

					Log.debug("El archivo " + preparado.getFileName() + " esta listo en ip " + preparado.getIP() + ":"
							+ preparado.getPort(), sc);

					// Log.debug(,sc);
					Receptor receptor = new Receptor(preparado.getIP(), preparado.getPort(), this.viaLibre,
							this.cliente);
					receptor.start();

					fOut.writeObject(new MensajeActualizarListaUsuarios(user.nombre, preparado.getFileName(), false));
					fOut.flush();
					fOut.reset();

					// fOut.writeObject(new MensajePreparadoSC(preparado, user.puerto,
					// preparado.getFileName()));
					break;

				case ACTUALIZAR_LISTA:
					if (m.isACK()) {
						Log.debug("Lista actualizada con exito", sc);
						break;
					}

				default:
					Log.error("Mensaje no reconocido", sc);
				}
			}
		} catch (Exception e) {
			Log.error("Error inesperado, cerrando hilo", sc);
			e.printStackTrace();
			try {
				fOut.writeObject(new MensajeConexion(TipoConexion.CERRAR, false, user));
			} catch (IOException e1) {
				Log.error("Error cerrando conexion", sc);
			}
		}
	}

	public int hashCode() {
		return id;
	}

	public ObjectOutputStream getFout() {
		return fOut;
	}

}
