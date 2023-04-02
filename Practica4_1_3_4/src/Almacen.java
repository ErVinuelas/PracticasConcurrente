public class Almacen {
	/**
	* Almacena (como ultimo) un producto en el almacén. Si no hay
	* hueco el proceso que ejecute el m ́etodo bloquear ́a hasta que lo
	* haya.
	*/
	private Producto[] almacena;
	
	private int numberReaders;
    private int delayedReaders;
    private int numberWriters;
    private int delayedWriters;
    private int blocked; //1 writer, 2 reader
	
	public Almacen(int capacidad) {
		almacena = new Producto[capacidad];
		this.numberReaders = 0;
        this.delayedReaders = 0;
        this.numberWriters = 0;
        this.delayedWriters = 0;
        this.blocked=0;
	}

	public synchronized Producto editar(Producto producto, int pos) {
		if(producto==null) {
			
			Producto res;
			
			if(numberWriters > 0) {
                
                delayedReaders++;

                while(numberWriters > 0 && blocked!=2)
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                
            }

            numberReaders++;
            if(delayedReaders > 0)  {   this.notifyAll();   }   //Si hay readers esperando, despierto en cadena

            res = this.leer(pos);   //Elección arbitraria de la posición a leer
            System.out.println("El Reader " + pos + " ha leido " + res.getVal());

            numberReaders--;
            if(numberReaders == 0 && delayedWriters > 0){
            	delayedWriters--;
            	blocked=2;
                this.notifyAll();
            }
            else {
            	blocked=0;
            }
            return res;
		}
		else {
			if(numberReaders > 0 || numberWriters > 0) {
                delayedWriters++;
                while((numberReaders > 0 || numberWriters > 0) && blocked!=1)
                	try {
        				this.wait();
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
            }
			numberWriters++;
			escribir(producto, pos);
			numberWriters--;
            if(delayedWriters > 0){
            	delayedReaders--;
            	blocked=1;
                this.notifyAll();
            }
            else {
            	blocked=0;
            }
            return null;
		}
	}
	
	private synchronized void escribir(Producto producto, int pos) {
		almacena[pos] = producto;
	}
	/**
	* Extrae el primer producto disponible. Si no hay productos el
	* proceso que ejecute el m ́etodo bloquear ́a hasta que se almacene un
	* dato.
	*/
	private Producto leer(int pos) {

        Producto out = almacena[pos];
        //System.out.println("El Reader " + id + " ha leido " + res.getVal());
		return out;
	}

}