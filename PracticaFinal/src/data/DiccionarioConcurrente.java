package data;

import java.util.Map;

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
	
	public Map<K,D> getTabla() {
		return this.tablaConcurrente;
	}
}
