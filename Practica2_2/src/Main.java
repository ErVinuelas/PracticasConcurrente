
public class Main {

	public static void main(String[] args) {
		int M = 1;
		int num = 100;
		MiEntero N = new MiEntero();
		N.set(0);
		Lock lock = new LockRompeEmpate(2 * M);

		Hilo_accion[] arrayThread = new Hilo_accion[2 * M];
		
		for (int i = 0; i < M; i++) {
			arrayThread[i] = new Hilo_accion(true, N, num, i, lock);
			arrayThread[i].start();

			arrayThread[M + i] = new Hilo_accion(false, N, num, M + i, lock);
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
