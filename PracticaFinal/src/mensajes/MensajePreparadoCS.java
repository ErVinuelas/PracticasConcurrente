package mensajes;

import java.io.Serializable;

public class MensajePreparadoCS extends Mensaje implements Serializable {

    private static final long serialVersionUID = -5644148867784751333L;
    private int port;
    private String IP;
    private String userId;
    private String file;
    
    public MensajePreparadoCS(String userId, String IP, int port, String file) {
        super(TipoMensaje.PREPARADO_CS,true);
        this.userId = userId;
        this.IP = IP;
        this.port = port;
        this.file=file;
    }    
    
    public String getIP() {
    	return this.IP;
    }
    
    public int getPort() {
    	return this.port;
    }
    
    public String getUser() {
		return userId;
	}
    
    public String getFileName() {
    	return file;
    }

}
