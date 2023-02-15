
public class Hilo_accion extends Thread {

	private MiEntero N;
	private boolean incrementar;
	private int num;

	public Hilo_accion(boolean incrementar, MiEntero N, int num) {
		this.incrementar = incrementar;
		this.N = N;
		this.num=num;
	}

	public void run() {
		if(incrementar)
			for(int i=0;i<num;i++) N.incrementar();
		else
			for(int i=0;i<num;i++) N.decrementar();
		//N.set(incrementar + N.get());

		System.out.println(N.get());
	}
}
