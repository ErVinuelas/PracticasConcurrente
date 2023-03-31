import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Almacen {

    private final Lock l = new ReentrantLock(true);

    private final Condition okRead = l.newCondition();
    private final Condition okWrite = l.newCondition();

    private int[] numberReaderArray; 
    private int[] numberWriterArray;

	private Producto[] almacena;

	public Almacen(int capacidad) {
		almacena = new Producto[capacidad];

        numberReaderArray = new int[capacidad];
        numberWriterArray = new int[capacidad];

        for(int i = 0; i < capacidad; ++i) {
            numberReaderArray[i] = 0;
            numberWriterArray[i] = 0;
        }

        for(int i = capacidad - 1; i >= 0; --i){
            almacena[(capacidad - 1) - i] = new Producto(i);
        }
	}

	public void escribir(Producto producto, int pos) {
		almacena[pos] = producto;
	}

	public Producto leer(int pos) {
		Producto out = almacena[pos];
		return out;
	}

    /*Procesos que nos permitene sincronizar los lectores y escritores */
    public void solicitarLectura(int i) {
        l.lock();
        while(numberWriterArray[i] > 0) {
            try {
                okRead.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numberReaderArray[i]++;
        l.unlock();
    }

    public void terminarLectura(int i) {
        l.lock();
        numberReaderArray[i]--;
        if(numberReaderArray[i] == 0) {
            okWrite.signal();
        }
        l.unlock();
    }

    public void solicitarEscritura(int i) {
        l.lock();
        while(numberReaderArray[i] > 0 || numberWriterArray[i] > 0) {
            try {
                okWrite.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numberWriterArray[i]++;
        l.unlock();
    }

    public void terminarEscritura(int i) {
        l.lock();
        numberWriterArray[i]--;
        okWrite.signal();
        okRead.signalAll();
        l.unlock();
    }

}