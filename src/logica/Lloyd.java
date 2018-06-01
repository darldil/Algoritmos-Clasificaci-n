package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dao.LectorFicheros;

public class Lloyd implements Algoritmo {
	
	private final float TOLERANCIA = (float) 0.0000000001;
	private final int MAXIT = 10;
	private final float RAPR = 0.1f;
	private LectorFicheros dao;
	private Map<String, ArrayList<ArrayList<Float>>> ListaCasos;
	private Map<String, ArrayList<Float>> centros;
	
	public Lloyd() {
		dao = new LectorFicheros();
		ListaCasos = new HashMap<String, ArrayList<ArrayList<Float>>>();
		centros = new HashMap<String, ArrayList<Float>>();
		initValoresIni();
	}

	@Override
	public Object cargarDatos(TransferArchivos transfer) throws Exception {
		if (transfer.getTipo().equals("Casos")) {
			ListaCasos = dao.leerDatos(transfer.getRuta() + transfer.getNombre());
			return null;
		}
		else {
			return dao.leerDatos(transfer.getRuta() + transfer.getNombre());
		}
	}
	
	private void initValoresIni() {
		ArrayList<Float> temp1 = new ArrayList<Float>();
		ArrayList<Float> temp2 = new ArrayList<Float>();
		
		temp1.add((float) 4.6); // centro iris setosa
		temp1.add((float) 3.0);
		temp1.add((float) 4.0);
		temp1.add((float) 0);
		temp2.add((float) 6.8); // centro iris versicolor
		temp2.add((float) 3.4);
		temp2.add((float) 4.6);
		temp2.add((float) 0.7);
		
		centros.put("Iris-versicolor", temp2);
		centros.put("Iris-setosa", temp1);
	}

	@Override
	public String buscarResultado(Object datos) {
		@SuppressWarnings("unchecked")
		ArrayList<Float> datosArchivo = (ArrayList<Float>) datos;
		Map<String, Float> resultado = new HashMap<String, Float>();
		for (String index: centros.keySet()) {
			float temp = 0f;
			for (int i = 0; i < centros.get(index).size(); i++) {
				temp += ((datosArchivo.get(i) - centros.get(index).get(i)) *
						(datosArchivo.get(i) - centros.get(index).get(i)));
			}
			resultado.put(index, temp);
		}
		
		float maxValueInMap=(Collections.min(resultado.values()));  
        for (Entry<String, Float> entry : resultado.entrySet()) {  
            if (entry.getValue()==maxValueInMap)
                return entry.getKey();
        }
        
		return null;
	}

	@Override
	public void procesar() {
		Boolean seguir = true;
		int n = 0; 
		while (seguir && n < MAXIT) {
			Map<String, ArrayList<Float>> nuevosCentros = new HashMap<String, ArrayList<Float>>();
			for (String caso: ListaCasos.keySet()) {
				nuevosCentros.put(caso, actualizarCentro(centros.get(caso), ListaCasos.get(caso), ListaCasos.size()));
			}
			seguir = continuar(nuevosCentros);
			centros = nuevosCentros;
			n++;
		}
	} 
	
	private ArrayList<Float> actualizarCentro(ArrayList<Float> c, ArrayList<ArrayList<Float>> muestra, int dim) {
		ArrayList<Float> resultado = new ArrayList<Float>(c);
		
		for (int n = 0; n < muestra.size(); n++) {
			ArrayList<Float> temp = new ArrayList<Float>();
			for (int f = 0; f < muestra.get(n).size(); f++)
				temp.add(RAPR * (muestra.get(n).get(f) - resultado.get(f)));

			for (int i = 0; i < temp.size(); i++) {
				resultado.set(i,(temp.get(i) + resultado.get(i)));
			}
		}
		return resultado;
	}
	
	private Boolean continuar(Map<String, ArrayList<Float>> centroNuevo ) {
		
		for (String index: this.centros.keySet()) {
			float aux = 0;
			for (int i = 0; i < centroNuevo.get(index).size(); i++)
				aux += (centroNuevo.get(index).get(i) - centros.get(index).get(i)) * (centroNuevo.get(index).get(i) - centros.get(index).get(i));
			if (aux > TOLERANCIA)  return true;
		}
		
		return false;
	}

}
