package locks;

import java.util.ArrayList;

public class LockRompeEmpate extends Lock {

	ArrayList<MiEntero> last;
	ArrayList<MiEntero> in;

	public LockRompeEmpate(int N) {
		super(N);
		this.last = new ArrayList<MiEntero>(N);
		this.in = new ArrayList<MiEntero>(N);
		for(int i=0;i<N;i++) {
			last.add(new MiEntero());
			in.add(new MiEntero());
		}
	}

	@Override
	public void takeLock(int id) {
		for (int j = 0; j < N; j++) {
			in.get(id).set(j+1);
			last.get(j).set(id);
			for (int k = 0; k < N; k++)
				if (k != id)
					while (in.get(k).get() >= in.get(id).get() && last.get(j).get() == id);
		}
	}

	public void releaseLock(int id) {
		in.get(id).set(0);
	}
}
