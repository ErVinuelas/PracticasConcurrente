package mensajes;

import java.io.Serializable;

public class MensajeEmitirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;

	private String fileName;
	private String userId;

	public MensajeEmitirFichero(String fileName, String userId, boolean ack) {
		super(TipoMensaje.EMITIR_FICHERO, ack);
		this.fileName = fileName;
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

}