import java.util.concurrent.Semaphore;

public class Main {

	public static void main(String[] args) {
		int M = 100;
		int num = 100;
		MiEntero N = new MiEntero();
		N.set(0);
		Semaphore sem = new Semaphore(1);

		Hilo_accion[] arrayThread = new Hilo_accion[2 * M];
		
		for (int i = 0; i < M; i++) {
			arrayThread[i] = new Hilo_accion(true, N, num, sem);
			arrayThread[i].start();

			arrayThread[M + i] = new Hilo_accion(false, N, num, sem);
			arrayThread[M + i].start();
		}

		for (int i = 0; i < 2 * M; ++i) {
			try {
				arrayThread[i].join();
			} catch (InterruptedException e) {
			}
		}
		System.out.println(N.get());
	}
}
