package data;
import java.util.HashMap;
import java.util.Map;
import locks.Lock;
import locks.LockTicketNoMaxSize;

public class DiccionarioFlujos {
    private Map<String, FlujosConcurrentes> tablaConcurrente;
    private Lock out;

    public DiccionarioFlujos() {
        this.tablaConcurrente = new HashMap<String, FlujosConcurrentes>();
        this.out = new LockTicketNoMaxSize();
    }

    public FlujosConcurrentes get(String key) {
        FlujosConcurrentes res = tablaConcurrente.get(key);
        return res;
    }

    public void put(String key, FlujosConcurrentes data) {
        out.takeLock(0);
        tablaConcurrente.put(key, data);
        out.releaseLock(0);
    }

    public boolean containsKey(String key) {
        return tablaConcurrente.containsKey(key);
    }

    public void remove(String key) {
        out.takeLock(0);
        tablaConcurrente.remove(key);
        out.releaseLock(0);
    }

}
