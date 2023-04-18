package mensajes;

import java.io.Serializable;

import data.Usuario;

public class MensajeConexion extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private TipoConexion tipo;
	private Usuario user;

	public MensajeConexion(TipoConexion tipo, boolean ack, Usuario user) {
		super(TipoMensaje.CONEXION, ack);
		this.tipo = tipo;
		this.user=user;
	}

	public TipoConexion getMessage() {
		return tipo;
	}

	public Usuario getUser() {
		return user;
	}

}
