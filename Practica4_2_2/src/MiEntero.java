import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MiEntero {
	private volatile int n;
	
	//Lock
	private final Lock l;
		
	public MiEntero() {
		n=0;
		l = new ReentrantLock(true); //El lock le pasamos true para que sea justo
	}
	
	public int get() {
		return n;
	}
	
	public void set(int n) {
		this.n=n;
	}
	
	public void incrementar() {
		l.lock();
				
		n++;
		
		l.unlock();
	}
	
	public void decrementar() {
		l.lock();
		
		n--;
		
		l.unlock();
	}
}
