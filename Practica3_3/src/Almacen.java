import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Almacen {
	/**
	* Almacena (como ultimo) un producto en el almac ́en. Si no hay
	* hueco el proceso que ejecute el m ́etodo bloquear ́a hasta que lo
	* haya.
	*/
	private int ini;
	private int fin;
	private Semaphore mutexP;
	private Semaphore mutexC;
	private Producto[] almacena;
	private int capacidad;
	
	public Almacen(int capacidad, Semaphore mutexP, Semaphore mutexC) {
		ini=0;
		fin=0;
		this.capacidad=capacidad;
		almacena = new Producto[capacidad];
		this.mutexC=mutexC;
		this.mutexP=mutexP;
	}

	public void almacenar(Producto producto) {
		try {
			mutexP.acquire();
		} catch (InterruptedException e) {}
		almacena[fin] = producto;
		fin=(fin+1)%capacidad;
		mutexP.release();
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	public Producto extraer() {
		try {
			mutexC.acquire();
		} catch (InterruptedException e) {}
		Producto out=almacena[ini];
		ini=(ini+1)%capacidad;
		mutexC.release();
		return out;
	}
	
	public void show() {
		for(Producto i : almacena) {
			System.out.print(Integer.toString(i.getVal())+" ");
		}
	}
}