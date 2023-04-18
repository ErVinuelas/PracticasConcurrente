import java.net.Socket;
public class Log {
    public static void debug(String mensaje, Socket sc){
        System.out.println("[DEBUG] " + mensaje + " (ip: " + sc.getInetAddress().toString() + ", Port: " + sc.getPort() + ")");
    }
}
