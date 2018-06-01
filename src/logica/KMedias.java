package logica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dao.LectorFicheros;

public class KMedias implements Algoritmo{

	private final float TOLERANCIA = (float) 0.01;
	private final int PEXP = 2;
	private LectorFicheros dao;
	private Map<String, ArrayList<Float>> centros;
	private Map<String, ArrayList<ArrayList<Float>>> ListaCasos;
	
	public KMedias() {
		dao = new LectorFicheros();
		centros = new HashMap<String, ArrayList<Float>>();
		ListaCasos = new HashMap<String, ArrayList<ArrayList<Float>>>();
		initValoresIni();
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
		ArrayList<Float> centrosArchivo = (ArrayList<Float>) datos;
		ArrayList<Float> distancias = new ArrayList<Float>();
		Map<String, Float> mapatemporal = calcularDistanciaEuclidea(centrosArchivo);
		for (String index: mapatemporal.keySet())
			distancias.add(mapatemporal.get(index));
		ArrayList<Float> gradosPertenencia = calcularGradosPertenencia(distancias);
		
		if(gradosPertenencia.get(0) > gradosPertenencia.get(1)) return "Iris-versicolor";
		else return "Iris-setosa";
		
	}
	
	public void procesar() {
		Boolean seguir = true;
		ArrayList<Float> centrosTemporalesBuenos = null;
			
		for (String caso: ListaCasos.keySet()) {
			while (seguir) {
				ArrayList<ArrayList<Float>> U = new ArrayList<ArrayList<Float>>();
				ArrayList<Float> distancias = new ArrayList<Float>();
				for (ArrayList<Float> temp: ListaCasos.get(caso)) {
					Map<String, Float> mapatemporal = calcularDistanciaEuclidea(temp);
					for (String index: mapatemporal.keySet())
						distancias.add(mapatemporal.get(index));
					ArrayList<Float> temporal = calcularGradosPertenencia(distancias);
					U.add(temporal);
					distancias.clear();
				}
				Map<String, ArrayList<Float>> centrosNuevos = calcularNuevosCentros(U, ListaCasos.get(caso));
				seguir = calcularCriterioFinalizacion(centros, centrosNuevos);
				centros = centrosNuevos;
			}
			if (!caso.equals("Iris-setosa")) {
				centrosTemporalesBuenos = centros.get(caso);
				initValoresIni();
				centros.put(caso, centrosTemporalesBuenos);
				seguir = true;
			}
			else {
				centros.put(caso, centros.get("Iris-versicolor"));
			}
		}
		centros.put("Iris-versicolor", centrosTemporalesBuenos);
	} 
	
	private Map<String, Float> calcularDistanciaEuclidea(ArrayList<Float> muestra) {
		Map<String, Float> resultado = new HashMap<String, Float>();
		
		for (String index: this.centros.keySet()) {
			float aux = 0;
			for (int i = 0; i < muestra.size(); i++) {
				//aux += Math.pow((muestra.get(i) - centros.get(i)), 2) ;
				aux += (muestra.get(i) - centros.get(index).get(i)) * (muestra.get(i) - centros.get(index).get(i));
			}
			resultado.put(index,((float) Math.sqrt(aux)));
		}
		
		return resultado;
	}
	
	private ArrayList<Float> calcularGradosPertenencia(ArrayList<Float> distancias) {
		ArrayList<Float> resultado = new ArrayList<Float>();
		float sumatorio =  0f;
		
		for (Float distancia: distancias)
			sumatorio += Math.pow((1/distancia),(1/(PEXP-1)));
		
		for (Float distancia: distancias)
			resultado.add((float) ((Math.pow((1/distancia),(1/(PEXP-1)))) / sumatorio));
		
		return resultado;
	}
	
	private Map<String, ArrayList<Float>> calcularNuevosCentros(ArrayList<ArrayList<Float>> U, 
			ArrayList<ArrayList<Float>> ListaCasos) {
		Map<String, ArrayList<Float>> resultado = new HashMap<String, ArrayList<Float>>();
		ArrayList<Float> temp = new ArrayList<Float>();
		float sumatorio1 = 0f, sumatorio2 = 0f;
		int i = 0;
		
		for (String casos: centros.keySet()) {
			temp = new ArrayList<Float>();
			for (ArrayList<Float> Pi: U)
				sumatorio1 += (Pi.get(i));
				
			for (int n = 0; n < ListaCasos.get(i).size(); n++) {
				for (ArrayList<Float> Pi: U) {
					sumatorio2 = 0f;
					for (ArrayList<Float> caso: ListaCasos) {
						//sumatorio2 += Math.pow((Pi.get(i)),(PEXP)) * caso.get(n);
						sumatorio2 += Pi.get(i) * caso.get(n);
					}
				}
				temp.add(sumatorio2/sumatorio1);
			}
			resultado.put(casos,temp);
			++i;
		}
		
		return resultado;
	}
	
	private boolean calcularCriterioFinalizacion(Map<String, ArrayList<Float>> centrosViejos,
			Map<String, ArrayList<Float>> centrosNuevos) {
		
		for (String caso: centrosViejos.keySet()) {
			for (int i = 0; i < centrosViejos.size(); i++) {
				ArrayList<Float> centroViejo = centrosViejos.get(caso);
				ArrayList<Float> centroNuevo = centrosNuevos.get(caso);
				float resultado = 0f;
				for (int n = 0; n < centroViejo.size(); n++)
					resultado += (float) Math.pow(centroViejo.get(n) - centroNuevo.get(n),2);
				if (Math.sqrt(resultado) > TOLERANCIA) return true;
			}
		}
		
		
		return false;
	}
	
}
