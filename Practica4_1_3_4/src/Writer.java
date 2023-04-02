    import java.util.concurrent.Semaphore;

public class Writer extends Thread {

    /*Atribuotos circunstaciales */
	private Almacen almacen;
	private int id;

	public Writer(Almacen almacen, int id) {

		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {

            almacen.editar(new Producto(id), id);
            System.out.println("El Reader " + id + " ha escrito " + id);
          

		}
	}

}
