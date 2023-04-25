package locks;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTicketNoSize extends LockNoSize implements Serializable{

	private static final long serialVersionUID = 1L;
	private AtomicInteger num;
	private int next;

	public LockTicketNoSize() {
		num = new AtomicInteger(0);
		next = 0;
	}

	@Override
	public void takeLock() {
		MiEntero turn = new MiEntero();
		turn.set(num.getAndAdd(1));
		while (turn.get() != next)
			;
	}

	@Override
	public void releaseLock() {
		next++;
	}

}