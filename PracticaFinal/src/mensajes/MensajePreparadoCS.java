package mensajes;

import java.io.Serializable;

public class MensajePreparadoCS extends Mensaje implements Serializable{

    private static final long serialVersionUID = -5644148867784751333L;

    private String userId;
    public MensajePreparadoCS(String userId, boolean ack) {
        super(TipoMensaje.PREPARADO_CS,ack);
        this.userId = userId;
    }    
}
