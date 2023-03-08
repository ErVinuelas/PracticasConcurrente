import java.util.concurrent.Semaphore;

public class Productor extends Thread {
	private Semaphore empty;
	private Semaphore full;
	private Almacen almacen;
	private int id;

	public Productor(Semaphore empty, Semaphore full, Almacen almacen, int id) {
		this.empty = empty;
		this.full = full;
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
			try {
				empty.acquire();
			} catch (Exception e) {
			}

			almacen.almacenar(new Producto(id));
			System.out.println("El productor " +  this.id +  " ha producido " +  this.id);

			full.release();
		}
	}

}
