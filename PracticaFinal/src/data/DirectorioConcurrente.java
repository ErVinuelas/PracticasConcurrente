package data;
import java.util.HashMap;
import java.util.Map;

import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraSemaforo;
import locks.Lock;

public class DirectorioConcurrente {

	private Map<String,LecturaEscritura> conc;
	private Map<String,String> info;
	
	public DirectorioConcurrente() {
		conc = new HashMap<String,LecturaEscritura>();
		info = new HashMap<String, String>();
	}
	
	public void put(String key, String data) {
		//No puede haber mas de un escritor con lo que esto es seguro
		if(!conc.containsKey(key)) {
			conc.put(key, new LecturaEscrituraSemaforo());
		}
		conc.get(key).requestWrite();
		info.put(key, data);
		conc.get(key).releaseWrite();
	}
	
	public String get(String key) {
		String out;
		conc.get(key).requestRead();
		out = info.get(key);
		conc.get(key).releaseRead();
		return out;
	}

}
