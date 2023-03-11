    import java.util.concurrent.Semaphore;

public class Writer extends Thread {
    /*Atributos de la clase necesarios para la concurrencia */
	private Semaphore testigo;
	private Semaphore reader;
    private Semaphore writer;

    private int numberReaders;
    private int delayedReaders;
    private int numberWriters;
    private int delayedWriters;

    /*Atribuotos circunstaciales */
	private Almacen almacen;
	private int id;

	public Writer(Semaphore testigo, Semaphore reader, Semaphore writer, int numberReaders, int delayedReaders, int numberWriters, int delayedWriters, Almacen almacen, int id) {
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

            if(numberReaders > 0 || numberWriters > 0) {
                delayedWriters++;
                testigo.release();
                try {
                    writer.acquire();                   //Paso de testigo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            numberWriters++;    //Tengo el mutex testigo
            testigo.release();  //Liberamos mutex

            almacen.escribir(new Producto(id), id);
            System.out.println("El Reader " + id + " ha escrito " + id);

            try {
                testigo.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            numberWriters--;
            if(delayedWriters > 0){
                delayedReaders--;
                reader.release();   //Paso del testigo a un lector
            } else{
                testigo.release();  //Soltamos el mutex
            }

		}
	}

}
