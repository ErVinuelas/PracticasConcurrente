
public class Hilo_accion extends Thread {

	private MiEntero N;
	private boolean incrementar;
	private int num;
	private static Lock lock;
	private int id;

	public Hilo_accion(boolean incrementar, MiEntero N, int num, int id, Lock lock) {
		this.incrementar = incrementar;
		this.N = N;
		this.num=num;
		this.lock=lock;
		this.id=id;
	}

	public void run() {
		if(incrementar)
			for(int i=0;i<num;i++) {
				lock.takeLock(id);
				N.incrementar();
				lock.releaseLock(id);
			}
		else
			for(int i=0;i<num;i++) {
				lock.takeLock(id);
				N.decrementar();
				lock.releaseLock(id);
			}
		//N.set(incrementar + N.get());

		System.out.println(N.get());
	}
}
