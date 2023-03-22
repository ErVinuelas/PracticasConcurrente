import java.util.concurrent.Semaphore;

public class Productor extends Thread {
	private Almacen almacen;
	private int id;

	public Productor(Almacen almacen, int id) {
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
			
			almacen.almacenar(new Producto(id));
			System.out.println("El productor " +  this.id +  " ha producido " +  this.id);

		}
	}

}
