import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private Lock mutexP;
	private Producto[] almacena;
	private int capacidad;
	private Condition producer;
	private Condition consumer;
	
	
	public Almacen(int capacidad) {
		ini=0;
		fin=0;
		this.capacidad=capacidad;
		almacena = new Producto[capacidad];
		this.mutexP=new ReentrantLock(true);
		producer=mutexP.newCondition();
		consumer=mutexP.newCondition();
		full=0;
		empty=capacidad;
	}

	public void almacenar(Producto producto) {
		mutexP.lock();
		while(empty==0)
			try {
				producer.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		almacena[fin] = producto;
		fin=(fin+1)%capacidad;
		empty--;
		full++;
		consumer.signal();
		producer.signal();
		mutexP.unlock();
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	public Producto extraer() {
		mutexP.lock();
		while(full==0)
			try {
				producer.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Producto out=almacena[ini];
		ini=(ini+1)%capacidad;
		empty++;
		full--;
		producer.signal();
		consumer.signal();
		mutexP.unlock();
		return out;
	}
	
	public void show() {
		for(Producto i : almacena) {
			System.out.print(Integer.toString(i.getVal())+" ");
		}
	}
}