package mensajes;

import java.io.Serializable;

public class MensajeConexion extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;
	private String nombre;

	public MensajeConexion(TipoConexion tipo, boolean ack, String nombre) {
		super(TipoMensaje.CONEXION, ack);
		this.tipo = tipo;
		this.nombre = nombre;
	}

	public TipoConexion getMessage() {
		return tipo;
	}

	public String getName() {
		return nombre;
	}

}
