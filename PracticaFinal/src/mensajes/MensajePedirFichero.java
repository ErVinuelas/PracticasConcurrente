package mensajes;

import java.io.Serializable;

public class MensajePedirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private String fileName;

	public MensajePedirFichero(String fileName, boolean ack) {
		super(TipoMensaje.PEDIR_FICHERO, ack);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

}