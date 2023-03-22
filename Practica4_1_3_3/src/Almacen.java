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
	
	private int full;
	private int empty;
	
	private Producto[] almacena;
	private int capacidad;

	
	public Almacen(int capacidad) {
		ini=0;
		fin=0;
		
		full = 0;
		empty = capacidad;
		
		this.capacidad=capacidad;
		almacena = new Producto[capacidad];
	}

	public synchronized Producto modificar(Producto producto) {
		if (producto==null)
			return extraer();
		else
			almacenar(producto); return null;
	}
	
	public synchronized void almacenar(Producto producto) {
		
		while(empty == 0)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		almacena[fin] = producto;
		fin=(fin+1)%capacidad;
		empty--;
		full++;
		this.notifyAll();
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	public synchronized Producto extraer() {
		
		while(full == 0)
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Producto out=almacena[ini];
		ini=(ini+1)%capacidad;
		empty++;
		full--;
		this.notifyAll();
		return out;
	}
	
	public void show() {
		for(Producto i : almacena) {
			System.out.print(Integer.toString(i.getVal())+" ");
		}
	}
}