package data;

import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraMonitor;

public class DiccionarioConcurrente<K, D> {
	private Map<K, D> tablaConcurrente;
	private LecturaEscritura mecanismoConcurrencia;
	
	public DiccionarioConcurrente(Map<K, D> tabla) {
		this.tablaConcurrente = tabla;
		this.mecanismoConcurrencia = new LecturaEscrituraMonitor();
	}
	
	public D get(K key) {
		mecanismoConcurrencia.requestRead();
		D res = tablaConcurrente.get(key);
		mecanismoConcurrencia.releaseRead();
		return res;
	}
	
	public void put(K key, D data) {
		mecanismoConcurrencia.requestWrite();
		tablaConcurrente.put(key, data);
		mecanismoConcurrencia.releaseWrite();
	}

	public boolean containsKey(String arc) {
		mecanismoConcurrencia.requestRead();
		tablaConcurrente.containsKey(arc);
		mecanismoConcurrencia.releaseRead();
		return false;
	}

	public void remove(String nombre) {
		mecanismoConcurrencia.requestWrite();
		tablaConcurrente.remove(nombre);
		mecanismoConcurrencia.releaseWrite();
	}

	public void remove(String nombre, String key) {
		mecanismoConcurrencia.requestWrite();
		((Collection)(tablaConcurrente.get(nombre))).remove(key);
		if(((Collection)(tablaConcurrente.get(nombre))).isEmpty()) {
			tablaConcurrente.remove(nombre);
		}
		mecanismoConcurrencia.releaseWrite();
	}

	public String getFirst(String nombre) {
		mecanismoConcurrencia.requestRead();
		String res = (String)(((Collection<D>)(tablaConcurrente.get(nombre))).iterator().next());
		mecanismoConcurrencia.releaseRead();
		return res;
	}
	
	public void add(String nombre, String key) {
		mecanismoConcurrencia.requestWrite();
		if(!tablaConcurrente.containsKey(nombre))
			tablaConcurrente.put((K)nombre, (D)(new HashSet()));
		((Collection)(tablaConcurrente.get(nombre))).add(key);
		mecanismoConcurrencia.releaseWrite();
	}

	public Map<K,D> getTabla() {
		return this.tablaConcurrente;
	}
}
