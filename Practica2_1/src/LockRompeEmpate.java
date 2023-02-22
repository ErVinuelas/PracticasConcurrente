import java.util.ArrayList;

public class LockRompeEmpate extends Lock {

	ArrayList<MiEntero> last;
	ArrayList<MiEntero> in;

	public LockRompeEmpate(int N) {
		super(N);
		System.out.println(N);
		this.last = new ArrayList<MiEntero>(N);
		this.in = new ArrayList<MiEntero>(N);
		for(int i=0;i<N;i++) {
			last.add(new MiEntero());
			in.add(new MiEntero());
		}
	}

	public void takeLock(int id) {
		for (int j = 0; j < N; j++) {
			in.get(id).set(j+1);
			last.get(j).set(id);
			for (int k = 0; k < N; k++)
				if (k != id)
					while (in.get(k).get() >= in.get(id).get() && last.get(j).get() == id);
			}
		System.out.println("the process "+Integer.toString(id)+" took the lock");
	}

	public void releaseLock(int id) {
		System.out.println("the process "+Integer.toString(id)+" released the lock");
		in.get(id).set(0);
	}
}
