import java.util.concurrent.Semaphore;

public class Consumidor extends Thread{

	private Almacen almacen;
	private Producto res;
	private int id;

	public Consumidor(Almacen almacen, int id) {
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {

			res = almacen.extraer();
			int val=this.res.getVal();
			
			System.out.println("El consumidor " +  this.id +  " ha consumido " +  val);

		}
	}
}
