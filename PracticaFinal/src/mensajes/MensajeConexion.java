package mensajes;

import java.io.Serializable;

public class MensajeConexion extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;

	public MensajeConexion(TipoConexion tipo, boolean ack) {
		super(TipoMensaje.CONEXION,ack);
		this.tipo=tipo;
	}
	
	public TipoConexion getMessage() {
		return tipo;
	}

}
