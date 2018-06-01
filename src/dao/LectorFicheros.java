package dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorFicheros {
	
	public Map<String, ArrayList<ArrayList<Float>>> leerDatos(String archivo) throws FileNotFoundException, IOException {
	    Map<String, ArrayList<ArrayList<Float>>> resultado = new HashMap<String, ArrayList<ArrayList<Float>>>();
		String cadena;
	    FileReader f = new FileReader(archivo);
	    BufferedReader b = new BufferedReader(f);
	    while((cadena = b.readLine())!=null) {
	    	ArrayList<Float> lista = new ArrayList<Float>();
	    	List<String> aux = Arrays.asList(cadena.split(","));
	    	String nombreClase = aux.get(aux.size() - 1);
	    	
	    	for(int i = 0; i < aux.size() - 1; i++){
	    		String s =  aux.get(i);
	    	    float res = Float.parseFloat(s);
		    	lista.add(res);
	    	}
	    	
	    	if (resultado.containsKey(nombreClase)) {
	    		ArrayList<ArrayList<Float>> temp = resultado.get(nombreClase);
	    		temp.add(lista);
		    	resultado.put(nombreClase, temp);
	    	}
		    else {
		    	ArrayList<ArrayList<Float>> temp = new ArrayList<ArrayList<Float>>();
	    		temp.add(lista);
		    	resultado.put(nombreClase, temp);
			}
	    }
	    b.close();
	      
	    return resultado;
	}

}
