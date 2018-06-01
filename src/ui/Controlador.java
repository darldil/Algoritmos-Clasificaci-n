package ui;

import java.util.ArrayList;
import java.util.Map;

import logica.Algoritmo;
import logica.TransferArchivos;

public class Controlador {
	
	private WindowController ui;
	private Estados state;
	private Algoritmo algoritmo;
	private String resultado;

	public Estados getState() {
		return state;
	}

	public void setState(Estados state) {
		this.state = state;
	}

	public void setAlgoritmo(Algoritmo algoritmo) {
		this.algoritmo = algoritmo;
	}

	public void setUi(WindowController ui) {
		this.ui = ui;
	}
	
	@SuppressWarnings("unchecked")
	public void accion(Object datos) throws Exception {
		
		switch(state) {
			case LEER_CASOS: 
				algoritmo.cargarDatos((TransferArchivos)datos); 
				setState(Estados.PROCESAR);
				break;
			case PROCESAR:
				algoritmo.procesar();
				setState(Estados.LEER_EJEMPLO);
				break;
			case LEER_EJEMPLO:
				Map<String, ArrayList<ArrayList<Float>>> temp = (Map<String, ArrayList<ArrayList<Float>>>) algoritmo.cargarDatos((TransferArchivos)datos); 
				for (String caso: temp.keySet()) {
					resultado = algoritmo.buscarResultado(temp.get(caso).get(0));
				}
				setState(Estados.FIN);
			default: break;
		}
		
		ui.update();
	}

	public String getResultado() {
		return resultado;
	}

}
