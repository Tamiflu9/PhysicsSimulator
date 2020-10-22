package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	
	private Controller _ctrl;
	public static Border bordePorDefecto = BorderFactory.createLineBorder(Color.black, 2);
	private ControlPanel panelControl;
	private StatusBar panelBarraEstado;

	public MainWindow(Controller ctrl) { 
		super("Physics Simulator"); 
		this._ctrl = ctrl; 
		this.initGUI(); 
	}
	
	private void initGUI() { 
		JPanel mainPanel = this.creaPanelPrincipal(); 
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {}
			
			@Override
			public void windowIconified(WindowEvent arg0) {}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
//				salir();				
			}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		
		this.setContentPane(mainPanel);
		
		// Control panel con botones de arriba
		this.addControlPanel(mainPanel);
		
		// Panel central 
		JPanel panelCentral = this.creaPanelCentral();
		mainPanel.add(panelCentral, BorderLayout.CENTER);
		
		// Barra de estado de abajo
		this.addBarraEstado(mainPanel);	
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setLocation(300, 27);
		this.setSize(1000,700);
		this.setVisible(true);
	}
	
	private JPanel creaPanelPrincipal() {
		 JPanel mainPanel = new JPanel();
	     mainPanel.setLayout(new BorderLayout());
	     return mainPanel;
	}
	
	private void addBarraEstado(JPanel panelPrincipal) {
		this.panelBarraEstado = new StatusBar(this._ctrl); 
		panelPrincipal.add(this.panelBarraEstado, BorderLayout.PAGE_END);
	}
	
	private JPanel creaPanelCentral() {
		JPanel panelCentro = new JPanel();
		panelCentro.setLayout(new GridLayout(2,1));
		
		addPanelSupCentral(panelCentro);
		addPanelInfCentral(panelCentro);
		
		return panelCentro;
	}
	
	private void addPanelSupCentral(JPanel panelCentral) {
		JPanel pSuperior = new JPanel();
		pSuperior.setLayout(new GridLayout(1,1));
	
		BodiesTable tlb = new BodiesTable(_ctrl);
		pSuperior.add(new JScrollPane(tlb,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ));
		
		panelCentral.add(pSuperior,BorderLayout.NORTH);
	}
	private void addPanelInfCentral(JPanel panelCentral) {
		JPanel pInferior = new JPanel();
		pInferior.setLayout(new GridLayout(1,2));
		
		Viewer comp = new Viewer(_ctrl);
		pInferior.add(new JScrollPane(comp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ));

// tabla a la derecha de la componente grafica		
//		BodiesTable t = new BodiesTable(_ctrl);
//		pInferior.add(new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		pInferior.setVisible(true);
		panelCentral.add(pInferior,BorderLayout.SOUTH);
	}
	
	private void addControlPanel(JPanel panelPrincipal) {
		this.panelControl = new ControlPanel(this._ctrl);
		this.panelControl.setLayout(new GridLayout(1,1));
		panelPrincipal.add(this.panelControl, BorderLayout.PAGE_START);
	}
	
	public void salir() { 
		int n = JOptionPane.showOptionDialog(this, "Estas seguro de que quieres salir?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); 
		if (n == 0) System.exit(0); 
	}
	
}
