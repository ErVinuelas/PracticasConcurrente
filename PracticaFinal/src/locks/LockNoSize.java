package locks;


public abstract class LockNoSize {
	protected int N;
	
	public LockNoSize() {}
	
	public abstract void takeLock();
	
	public abstract void releaseLock();
}
