package logica.bayes;

import java.util.ArrayList;

public class Matrices {
	
	public ArrayList<ArrayList<Float>> inicializarMatriz(int dim) {
		ArrayList<ArrayList<Float>> matriz = new ArrayList<ArrayList<Float>>();
		for(int f=0; f < dim; f++) {
			ArrayList<Float> temp = new ArrayList<Float>();
	        for(int c=0; c < dim; c++)
	        	temp.add(0f);
	        matriz.add(temp);
	    }
		return matriz;
	}

	public ArrayList<ArrayList<Float>> matrizInversa(ArrayList<ArrayList<Float>> matriz) {
	    float det=1/determinante(matriz);
	    ArrayList<ArrayList<Float>> nmatriz=matrizAdjunta(matriz);
	    multiplicarMatriz(det,nmatriz);
	    return nmatriz;
	}
	 
	public void multiplicarMatriz(float n, ArrayList<ArrayList<Float>> matriz) {
	    for(int i=0;i<matriz.size();i++)
	        for(int j=0;j<matriz.size();j++)
	            matriz.get(i).set(j, matriz.get(i).get(j) * n);
	}
	 
	public ArrayList<ArrayList<Float>> matrizAdjunta(ArrayList<ArrayList<Float>> matriz){
	    return matrizTranspuesta(matrizCofactores(matriz));
	}
	 
	public ArrayList<ArrayList<Float>> matrizCofactores(ArrayList<ArrayList<Float>> matriz){
		ArrayList<ArrayList<Float>> nm = inicializarMatriz(matriz.size());
	    for(int i=0;i<matriz.size();i++) {
	        for(int j=0;j<matriz.size();j++) {
	        	ArrayList<ArrayList<Float>> det = inicializarMatriz(matriz.size()-1);
	            float detValor;
	            
	            for(int k = 0; k < matriz.size(); k++) {
	                if(k!=i) {
	                    for(int l=0;l<matriz.size();l++) {
	                        if(l!=j) {
		                        int indice1=k<i ? k : k-1 ;
		                        int indice2=l<j ? l : l-1 ;
		                        det.get(indice1).set(indice2, matriz.get(k).get(l));
		                     }
                    	}
	                }
	            }
	            detValor=determinante(det);
	            nm.get(i).set(j, (float) (detValor * (double)Math.pow(-1, i+j+2)));
	        }
	    }
	    return nm;
	}
	 
	public ArrayList<ArrayList<Float>> matrizTranspuesta(ArrayList<ArrayList<Float>> matriz){
		ArrayList<ArrayList<Float>>nuevam = inicializarMatriz(matriz.size());
		
	    for(int i=0; i<matriz.size(); i++) {
	        for(int j=0; j<matriz.size(); j++)
	            nuevam.get(i).set(j, matriz.get(j).get(i));
	    }
	    return nuevam;
	}
	
	public float determinante(ArrayList<ArrayList<Float>> matriz) {
		
		if(matriz.size() == 2)
	        return matriz.get(0).get(0)*matriz.get(1).get(1)-(matriz.get(1).get(0)*matriz.get(0).get(1));
	        
	    float suma=0;
	    
	    for(int i=0; i<matriz.size(); i++) {
	    	ArrayList<ArrayList<Float>> nm = new ArrayList<ArrayList<Float>>();
	        for(int j=0; j<matriz.size(); j++){
	            if(j!=i){
	            	ArrayList<Float> temp = new ArrayList<Float>();
	                for(int k=1; k<matriz.size(); k++)
	                	temp.add(matriz.get(j).get(k));
	                nm.add(temp);
	            }
	        }
	        if(i%2==0)
	        	suma+=matriz.get(i).get(0) * determinante(nm);
	        else
	        	suma-=matriz.get(i).get(0) * determinante(nm);
	    }
	    return suma;
	}
}
