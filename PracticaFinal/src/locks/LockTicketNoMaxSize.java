package locks;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicketNoMaxSize extends Lock {

	private static final long serialVersionUID = 1L;
	private AtomicInteger num;
	private int next;

	public LockTicketNoMaxSize() {
		num = new AtomicInteger(0);
		next = 0;
	}

	@Override
	public void takeLock(int id) {
		MiEntero turn = new MiEntero();
		
		turn.set(num.getAndAdd(1));
		
		while (turn.get() != next)
			;
	}

	@Override
	public void releaseLock(int id) {
		next = next + 1;

	}

}