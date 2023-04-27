package data;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraSemaforo;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import locks.Lock;
import locks.LockTicketNoMaxSize;

public class FlujosConcurrentes {

	private Flujos flujos;
    private Lock out;
	
	public FlujosConcurrentes(ObjectInputStream fIn, ObjectOutputStream fOut) {
        this.flujos = new Flujos(fIn, fOut);
        this.out = new LockTicketNoMaxSize();
    }
	
	public void writeObject(Object data) throws IOException {
        out.takeLock(0);
        flujos.getFout().flush();
		flujos.getFout().reset();
		flujos.getFout().writeObject(data);
        out.releaseLock(0);
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		return flujos.getFin().readObject();
	}

    public void close() throws IOException{
        flujos.getFout().close();
        flujos.getFin().close();
    }
}
