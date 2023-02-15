import java.util.ArrayList;

public class LockRompeEmpate extends Lock {

	ArrayList<MiEntero> last;
	ArrayList<MiEntero> in;

	public LockRompeEmpate(int N) {
		super(N);
		this.last = new ArrayList<MiEntero>(N);
		this.in = new ArrayList<MiEntero>(N);
	}

	public void takeLock(int id) {
		for (int j = 1; j < N; j++) {
			in.get(id).set(j);
			last.get(j).set(id);
			for (int k = 1; k < N; k++)
				if (k != id)
					while (in.get(j).get() >= in.get(id).get() && last.get(j).get() == id)
						;
		}
	}

	public void releaseLock(int id) {
		in.get(id).set(0);
	}
}
