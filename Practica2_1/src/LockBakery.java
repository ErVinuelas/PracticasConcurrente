
public class LockBakery extends Lock{

	private int[] turn;
	
	private boolean op(int id1, int id2) {
		return turn[id1]>turn[id2] || (turn[id1]==turn[id2] && id1>id2);
	}
	
	public LockBakery(int N) {
		super(N);
		turn = new int[N];
	}

	@Override
	public void takeLock(int id) {
		turn[id]=1;
		for(int i=0;i<N;i++)
			if(turn[id]<turn[i])
				turn[id]=turn[i];
		turn[id]++;
		for(int i=0;i<N;i++) if(i!=id) {
			while(turn[i]!=0 && !op(id,i));
		}
	}

	@Override
	public void releaseLock(int id) {
		turn[id] = 0;
	}

}
