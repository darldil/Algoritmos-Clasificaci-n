package p3;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.Controlador;
import ui.WindowController;

public class Main {

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Controlador controlador = new Controlador();
		WindowController ui = new WindowController(controlador);
		
		ui.run();

	}

}
