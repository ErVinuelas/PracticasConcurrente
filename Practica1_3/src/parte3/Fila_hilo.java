package parte3;

public class Fila_hilo extends Thread {

	int fila;
	int[] filaA;
	int[][] B;
	int[] filaC;
	int size;

	public Fila_hilo(int size, int fila, int[] filaA, int[][] B, int[] filaC) {
		this.fila = fila;
		this.filaA = filaA;
		this.B = B;
		this.filaC = filaC;
		this.size = size;
	}

	public void run() {
		for (int i = 0; i < size; i++) {
			filaC[i] = 0;
			for (int j = 0; j < size; j++)
				filaC[i] += filaA[j] * B[j][i];
		}
	}
}
