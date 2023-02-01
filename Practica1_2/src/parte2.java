
public class parte2 {

	public static void main(String[] args) {
		int M = 100;
		Integer N = 0;

		Hilo_accion[] arrayThread = new Hilo_accion[2 * M];
		for (int i = 0; i < M; i++) {
			arrayThread[i] = new Hilo_accion(1, N);
			arrayThread[i].start();

			arrayThread[M + i] = new Hilo_accion(-1, N);
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
