package lecturaEscritura;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LecturaEscrituraMonitor implements LecturaEscritura {

	private final Lock l = new ReentrantLock(true);
	
	//Tenemos un variable condicional por cada grupo de lectores o escritores en cada tabla
	
	private final Condition cmdReader = l.newCondition();
	private final Condition cmdWriter = l.newCondition();
	
	private int numReader = 0, numWriter = 0;
	
	Object buffer;

	public LecturaEscrituraMonitor(Object o) {
		buffer = 0;
		
	}

	@Override
	public void write(Object o) {
		
	}

	@Override
	public Object read() {
		
		return null;
	}
	
	//Procesos para manejar la concurrencia
	
	public void solicitarLectura() {
		l.lock();
		
		while(numWriter > 0) {
			try {
				cmdReader.await();
			}	catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		numReader++;
		l.unlock();
	}
	
	public void terminarLectura() {
		l.lock();
		
		numReader--;
		if(numReader == 0) {
			cmdWriter.signal();
		}
		
		l.unlock();
	}
	
	public void solicitarEscritura() {
		l.lock();
        while(numReader > 0 || numWriter > 0) {
            try {
                cmdWriter.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numWriter++;
        l.unlock();
	}
	
	public void terminarEscritura() {
		l.lock();
        numWriter--;
        cmdWriter.signal();
        cmdReader.signalAll();
        l.unlock();
	}

}
