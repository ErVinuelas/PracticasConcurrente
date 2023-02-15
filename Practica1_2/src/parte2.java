
public class parte2 {

	public static void main(String[] args) {
		int M = 1000;
		int num = 100;
		MiEntero N = new MiEntero();
		N.set(0);

		Hilo_accion[] arrayThread = new Hilo_accion[2 * M];
		for (int i = 0; i < M; i++) {
			arrayThread[i] = new Hilo_accion(true, N, num);
			arrayThread[i].start();

			arrayThread[M + i] = new Hilo_accion(false, N, num);
			arrayThread[M + i].start();
		}

		for (int i = 0; i < 2 * M; ++i) {
			try {
				arrayThread[i].join();
			} catch (InterruptedException e) {
			}
		}
	}

}
