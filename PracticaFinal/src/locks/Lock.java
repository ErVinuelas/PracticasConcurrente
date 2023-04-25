package locks;


public abstract class Lock {
	protected int N;
	
	public Lock(int N) {
		this.N=N;
	}
	
	public abstract void takeLock(int id);
	
	public abstract void releaseLock(int id);
}
