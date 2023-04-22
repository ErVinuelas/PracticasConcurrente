package locks;

public class MiEntero {
	private volatile int n;
	
	public MiEntero() {
		n=0;
	}
	
	public int get() {
		return n;
	}
	
	public void set(int n) {
		this.n=n;
	}
	
	public void incrementar() {
		n++;
	}
	
	public void decrementar() {
		n--;
	}
}
