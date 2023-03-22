
public class Hilo_accion extends Thread {

	private MiEntero N;
	private boolean incrementar;
	private int num;
	private int id;

	public Hilo_accion(boolean incrementar, MiEntero N, int num, int id) {
		this.incrementar = incrementar;
		this.N = N;
		this.num=num;
		this.id=id;
	}

	public void run() {
		if(incrementar)
			for(int i=0;i<num;i++) {
				N.incrementar();
				System.out.println(N.get());
			}
		else
			for(int i=0;i<num;i++) {
				N.decrementar();
				System.out.println(N.get());
			}
	}
}
