
public class Receptor extends Thread{
	
    private String IP;
    private int puerto;
    private Semaphore viaLibre;
    private Socket sc;
    private ObjectInputStream fIn;
    private ObjectOutputStream fOut;

    public Receptor(String IP, int puerto, Semaphore viaLibre){
        this.IP = IP;
        this.puerto = puerto;
        this.viaLibre = viaLibre;
        sc = new Socket(IP, puerto);
        fIn = new ObjectInputStream(sc.getInputStream());
        fOut = new ObjectOutputStream(sc.getOutputStream());
    }

    public void run(){
        fOut.writeObject(new MensajeConexion(TipoConexion.ABRIR, false, null));
		Log.debug("Esperando confirmacion de canal preparado...", sc);
        Mensaje m = (Mensaje) fIn.readObject();
        while(m.getTipo()!=TipoMensaje.CONEXION){
            m = (Mensaje) fIn.readObject();
        }
        MensajeConexion mc = (MensajeConexion) m;
        if (mc.getMessage() != TipoConexion.ABRIR || !mc.isACK()) {
            
        }
        m = (Mensaje) fIn.readObject();
        while(m.getTipo()!=TipoMensaje.ARCHIVO){
            m = (Mensaje) fIn.readObject();
        }
        MensajeArchivo ma = (MensajeArchivo) m;
        
    }

}
