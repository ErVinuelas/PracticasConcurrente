    import java.util.concurrent.Semaphore;

public class Writer extends Thread {
    private Almacen almacen;
	private int id;

	public Writer(Almacen almacen, int id) {
		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
            this.almacen.solicitarEscritura(id);

            this.almacen.escribir(new Producto(id), id);

            System.out.println("El escritor num " + id + " ha escrito");

            this.almacen.terminarEscritura(id);
		}
	}
}
