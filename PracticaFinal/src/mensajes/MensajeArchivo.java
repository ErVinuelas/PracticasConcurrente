public class MensajeArchivo extends Mensaje{
    
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
