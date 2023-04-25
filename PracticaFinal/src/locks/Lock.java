package locks;

import java.io.Serializable;

public abstract class Lock implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int N;
	
	public Lock(int N) {
		this.N=N;
	}
	
	public abstract void takeLock(int id);
	
	public abstract void releaseLock(int id);
}
