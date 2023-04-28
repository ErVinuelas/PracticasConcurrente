package data;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiccionarioFlujos {
    private Map<String, FlujosConcurrentes> tablaConcurrente;
    private Lock out;

    public DiccionarioFlujos() {
        this.tablaConcurrente = new HashMap<String, FlujosConcurrentes>();
        this.out = new ReentrantLock(true);
    }

    public FlujosConcurrentes get(String key) {
        FlujosConcurrentes res = tablaConcurrente.get(key);
        return res;
    }

    public void put(String key, FlujosConcurrentes data) {
        out.lock();
        tablaConcurrente.put(key, data);
        out.unlock();
    }

    public boolean containsKey(String key) {
        return tablaConcurrente.containsKey(key);
    }

    public void remove(String key) {
        out.lock();
        tablaConcurrente.remove(key);
        out.unlock();
    }

}
