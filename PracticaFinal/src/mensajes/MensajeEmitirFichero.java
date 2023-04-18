package mensajes;

import java.io.Serializable;

public class MensajeEmitirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;

    private String fileName;

	public MensajeEmitirFichero(String fileName, boolean ack) {
		super(TipoMensaje.EMITIR_FICHERO,ack);
        this.fileName = fileName;
    }
	
	public TipoConexion getMessage() {
		return tipo;
	}

    public getFileName() {
        return fileName;
    }

}