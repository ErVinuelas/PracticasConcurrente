import java.util.concurrent.Semaphore;    

public class Main {

	public static void main(String[] args) {
		int B = 5;
		int M = 5;
		int N = 5;

		Writer[] arrayProductores = new Writer[N];
		Reader[] arrayConsumidores = new Reader[M];
		Almacen almacen = new Almacen(B);
		
		for(int i = 0; i < N; ++i) {
			arrayProductores[i] = new Writer(almacen, i);
		}
		
		for(int i = 0; i < M; ++i) {
			arrayConsumidores[i] = new Reader(almacen, i);
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
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < M; ++i) {
			try {
				arrayConsumidores[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
