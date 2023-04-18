public class MensajePreparadoSC implements Serializable{

    private static final long serialVersionUID = -5644148867784751333L;

    public MensajePreparadoSC(boolean ack) {
        super(TipoMensaje.PREPARADO_SC,ack);
    }    
}
