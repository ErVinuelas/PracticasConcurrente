import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Almacen {
	/**
	* Almacena (como ultimo) un producto en el almac ́en. Si no hay
	* hueco el proceso que ejecute el m ́etodo bloquear ́a hasta que lo
	* haya.
	*/
	private Semaphore mutexP;
	private Producto[] almacena;
	private int capacidad;
	
	public Almacen(int capacidad, Semaphore mutexP) {
		this.capacidad=capacidad;
		almacena = new Producto[capacidad];
		this.mutexP=mutexP;
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
		Producto out=almacena[pos];
		return out;
	}
	
	public void show() {
		for(Producto i : almacena) {
			System.out.print(Integer.toString(i.getVal())+" ");
		}
	}
}