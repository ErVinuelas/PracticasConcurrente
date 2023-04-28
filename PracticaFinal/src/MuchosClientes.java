import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MuchosClientes extends Thread{

    String nombre;
    String dir;
    int port; 
    HashMap<String, String> archivos;
    List<String> archivosPedir;
    int numPedirUsuarios;
    int id;
    boolean ends = true;

    public MuchosClientes(int id, String nombre, String dir, int port, HashMap<String, String> archivos, List<String> archivosPedir, int numPedirUsuarios, boolean ends){
        this.id = id;
        this.nombre = nombre;
        this.dir = dir;
        this.port = port;
        this.archivos = archivos;
        this.archivosPedir = archivosPedir;
        this.numPedirUsuarios = numPedirUsuarios;
        this.ends = ends;
    }

    public void run() {
        try{
            new Cliente(id, nombre, dir, port, archivos, archivosPedir, numPedirUsuarios, ends);
        }catch(Exception e){
            System.out.println("algo fallo con el cliente "+ Integer.toString(id));
        }
    }

    public static void main(String[] args) {
        Log.DEBUG = false;
        int numClientes = 15;
        String dir = "localhost";
        int port = 5200;
        HashMap<String, String> archivos = new HashMap<String, String>();
        int numArchivos = 10;
        for(int i = 0; i < numArchivos; i++){
            archivos.put(Integer.toString(i), Integer.toString(i));
        }
        List<String> archivosPedir = new ArrayList<String>();
        for(int i = 0; i < numArchivos; i++){
            archivosPedir.add(Integer.toString(i));
        }
        int numPedirUsuarios = 5;
        List<MuchosClientes> clientes = new ArrayList<MuchosClientes>();
        MuchosClientes mainCliente = new MuchosClientes(101, "main", dir, port, archivos, new ArrayList<String>(), numPedirUsuarios, false); 
        mainCliente.start();
        for(int i = 1; i <= numClientes; i++){
            clientes.add(new MuchosClientes(i, Integer.toString(i), dir, port, new HashMap<String, String>() , archivosPedir, numPedirUsuarios, false));
        }
        try{
            TimeUnit.SECONDS.sleep(2);
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        for(MuchosClientes c : clientes){
            c.start();
            try{
                TimeUnit.MILLISECONDS.sleep(200);
                
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        for(MuchosClientes c : clientes){
            try{
                c.join();
            }catch(Exception e){
                System.out.println("algo fallo con el cliente "+ Integer.toString(c.id));
            }
        }
        mainCliente.interrupt();
        
    }
        
}
