import java.util.concurrent.Semaphore;

public class Consumidor extends Thread{

	private Semaphore empty;
	private Semaphore full;
	private Almacen almacen;
	private Producto res;
	private int id;

	public Consumidor(Semaphore empty, Semaphore full, Almacen almacen, int id) {
		this.empty = empty;
		this.full = full;
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
			try {
				full.acquire();
			} catch (InterruptedException e) {
			}

			res = almacen.extraer();
			
			System.out.println("El consumidor " +  this.id +  " ha consumido " +  this.res.getVal());


			empty.release();
		}
	}
}
