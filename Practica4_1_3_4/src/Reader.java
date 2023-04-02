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

            res = almacen.editar(null,id);   //Elección arbitraria de la posición a leer
            System.out.println("El Reader " + id + " ha leido " + res.getVal());

		}
	}
}
