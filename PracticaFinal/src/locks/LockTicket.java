package locks;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket extends Lock {

	private static final long serialVersionUID = 1L;
	private AtomicInteger num;
	private int next;

	public LockTicket(int N) {
		this.N = N;
		num = new AtomicInteger(0);
		next = 0;
	}

	@Override
	public void takeLock(int id) {
		AtomicInteger Max = new AtomicInteger(N + 1);
		MiEntero turn = new MiEntero();
		
		turn.set(num.getAndAdd(1));
		
		if(turn.get() == Max.get())	{	num.getAndAdd(-Max.get());	}
		else if(turn.get() >= Max.get())	{	turn.set(turn.get() - Max.get());	}
		
		while (turn.get() != next)
			;
	}

	@Override
	public void releaseLock(int id) {
		next = next%N + 1;

	}

}