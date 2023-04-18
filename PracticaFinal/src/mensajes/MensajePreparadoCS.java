public class MensajePreparadoCS implements Serializable{

    private static final long serialVersionUID = -5644148867784751333L;

    public MensajePreparadoCS(boolean ack) {
        super(TipoMensaje.PREPARADO_CS,ack);
    }    
}
