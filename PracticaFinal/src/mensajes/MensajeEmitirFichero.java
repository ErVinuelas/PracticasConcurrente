package mensajes;

import java.io.Serializable;

public class MensajeEmitirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;

	public MensajeEmitirFichero(TipoConexion tipo, boolean ack) {
		super(TipoMensaje.EMITIR_FICHERO,ack);
		this.tipo=tipo;
	}
	
	public TipoConexion getMessage() {
		return tipo;
	}

}