import java.util.concurrent.Semaphore;

public class Almacen {
	/**
	* Almacena (como ultimo) un producto en el almacén. Si no hay
	* hueco el proceso que ejecute el m ́etodo bloquear ́a hasta que lo
	* haya.
	*/
	private Producto[] almacena;
	
	public Almacen(int capacidad) {
		almacena = new Producto[capacidad];
	}

	public void escribir(Producto producto, int pos) {
		almacena[pos] = producto;
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	public Producto leer(int pos) {
		Producto out = almacena[pos];
		return out;
	}

}