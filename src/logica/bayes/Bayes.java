package logica.bayes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dao.LectorFicheros;
import logica.Algoritmo;
import logica.TransferArchivos;

public class Bayes implements Algoritmo{

	private LectorFicheros dao;
	private Map<String, ArrayList<ArrayList<Float>>> ListaCasos;
	private Map<String, ArrayList<Float>> m;
	private Map<String, ArrayList<ArrayList<Float>>> c;
	
	public Bayes() {
		dao = new LectorFicheros();
		ListaCasos = new HashMap<String, ArrayList<ArrayList<Float>>>();
		c = new HashMap<String, ArrayList<ArrayList<Float>>>();
		m = new HashMap<String, ArrayList<Float>>();
	}
	
	public Object cargarDatos(TransferArchivos transfer) throws Exception {
		if (transfer.getTipo().equals("Casos")) {
			ListaCasos = dao.leerDatos(transfer.getRuta() + transfer.getNombre());
			return null;
		}
		else {
			return dao.leerDatos(transfer.getRuta() + transfer.getNombre());
		}
	}
	
	public String buscarResultado(Object datos) {
		@SuppressWarnings("unchecked")
		ArrayList<Float> datosArchivo = (ArrayList<Float>) datos;
		Map<String, Float> resultado = new HashMap<String, Float>();
		for (String index: ListaCasos.keySet())
			resultado.put(index, calculoMaxVerosimilitud(datosArchivo, index));
		
		float maxValueInMap=(Collections.min(resultado.values()));  
        for (Entry<String, Float> entry : resultado.entrySet()) {  
            if (entry.getValue()==maxValueInMap)
                return entry.getKey();
        }
        
		return null;
	}
	
	public void procesar() {
			
		for (String caso: ListaCasos.keySet()) {
			int muestras = ListaCasos.get(caso).size();
			m.put(caso, calcularM(caso, muestras));
			c.put(caso, calcularC(caso, m.get(caso), muestras));
		}
	} 
	
	private ArrayList<Float> calcularM(String caso, int totalMuestras) {
		ArrayList<Float> m = new ArrayList<Float>();
		int n = 0;
		float x = 0f;
		
		while (n < ListaCasos.get(caso).get(0).size()) {
			for (ArrayList<Float> linea: ListaCasos.get(caso))
				x += linea.get(n);
			m.add(x/totalMuestras);
			x = 0f;
			++n;
		}
		
		return m;
	}
	
	private ArrayList<ArrayList<Float>> calcularC(String caso, ArrayList<Float> m, int totalMuestras) {
		ArrayList<ArrayList<Float>> c = new ArrayList<ArrayList<Float>>();
		
		for (int fil = 0; fil < m.size(); fil++) {
			ArrayList<Float> temp = new ArrayList<Float>();
			for (int col = 0; col < m.size(); col++)
				temp.add(0f);
			c.add(temp);
		}
		
		for (ArrayList<Float> linea: ListaCasos.get(caso)) {
			ArrayList<Float> temp = new ArrayList<Float>();
			for (int i = 0; i < linea.size(); i++)
				temp.add(linea.get(i) - m.get(i));
			
			for (int fil = 0; fil < temp.size(); fil++) {
				ArrayList<Float> filasActuales = c.get(fil);
				int n = 0;
				for (int col = 0; col < filasActuales.size(); col++) {
					filasActuales.set(col, filasActuales.get(col) + (temp.get(fil)*temp.get(n)));
					n++;
				}
				c.set(fil, filasActuales);
			}
		}
		
		for (int fil = 0; fil < c.size(); fil++) {
			ArrayList<Float> filasActuales = c.get(fil);
			for (int col = 0; col < filasActuales.size(); col++)
				filasActuales.set(col, filasActuales.get(col)/5);
			c.set(fil, filasActuales);
		}
		
		return c;
	}
	
	private float calculoMaxVerosimilitud(ArrayList<Float> x, String caso) {
		Matrices operadorMatr = new Matrices();
		float detC = operadorMatr.determinante(c.get(caso));
		ArrayList<ArrayList<Float>> cInv = operadorMatr.matrizInversa(c.get(caso));
		float p1 = 0f; //Parte 1 de la formula
		float p2 = 0f; //Parte 2 de la formula
		
		p1 = (float) (1 / (Math.pow((2*3.14),((float) m.size()/2)) * Math.sqrt(detC)));
		p2 = calcularMatrizExp(x, m.get(caso), m.get(caso).size(), cInv);
		
		return (float) (p1 * Math.pow((float) 2.71828, p2));
		
	}
	
	private float calcularMatrizExp(ArrayList<Float> x, ArrayList<Float> m, 
			int dim, ArrayList<ArrayList<Float>> cInv) {
		float resultado = 0f;
		ArrayList<Float> x1 = new ArrayList<Float>(x);
		ArrayList<Float> matriz1 = new ArrayList<Float>();
		
		for (int i = 0; i < dim; i++)
			x1.set(i, (x1.get(i) - m.get(i)));
		
		for (int c = 0; c < cInv.size(); c++) {
			float temp = 0f;
			for (int f = 0; f < cInv.size(); f++)
				temp += cInv.get(f).get(c) * x1.get(f);
			matriz1.add(temp);
		}
		
		for (int c = 0; c < matriz1.size(); c++) 
			resultado += x1.get(c) * matriz1.get(c);
		
		return resultado;
	}
	
}
