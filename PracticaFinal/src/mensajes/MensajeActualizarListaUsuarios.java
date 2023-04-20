package mensajes;

public class MensajeActualizarListaUsuarios extends Mensaje{
	
	private static final long serialVersionUID = 2301916992814693162L;
	public String idCliente;
	public String nombreArchivo;
	
	
	public MensajeActualizarListaUsuarios(String idCliente, String nombreArchivo, boolean ack) {
		super(TipoMensaje.ACTUALIZAR_LISTA, ack);
		
		this.idCliente = idCliente;
		this.nombreArchivo = nombreArchivo;
	}
	
}