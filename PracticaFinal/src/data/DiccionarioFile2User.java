package data;

import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Set;

import lecturaEscritura.LecturaEscritura;
import lecturaEscritura.LecturaEscrituraMonitor;

public class DiccionarioFile2User {
	private Map<String, Set<String>> tablaConcurrente;
	private LecturaEscritura mecanismoConcurrencia;
	
	public DiccionarioFile2User() {
		this.tablaConcurrente = new TreeMap<String, Set<String>>();
		this.mecanismoConcurrencia = new LecturaEscrituraMonitor();
	}
	
	public void put(String key, Set<String> data) {
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
		tablaConcurrente.get(nombre).remove(key);
		if(tablaConcurrente.get(nombre).isEmpty()) {
			tablaConcurrente.remove(nombre);
		}
		mecanismoConcurrencia.releaseWrite();
	}

	public String getFirst(String nombre) {
		mecanismoConcurrencia.requestRead();
		String res = tablaConcurrente.get(nombre).iterator().next();
		mecanismoConcurrencia.releaseRead();
		return res;
	}
	
	public void add(String nombre, String key) {
		mecanismoConcurrencia.requestWrite();
		if(!tablaConcurrente.containsKey(nombre))
			tablaConcurrente.put(nombre, new HashSet<String>());
		tablaConcurrente.get(nombre).add(key);
		mecanismoConcurrencia.releaseWrite();
	}

	public Map<String, Set<String>> getTabla() {
		return this.tablaConcurrente;
	}
}
