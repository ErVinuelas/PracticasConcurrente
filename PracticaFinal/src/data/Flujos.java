package data;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Flujos {
	private ObjectInputStream fin;
	private ObjectOutputStream fout;

	public Flujos(ObjectInputStream fin, ObjectOutputStream fout) {
		this.fin = fin;
		this.fout = fout;
	}

	public ObjectInputStream getFin() {
		return fin;
	}

	public ObjectOutputStream getFout() {
		return fout;
	}
}