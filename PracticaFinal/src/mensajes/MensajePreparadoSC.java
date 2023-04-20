package mensajes;

import java.io.Serializable;

public class MensajePreparadoSC extends Mensaje implements Serializable {

	private static final long serialVersionUID = -5644148867784751333L;
	private int port;
    private String IP;
    private String fileName;
	
	public MensajePreparadoSC(String IP, int puerto, String fileName) {
		super(TipoMensaje.PREPARADO_SC, true);
		this.IP=IP;
		this.port=puerto;
		this.fileName=fileName;
	}
	
	public String getIP() {
    	return this.IP;
    }
    
    public int getPort() {
    	return this.port;
    }
    
    public String getFileName() {
    	return this.fileName;
    }
    
}
