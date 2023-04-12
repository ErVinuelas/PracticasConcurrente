import java.util.Set;
import java.util.TreeSet;

public class Servidor {
	
	protected Set<Thread> threads;
	protected OyenteServidor oyente;
	
	public Servidor() {
		threads = new TreeSet<Thread>();
		oyente = new OyenteServidor(this);
	}

	public void listen() {
		oyente.start();
	}

	public String getData(String key) {
		return "you have requested the data for key: " + key;
	}
	
	public static void main(String[] args) {
		Servidor serv = new Servidor();
		serv.listen();
	}

}
