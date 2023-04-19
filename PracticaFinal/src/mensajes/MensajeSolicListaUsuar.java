package mensajes;

import java.io.Serializable;
import java.util.Map;

import data.Usuario;

public class MensajeSolicListaUsuar extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private Map<String, Usuario> usuarios;

	public MensajeSolicListaUsuar(Map<String, Usuario> usuarios, boolean ack) {
		super(TipoMensaje.PEDIR_LISTA,ack);
		this.usuarios=usuarios;
	}
	
	public Map<String, Usuario> getUsuarios() {
		return usuarios;
	}	
}