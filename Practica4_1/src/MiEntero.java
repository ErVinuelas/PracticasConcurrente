
public class MiEntero {
	private int n;
	
	public MiEntero() {
		n=0;
	}
	
	public int get() {
		return n;
	}
	
	synchronized public void change(boolean increment) {
		if(increment)
			incrementar();
		else
			decrementar();
	}
	
	public void set(int n) {
		this.n=n;
	}
	
	private void incrementar() {
		n++;
	}
	
	private void decrementar() {
		n--;
	}
}
