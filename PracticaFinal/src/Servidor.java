import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data.Usuario;

public class Servidor {

	protected ServerSocket ss;

	//Tenemos tres tablas de datos que tenemos que proteger mediante un monitor
	//Hay una cola por cada tabla
	
	//Tablas
	public volatile TreeMap<String, Usuario> userLst = new TreeMap<String, Usuario>();
	public volatile TreeMap<String, Flujos> flujoLst = new TreeMap<String, Flujos>();

	public volatile TreeMap<String, Set<String>> fileToUser = new TreeMap<String, Set<String>>();
	
	//Variables condicionales y lock
	
	private final Lock userLock = new ReentrantLock(true);
	private final Lock flujoLock = new ReentrantLock(true);
	private final Lock fileToUserLock = new ReentrantLock(true);

	//Tenemos un variable condicional por cada grupo de lectores o escritores en cada tabla
	
	private final Condition cmdReaderUser = userLock.newCondition();
	private final Condition cmdWriterUser = userLock.newCondition();
	
	private final Condition cmdReaderFlujo = flujoLock.newCondition();
	private final Condition cmdWriterFlujo = flujoLock.newCondition();
	
	private final Condition cmdReaderFileToUser = fileToUserLock.newCondition();
	private final Condition cmdWriterFileToUser = fileToUserLock.newCondition();
	
	//Contadores del número de lectores y escritores para cada proceso
	
	private int numReaderUser = 0, numWriterUser = 0, numReaderFlujo = 0, numWriterFlujo = 0, numReaderFileToUser = 0, numWriterFileToUser = 0;

	public Servidor(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Servidor serv = new Servidor(4200);
		serv.listen();
	}

	public void listen() {
		int i = 0;
		while (true) {
			Socket sc;
			try {
				sc = ss.accept();
				OyenteCliente oc = new OyenteCliente(sc, this);
				i++;
				oc.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Procesos para manejar la concurrencia
	
	public void solicitarLecturaUsuarios() {
		userLock.lock();
		
		while(numWriterUser > 0) {
			try {
				cmdReaderUser.await();
			}	catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		numReaderUser++;
		userLock.unlock();
	}
	
	public void solicitarLecturaFlujo() {
		flujoLock.lock();
		
		while(numWriterFlujo > 0) {
			try {
				cmdReaderFlujo.await();
			}	catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		numReaderFlujo++;
		flujoLock.unlock();
	}
	
	public void solicitarLecturaFileToUser() {
		fileToUserLock.lock();
		
		while(numWriterFileToUser > 0) {
			try {
				cmdReaderFileToUser.await();
			}	catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		numReaderFileToUser++;
		fileToUserLock.unlock();
	}
	
	public void terminarLecturaUser() {
		userLock.lock();
		
		numReaderUser--;
		if(numReaderUser == 0) {
			cmdWriterUser.signal();
		}
		
		userLock.unlock();
	}
	
	public void terminarLecturaFlujo() {
		flujoLock.lock();
		
		numReaderFlujo--;
		if(numReaderFlujo == 0) {
			cmdWriterFlujo.signal();
		}
		
		flujoLock.unlock();
	}
	
	public void terminarLecturaFileToUser() {
		fileToUserLock.lock();
		
		numReaderFileToUser--;
		if(numReaderFileToUser == 0) {
			cmdWriterFileToUser.signal();
		}
		
		fileToUserLock.unlock();
	}
	
	public void solicitarEscrituraUser() {
		userLock.lock();
        while(numReaderUser > 0 || numWriterUser > 0) {
            try {
                cmdWriterUser.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numWriterUser++;
        userLock.unlock();
	}
	
	public void solicitarEscrituraFlujo() {
		flujoLock.lock();
        while(numReaderFlujo > 0 || numWriterFlujo > 0) {
            try {
                cmdWriterFlujo.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numWriterFlujo++;
        flujoLock.unlock();
	}
	
	public void solicitarEscrituraFileToUser() {
		flujoLock.lock();
        while(numReaderFileToUser > 0 || numWriterFileToUser > 0) {
            try {
                cmdWriterFileToUser.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numWriterFileToUser++;
        fileToUserLock.unlock();
	}
	
	public void terminarEscrituraUser() {
		userLock.lock();
        numWriterUser--;
        cmdWriterUser.signal();
        cmdReaderUser.signalAll();
        userLock.unlock();
	}
	
	public void terminarEscrituraFlujo() {
		flujoLock.lock();
        numWriterFlujo--;
        cmdWriterFlujo.signal();
        cmdReaderFlujo.signalAll();
        flujoLock.unlock();
	}
	
	public void terminarEscrituraFileToUser() {
		flujoLock.lock();
        numWriterFlujo--;
        cmdWriterFlujo.signal();
        cmdReaderFlujo.signalAll();
        flujoLock.unlock();
	}

}
