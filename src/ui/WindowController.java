package ui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import logica.KMedias;
import logica.Lloyd;
import logica.TransferArchivos;
import logica.bayes.Bayes;

public class WindowController {
	
	private Controlador contr;
	private Estados state;
	
	public WindowController(Controlador c) {
		contr = c;
		state = Estados.LEER_CASOS;
		contr.setState(state);
		String seleccion = pedirAlgoritmo();
		if (seleccion.equals("KMedias"))
			contr.setAlgoritmo(new KMedias());
		else if (seleccion.equals("Bayes"))
			contr.setAlgoritmo(new Bayes());
		else
			contr.setAlgoritmo(new Lloyd());
		contr.setUi(this);
	}
	
	private String pedirAlgoritmo() {
		Object[] options1 = { "KMedias", "Bayes", "Lloyd",
        "Cerrar" };

		JPanel panel = new JPanel();
		panel.add(new JLabel("Seleccione qué algoritmo desea utilizar"));
		
		int result = JOptionPane.showOptionDialog(null, panel, "Practica 3 - Mauricio Abbati Loureiro",
		        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, options1, null);
		if (result == JOptionPane.YES_OPTION){
		    return "KMedias";
		} else if (result == JOptionPane.NO_OPTION)
			return "Bayes";
		else if(result == 2) {
			return "Lloyd";
		}
		else
			System.exit(0);
		return null;
	}
	
	public void run() {
		switch (state) {
			case LEER_CASOS: 
				JOptionPane.showMessageDialog(null, "En primer lugar, escoja el archivo de casos");
				leerArchivos(); 
				break;
			case PROCESAR:  try {
				contr.accion(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			case LEER_EJEMPLO: 
				JOptionPane.showMessageDialog(null, "Ahora, proporcione una muestra");
				leerArchivos(); 
				break;
			case FIN: 
				JOptionPane.showMessageDialog(null, "Resultado: " + contr.getResultado());
				System.exit(0);
				break;
			default: break;
		}
	}
	
	public void update() {
		state = contr.getState();
		run();
	}
	
	private void leerArchivos() {
		try {
			EventQueue.invokeLater(new Runnable() {
				@Override
			    public void run() {
					JFileChooser c = new JFileChooser();
				      // Demonstrate "Open" dialog:
				      int rVal = c.showOpenDialog(null);
				      if (rVal == JFileChooser.APPROVE_OPTION) {
							TransferArchivos transfer = new TransferArchivos();
							if (state.equals(Estados.LEER_CASOS))
								transfer.setTipo("Casos");
							else
								transfer.setTipo("Muestra");
							transfer.setNombre(c.getSelectedFile().getName());
							transfer.setRuta(c.getCurrentDirectory().toString() + '/');
							try {
								c.removeAll();
								contr.accion(transfer);
							} catch (Exception e) {
								e.printStackTrace(); 
								JOptionPane.showMessageDialog(null, "Me has dado un archivo cascado. Abreme de nuevo"
										+ "con el archivo correcto.");
								System.exit(0);
							}
				      }
				      else if (rVal == JFileChooser.CANCEL_OPTION) {
				    	  JOptionPane.showMessageDialog(null, "Debe seleccionar los archivos. Ejecute de nuevo la aplicación");
				    	  System.exit(0);
				      }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error catastrofico. Ejecute de nuevo la aplicación");
	    	  System.exit(0);
		}
	}

}
