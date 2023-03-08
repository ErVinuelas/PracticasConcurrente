import java.util.concurrent.Semaphore;

public class Reader extends Thread{

	private Semaphore empty;
	private Semaphore full;
	private Almacen almacen;
	private Producto res;
	private int id;

	public Reader(Semaphore empty, Semaphore full, Almacen almacen, int id) {
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
			int val=this.res.getVal();
			
			System.out.println("El consumidor " +  this.id +  " ha consumido " +  val);

			empty.release();
		}
	}
}
