
public class Boring_thread extends Thread {

	private int N;
	private long T;

	public Boring_thread(int N, long T) {
		this.N = N;
		this.T = T;
	}

	public void run() {
		System.out.println(N);

		try {
			sleep(T);
		} catch (InterruptedException e) {

		}

		System.out.println(N);
	}

}
