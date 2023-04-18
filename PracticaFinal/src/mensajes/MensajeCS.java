package mensajes;

import java.io.Serializable;

public class MensajeCS extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;

	public MensajeCS(TipoConexion tipo, boolean ack) {
		super(TipoMensaje.CONNECTIONPEERTOPEER,ack);
		this.tipo=tipo;
	}
	
	public TipoConexion getMessage() {
		return tipo;
	}

}