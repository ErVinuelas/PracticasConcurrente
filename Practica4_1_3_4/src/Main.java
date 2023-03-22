import java.util.concurrent.Semaphore;    

public class Main {

	public static void main(String[] args) {
		int B = 100;
		int M = 100;
		int N = 100;
		
        int numberReaders = 0;
        int delayedReaders = 0;
        int numberWriters = 0;
        int delayedWriters = 0;

		Semaphore testigo = new Semaphore(1);
		Semaphore reader = new Semaphore(0);
		Semaphore writer = new Semaphore(0);

		Writer[] arrayProductores = new Writer[N];
		Reader[] arrayConsumidores = new Reader[M];
		Almacen almacen = new Almacen(B);
		
		for(int i = 0; i < N; ++i) {
			arrayProductores[i] = new Writer(testigo, reader, writer, numberReaders, delayedReaders, numberWriters, delayedWriters, almacen, i);
		}
		
		for(int i = 0; i < M; ++i) {
			arrayConsumidores[i] = new Reader(testigo, reader, writer, numberReaders, delayedReaders, numberWriters, delayedWriters, almacen, i);
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
