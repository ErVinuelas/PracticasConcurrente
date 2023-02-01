package parte3;

public class multMat {

	public static void main(String[] args) {
		int N = 2;

		int[][] A = { { 1, 2 }, { 3, 4 } };
		int[][] B = { { 5, 6 }, { 7, 8 } };

		int[][] C = new int[N][N];

		Fila_hilo[] array = new Fila_hilo[N];

		for (int i = 0; i < N; i++) {
			array[i] = new Fila_hilo(N, i, A[i], B, C[i]);
			array[i].start();
		}

		for (int i = 0; i < N; ++i) {
			try {
				array[i].join();
			} catch (InterruptedException e) {
			}
		}

		for (int i = 0; i < N; i++) {
			System.out.println();
			for (int j = 0; j < N; j++) {
				System.out.print(C[i][j]);
				System.out.print(" ");
			}
		}
	}

}
