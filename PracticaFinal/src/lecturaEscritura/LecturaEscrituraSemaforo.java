package lecturaEscritura;

import java.util.concurrent.Semaphore;

public class LecturaEscrituraSemaforo implements LecturaEscritura {

	private Semaphore testigo;
	private Semaphore reader;
	private Semaphore writer;

	private int numberReaders;
	private int delayedReaders;
	private int numberWriters;
	private int delayedWriters;
	
	public LecturaEscrituraSemaforo() {
		numberReaders = 0;
		delayedReaders = 0;
		numberWriters = 0;
		delayedWriters = 0;

		testigo = new Semaphore(1);
		reader = new Semaphore(0);
		writer = new Semaphore(0);
	}

	@Override
	public void requestWrite() {
		try {	testigo.acquire();	} catch (InterruptedException e) {	e.printStackTrace();	}

		if (numberReaders > 0 || numberWriters > 0) {
			delayedWriters++;
			testigo.release();
			try {	writer.acquire(); // Paso de testigo
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		numberWriters++; // Tengo el mutex testigo
		testigo.release(); // Liberamos mutex
	}
	
	@Override
	public void releaseWrite() {

		try {
			testigo.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		numberWriters--;
		if (delayedWriters > 0) {
			delayedReaders--;
			reader.release(); // Paso del testigo a un lector
		} else {
			testigo.release(); // Soltamos el mutex
		}
	}

	@Override
	public void requestRead() {
		try {
			testigo.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (numberWriters > 0) {

			delayedReaders = delayedReaders + 1;
			testigo.release(); // Paso testigo E

			try {
				reader.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		numberReaders++;
		if (delayedReaders > 0) {
			delayedReaders = delayedReaders - 1;
			reader.release();
		} // Si hay readers esperando, despierto en cadena
		else {
			testigo.release();
		} // Sino, libero el mutex

	}
	
	@Override
	public void releaseRead() {

		try {
			testigo.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		numberReaders--;
		if (numberReaders == 0 && delayedWriters > 0) {
			delayedWriters--;
			writer.release(); // Paso testigo
		} else {
			testigo.release();
		}
	}

}
