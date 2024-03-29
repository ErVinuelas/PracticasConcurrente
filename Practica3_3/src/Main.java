import java.util.concurrent.Semaphore;    

public class Main {

	public static void main(String[] args) {
		int B = 100;
		int M = 100;
		int N = 100;
		
		Semaphore empty = new Semaphore(B);
		Semaphore full = new Semaphore(0);
		Semaphore mutexC = new Semaphore(1);
		Semaphore mutexP = new Semaphore(1);

		Productor[] arrayProductores = new Productor[N];
		Consumidor[] arrayConsumidores = new Consumidor[M];
		Almacen almacen = new Almacen(B, mutexP, mutexC);
		
		for(int i = 0; i < N; ++i) {
			arrayProductores[i] = new Productor(empty, full, almacen, i);
		}
		
		for(int i = 0; i < M; ++i) {
			arrayConsumidores[i] = new Consumidor(empty, full, almacen, i);
		}
		
		for(int i = 0; i < N; ++i) {
			arrayProductores[i].start();
		}
		for(int i = 0; i < M; ++i) {
			arrayConsumidores[i].start();
		}
		
		for(int i = 0; i < N; ++i) {
			try {
				arrayProductores[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < M; ++i) {
			try {
				arrayConsumidores[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
