package data;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraSemaforo;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import locks.LockTicketNoMaxSize;

public class FlujosConcurrentes {

	private Flujos flujos;
    private Lock out;
	
	public FlujosConcurrentes(ObjectInputStream fIn, ObjectOutputStream fOut) {
        this.flujos = new Flujos(fIn, fOut);
        this.out = new ReentrantLock(true);
    }
	
	public void writeObject(Object data) throws IOException {
        out.lock();
        flujos.getFout().flush();
		flujos.getFout().reset();
		flujos.getFout().writeObject(data);
        out.unlock();
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		return flujos.getFin().readObject();
	}

    public void close() throws IOException{
        flujos.getFout().close();
        flujos.getFin().close();
    }
}
