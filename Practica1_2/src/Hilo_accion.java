
public class Hilo_accion extends Thread {

	private Integer N;
	private int incrementar;

	public Hilo_accion(int incrementar, Integer N) {
		this.incrementar = incrementar;
		this.N = N;
	}

	public void run() {

		N += incrementar;

		System.out.println(N);
	}
}
