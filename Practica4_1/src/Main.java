
public class Main {

	public static void main(String[] args) {
		int M = 1;
		int num = 100;
		MiEntero N = new MiEntero();
		N.set(0);

		Hilo_accion[] arrayThread = new Hilo_accion[2 * M];
		
		for (int i = 0; i < M; i++) {
			arrayThread[i] = new Hilo_accion(true, N, num, i);
			arrayThread[i].start();

			arrayThread[M + i] = new Hilo_accion(false, N, num, M + i);
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
