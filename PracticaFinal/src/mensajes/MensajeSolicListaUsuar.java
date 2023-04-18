package mensajes;

import java.io.Serializable;

public class MensajeSolicListaUsuar extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;

	public MensajeSolicListaUsuar(TipoConexion tipo, boolean ack) {
		super(TipoMensaje.PEDIR_LISTA,ack);
	}
}