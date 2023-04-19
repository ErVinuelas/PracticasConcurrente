import java.net.Socket;

public class Log {

	public static boolean DEBUG = true;
	public static boolean ERROR = true;

	public static void debug(String mensaje, Socket sc) {
		if (DEBUG)
			System.out.println(
					"[DEBUG] " + mensaje + " (ip: " + sc.getInetAddress().toString() + ", Port: " + sc.getPort() + ")");
	}

	public static void error(String mensaje, Socket sc) {
		if (ERROR)
			System.out.println(
					"[Error] " + mensaje + " (ip: " + sc.getInetAddress().toString() + ", Port: " + sc.getPort() + ")");
	}

	public static void console(String mensaje) {
		System.out.print("|" + mensaje + "> ");
	}
}
