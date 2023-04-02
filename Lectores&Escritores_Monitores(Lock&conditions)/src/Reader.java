import java.util.concurrent.Semaphore;

public class Reader extends Thread{

    /*Atributos circunstanciales */
	private Almacen almacen;
	private Producto res;
	private int id;

	public Reader(Almacen almacen, int id) {
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
            this.almacen.solicitarLectura(id);

            res = this.almacen.leer(id);

            System.out.println("El lector num " + id + " ha leido el valor " + res);

            this.almacen.terminarLectura(id);
		}
	}
}
