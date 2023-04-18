package mensajes;

import java.io.Serializable;

public class MensajePedirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;

    private String fileName;

	public MensajePedirFichero(TipoConexion tipo, boolean ack, String fileName) {
		super(TipoMensaje.PEDIR_FICHERO,ack);
		this.tipo=tipo;
        this.fileName = fileName;
	}
	
	public TipoConexion getMessage() {
		return tipo;
	}

    public getFileName() {
        return fileName;
    }

}