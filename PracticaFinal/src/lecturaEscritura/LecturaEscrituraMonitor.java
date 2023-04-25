package lecturaEscritura;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LecturaEscrituraMonitor implements LecturaEscritura {

	private final Lock l;
	
	//Tenemos un variable condicional por cada grupo de lectores o escritores 
	
	private final Condition cmdReader;
	private final Condition cmdWriter;
	
	private int numReader, numWriter;
	
	public LecturaEscrituraMonitor() {
		this.l = new ReentrantLock(true);
		this.cmdReader = l.newCondition();
		this.cmdWriter = l.newCondition();
		this.numReader = 0;
		this.numWriter = 0;
	}
	
	//Procesos para manejar la concurrencia
	
	public void requestRead() {
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
	
	public void releaseRead() {
		l.lock();
		
		numReader--;
		if(numReader == 0) {
			cmdWriter.signal();
		}
		
		l.unlock();
	}
	
	public void requestWrite() {
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
	
	public void releaseWrite() {
		l.lock();
        numWriter--;
        cmdWriter.signal();
        cmdReader.signalAll();
        l.unlock();
	}

}
