package mensajes;

import java.io.Serializable;

public class MensajePedirFichero extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;

	public MensajePedirFichero(TipoConexion tipo, boolean ack) {
		super(TipoMensaje.PEDIR_FICHERO,ack);
		this.tipo=tipo;
	}
	
	public TipoConexion getMessage() {
		return tipo;
	}

}