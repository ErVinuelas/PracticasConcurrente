package lecturaEscritura;

public interface LecturaEscritura {
	public void requestWrite();
	public void releaseWrite();
	public void requestRead();
	public void releaseRead();
}
