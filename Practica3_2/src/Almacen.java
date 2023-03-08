import java.util.LinkedList;

public class Almacen {
	/**
	* Almacena (como ultimo) un producto en el almac ́en. Si no hay
	* hueco el proceso que ejecute el m ́etodo bloquear ́a hasta que lo
	* haya.
	*/
	private LinkedList<Producto> almacena;
	private int capacidad;
	
	public Almacen(int capacidad) {
		this.capacidad=capacidad;
		almacena = new LinkedList<Producto>();
	}

	public boolean almacenar(Producto producto) {
		if(almacena.size()>=capacidad)
			return false;
		almacena.add(producto);
		return true;
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	public Producto extraer() {
		if(almacena.size()<=0)
			return null;
		return almacena.pop();
	}
}