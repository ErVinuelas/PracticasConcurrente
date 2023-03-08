import java.util.concurrent.Semaphore;

public class Reader extends Thread{

    /*Atributos de la clase necesarios para la concurrencia */
	private Semaphore testigo;
	private Semaphore reader;
    private Semaphore writer;

    private int numberReaders;
    private int delayedReaders;
    private int numberWriters;
    private int delayedWriters;

    /*Atributos circunstanciales */
	private Almacen almacen;
	private Producto res;
	private int id;

	public Reader(Semaphore testigo, Semaphore reader, Semaphore writer, int numberReaders, int delayedReaders, int numberWriters, int delayedWriters, Almacen almacen, int id) {
		this.testigo = testigo;
		this.reader = reader;
        this.writer = writer;
        this.numberReaders = numberReaders;
        this.delayedReaders = delayedReaders;
        this.numberWriters = numberWriters;
        this.delayedWriters = delayedWriters;

		this.almacen = almacen;
		this.id = id;
	}

	public void run() {
		while (true) {
			try {
                testigo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(numberWriters > 0) {
                
                delayedReaders = delayedReaders + 1;
                testigo.release();  //Paso testigo E

                try {
                    reader.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            numberReaders++;
            if(delayedReaders > 0)  {   delayedReaders = delayedReaders - 1;    reader.release();   }   //Si hay readers esperando, despierto en cadena
            else    {   testigo.release();  }   //Sino, libero el mutex

            almacen.leer(id);   //Elección arbitraria de la posición a leer

            try {
                testigo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            numberReaders--;
            if(numberReaders == 0 && delayedWriters > 0){
                delayedWriters--;
                writer.release(); //Paso testigo
            }
            else{
                testigo.release();
            }
		}
	}
}
