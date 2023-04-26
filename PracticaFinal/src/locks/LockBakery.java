package locks;
import java.util.ArrayList;

public class LockBakery extends Lock{

	private ArrayList<MiEntero> turn;
	
	private boolean op(int id1, int id2) {
		return turn.get(id1).get()>turn.get(id2).get() || (turn.get(id1).get()==turn.get(id2).get() && id1>id2);
	}
	
	public LockBakery(int N) {
		this.N = N;
		turn = new ArrayList<MiEntero>(N);
		for(int i=0;i<N;i++)
			turn.add(new MiEntero());
	}

	@Override
	public void takeLock(int id) {
		turn.get(id).set(1);
		for(int i=0;i<N;i++)
			if(turn.get(id).get()<turn.get(i).get())
				turn.get(id).set(turn.get(i).get());
		turn.get(id).incrementar();
		for(int i=0;i<N;i++) if(i!=id) {
			while(turn.get(i).get()!=0 && op(id,i));
		}
	}

	@Override
	public void releaseLock(int id) {
		turn.get(id).set(0);
	}

}
