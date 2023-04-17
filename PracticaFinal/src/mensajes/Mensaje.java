package mensajes;
import java.io.Serializable;

public abstract class Mensaje implements Serializable {

	private static final long serialVersionUID = 1L;
	private TipoMensaje tipo;
	private boolean ack;
	
	public Mensaje(TipoMensaje tipo, boolean ack) {
		this.tipo=tipo;
		this.ack=ack;
	}
	
	public TipoMensaje getTipo() {
		return tipo;
	}
	
	public boolean isACK() {
		return ack;
	}

}
