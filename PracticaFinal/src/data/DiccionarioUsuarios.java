package data;

import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraMonitor;

public class DiccionarioUsuarios{
	private Map<String, Usuario> tablaConcurrente;
	private LecturaEscritura mecanismoConcurrencia;
	
	public DiccionarioUsuarios() {
		this.tablaConcurrente = new TreeMap<String, Usuario>();
		this.mecanismoConcurrencia = new LecturaEscrituraMonitor();
	}
	
	public Usuario get(String key) {
		mecanismoConcurrencia.requestRead();
		Usuario res = tablaConcurrente.get(key);
		mecanismoConcurrencia.releaseRead();
		return res;
	}
	
	public void put(String key, Usuario data) {
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

	public Map<String, Usuario> getTabla() {
		return this.tablaConcurrente;
	}
}