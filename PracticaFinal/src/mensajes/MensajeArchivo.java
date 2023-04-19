package mensajes;

public class MensajeArchivo extends Mensaje{
    
    private static final long serialVersionUID = -9051952682632048062L;
	private String mensaje;
    private String nombreArchivo;

    public MensajeArchivo(String mensaje, String nombreArchivo, boolean ack) {
        super(TipoMensaje.ARCHIVO, ack);
        this.mensaje = mensaje;
        this.nombreArchivo = nombreArchivo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

}
