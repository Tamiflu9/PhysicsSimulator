package simulator.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONException;

import simulator.control.Controller;
import simulator.factories.FallingToCenterGravityBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoGravityBuilder;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {

	private Controller _ctrl;
	//	private boolean _stopped;
	private JSpinner steps;
	
	private JSpinner delay;
	volatile Thread _thread;

	private JTextField time;
	private File ficheroActual;
	private JFileChooser fc;
	private JButton botonCargar = new JButton();
	private JButton physics = new JButton();
	private JButton botonSalir = new JButton();
	private JButton play = new JButton();
	private JButton stop = new JButton();
	
	// Añade atributos para JToolBar, botones, etc.
	public ControlPanel(Controller ctrl) {
		super();
		_ctrl = ctrl;
		_ctrl.addObserver(this);
		initGUI(ctrl);
	}

	private void initGUI(Controller controlador) {
		JToolBar toolbar = new JToolBar();
		//toolbar.set
		//	this.setLocation(null);
		this.add(toolbar);
		// Construye la tool bar con todos sus botones, etc.
		//Barra de Herramientas: 
		
		//open
		botonCargar.setToolTipText("Carga un fichero de eventos");
		botonCargar.setActionCommand("cargar");
		botonCargar.setIcon(new ImageIcon("resources/icons/open.png"));
		botonCargar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc = new JFileChooser();
				fc.setMultiSelectionEnabled(true);

				if(e.getActionCommand().equals("cargar")){
					int ret = fc.showOpenDialog(botonCargar);
					if(ret == JFileChooser.APPROVE_OPTION) {
						ficheroActual = fc.getSelectedFile();
						controlador.reset();
						FileInputStream in;
						try {
							in = new FileInputStream(ficheroActual);
							controlador.loadBodies(in);
						} catch (FileNotFoundException e1) {
							JOptionPane.showMessageDialog(botonCargar, "No se ha encontrado el archivo", "Error", JOptionPane.ERROR_MESSAGE);
						} catch(JSONException e2) {
							JOptionPane.showMessageDialog(botonCargar, "formato de archivo no valido", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}else {
						JOptionPane.showMessageDialog(botonCargar, "Se ha pulsado cancelar o ha ocurrido un error.");

					}
				}	
			}
		});

		//physics 
		physics.setToolTipText("Cambia la ley de gravedad");
		physics.setIcon(new ImageIcon("resources/icons/physics.png"));

		physics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				cambiarLeyGravedad();
			}
		});

		this.delay = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		this.delay.setToolTipText("delay");
		this.delay.setMaximumSize(new Dimension(70, 70));
		this.delay.setMinimumSize(new Dimension(70, 70));
		this.delay.setValue(1);

		//Barra de Herramientas: Spinner
		this.steps = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));
		this.steps.setToolTipText("pasos a ejecutar: 1-1000");
		this.steps.setMaximumSize(new Dimension(70, 70));
		this.steps.setMinimumSize(new Dimension(70, 70));
		this.steps.setValue(1000);


		//Barra de herramientas: Tiempo
		this.time = new JTextField("2500", 5);
		this.time.setToolTipText("Tiempo actual");
		this.time.setMaximumSize(new Dimension(70, 70));
		this.time.setMinimumSize(new Dimension(70, 70));
		this.time.setEditable(true);

		//exit
		botonSalir.setToolTipText("Salir");
		botonSalir.setIcon(new ImageIcon ("resources/icons/exit.png"));
		botonSalir.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				salir();
			}
		});
		
	class Hebra1 extends Thread{
			private String id;
			
			public Hebra1(String id){
				this.id = id;
			}
			public Hebra1() {}

			public void run() {
				while(_thread != null) {
					run_sim(getSteps(), getDelay());
					physics.setEnabled(true);
					play.setEnabled(true);
					botonSalir.setEnabled(true);
					botonCargar.setEnabled(true);
					_thread = null;
				}	
			};
		}

		//play
		play.setToolTipText("Ejecutar el simulador");
		play.setIcon(new ImageIcon("resources/icons/run.png"));
		play.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				physics.setEnabled(false);
				play.setEnabled(false);
				botonSalir.setEnabled(false);
				botonCargar.setEnabled(false);
				controlador.setDeltaTime(getTime());
				_thread = new Hebra1("Hebra 1");
				_thread.start();
			}
		});

		//stop
		stop.setToolTipText("Para la ejecucion del simulador");
		stop.setIcon(new ImageIcon("resources/icons/stop.png"));
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_thread != null) {
					_thread.interrupt();
				}
				physics.setEnabled(true);
				play.setEnabled(true);
				botonSalir.setEnabled(true);
				botonCargar.setEnabled(true);
			}	
		});
		
		toolbar.setLayout(new GridLayout(1,1));
		toolbar.add(botonCargar);
		toolbar.add(physics);
		toolbar.add(play);
		toolbar.add(stop);
		toolbar.addSeparator();
		toolbar.add(new JLabel("Delay: "));
		toolbar.add(delay);
		toolbar.addSeparator();
		toolbar.add(new JLabel(" Steps: "));
		toolbar.add(steps);
		toolbar.addSeparator();
		toolbar.add(new JLabel(" Delta-Time: "));
		toolbar.add(this.time);
		toolbar.addSeparator();
		toolbar.add(botonSalir);
	}

	// Añade métodos privados/protegidos
	/*while ( n>0 && (the current thread has not been intereptred) ) {
	  1. execute the simulator one step, i.e., call method _ctrl.run(1) and handle exceptions if any
      2. sleep the current thread for �delay� milliseconds n--;
	}
	 */
	private void run_sim(int n, long delay) {
		while(n>0 && !_thread.isInterrupted()) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Ha ocurrido un error (run)", "Error", JOptionPane.ERROR_MESSAGE);
			}
			try {
				_thread.sleep(delay);
			}catch(Exception e) {
				_thread.interrupt();
			}

			n--;			
		}
		return;
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		this.steps.setValue(1000);
		this.time.setText("1000");
		this.delay.setValue(1);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {

	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		try {
			this.time.setText("" + time);
		}catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "formato de tiempo no valido", "Error", JOptionPane.ERROR_MESSAGE);
			this.steps.setValue(1000);
			this.time.setText("1000");
			this.delay.setValue(1);
		}		
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		this.time.setText("" + dt);
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {

	}

	public double getTime() {
		try {
			Double t = Double.parseDouble(time.getText());
			return t;
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "formato de tiempo no valido", "Error", JOptionPane.ERROR_MESSAGE);
			this.steps.setValue(1000);
			this.time.setText("1000");
			this.delay.setValue(1);	
		}
		return 0;
	}

	public int getSteps () {
		try {
			int t = Integer.parseInt(steps.getValue().toString());
			return t;
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "formato de steps no valido", "Error", JOptionPane.ERROR_MESSAGE);
			this.steps.setValue(1000);
			this.time.setText("1000");
			this.delay.setValue(1);
		}
		return 0;		
	}

	public long getDelay() {
		try {
			long t = Long.parseLong(delay.getValue().toString());
			return t;
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "formato de tiempo no valido", "Error", JOptionPane.ERROR_MESSAGE);
			this.steps.setValue(1000);
			this.time.setText("1000");
			this.delay.setValue(1);
		}
		return 0;
	}

	public void cambiarLeyGravedad() {
		String leyes [] = {"Newton's law of universal gravitation (nlug)","Falling to center gravity (ftcg)","No gravity (ng)"};
		String n = (String) JOptionPane.showInputDialog(this, "Select gravity laws to be used", "Gravity laws selector", JOptionPane.INFORMATION_MESSAGE, null, leyes, "");

		if(n != null && n.equals("Newton's law of universal gravitation (nlug)")) {
			NewtonUniversalGravitationBuilder nlug = new NewtonUniversalGravitationBuilder();
			_ctrl.setGravityLaws(nlug.getBuilderInfo());
		}else if(n != null && n.equals("Falling to center gravity (ftcg)")) {
			FallingToCenterGravityBuilder ftcg = new FallingToCenterGravityBuilder();
			_ctrl.setGravityLaws(ftcg.getBuilderInfo());
		}else if(n != null && n.equals("No gravity (ng)")) {
			NoGravityBuilder ng = new NoGravityBuilder();
			_ctrl.setGravityLaws(ng.getBuilderInfo());
		}
	}

	public void salir() { 
		int n = JOptionPane.showOptionDialog(this, "Estas seguro de que quieres salir?", "Salir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null); 
		if (n == 0) System.exit(0); 
	}

}
