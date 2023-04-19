
public class MensajeActualizarListaUsuarios extends Mensaje{
	public String idCliente;
	public String nombreArchivo;
	
	
	public MensajeActualizarListaUsuarios(String idCliente, nombreArchivo, bool ack) {
		super(TipoMensaje.ACTUALIZAR_LISTA, ack);
		
		this.idCliente = idCliente;
		this.nombreArchivo = nombreArchivo;
	}
}
