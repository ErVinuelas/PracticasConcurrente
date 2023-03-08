import java.util.concurrent.Semaphore;

public class Hilo_accion extends Thread {

	private MiEntero N;
	private boolean incrementar;
	private int num;
	private Semaphore sem;

	public Hilo_accion(boolean incrementar, MiEntero N, int num, Semaphore sem) {
		this.incrementar = incrementar;
		this.N = N;
		this.num=num;
		this.sem=sem;
	}

	public void run() {
		if(incrementar)
			for(int i=0;i<num;i++) {
				try {
					sem.acquire();
				} catch (InterruptedException e) {}
				N.incrementar();
				sem.release();
			}
		else
			for(int i=0;i<num;i++) {
				try {
					sem.acquire();
				} catch (InterruptedException e) {}
				N.decrementar();
				sem.release();
			}
		//N.set(incrementar + N.get());

		System.out.println(N.get());
	}
}
