package logica;

public interface Algoritmo {
	
	abstract public Object cargarDatos(TransferArchivos transfer) throws Exception;
	
	abstract public String buscarResultado(Object datos);
	
	abstract public void procesar();

}
