package locks;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class LockTicket extends Lock {

	private AtomicInteger num;
	private int next;
	private ArrayList<MiEntero> turn;

	public LockTicket(int N) {
		super(N);
		num = new AtomicInteger(0);
		next = 0;
		turn = new ArrayList<MiEntero>(N);
		for (int i = 0; i < N; i++) {
			turn.add( new MiEntero());
		}
	}

	@Override
	public void takeLock(int id) {
		turn.get(id).set(num.getAndAdd(1));
		while (turn.get(id).get() != next)
			;
	}

	@Override
	public void releaseLock(int id) {
		next++;

	}

}