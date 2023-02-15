
public class MiEntero {
	private volatile int n;
	
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
