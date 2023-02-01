
public class Practica1 {
	public static void main(String[] args) {
		int N = 100;
		long T = 1000;
		
		Boring_thread[] arrayThread = new Boring_thread[N];
		
		for (int i = 0; i < N; i++) {
			arrayThread[i] = new Boring_thread(i, T);
			arrayThread[i].start();
		}

		for (int i = 0; i < N; ++i) {
			try {
				arrayThread[i].join();
			} catch (InterruptedException e) {
			}
		}
	}
}
