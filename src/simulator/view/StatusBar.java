package simulator.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver {
	// ... 
	private JLabel _currTime; // for current time 
	private JLabel _currLaws; // for gravity laws 
	private JLabel _numOfBodies; // for number of bodies
	private List<SimulatorObserver> observadores;

	StatusBar(Controller ctrl) { 
		initGUI(); 
		ctrl.addObserver(this); 
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		this._currTime = new JLabel("Time: " );
		this._numOfBodies = new JLabel("Bodies: ");
		this._currLaws = new JLabel("Laws: ");
		this.setLayout(new GridLayout(1,3));
		this.add(_currTime);
		this.add(_numOfBodies);
	 	this.add(_currLaws);
		this.setBorder(BorderFactory.createBevelBorder(1));	
		
	}
	// other private/protected methods 
	
	 public void setMensajeTiempo(String mensaje) {
		this._currTime.setText(mensaje);
	 } 
	 
	 public void setMensajeBodies(String mensaje) {
			this._numOfBodies.setText(mensaje);
	 }
	
	 public void setMensajeLaws(String mensaje) {
		 this._currLaws.setText(mensaje);
	 }
	 
	// SimulatorObserver methods // ...


	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
//		this._currTime.setText("Time: " + time);
//		this._numOfBodies.setText("Bodies: " + bodies.size());
//		this._currLaws.setText("Laws: " + gLawsDesc);
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
				_currLaws.setText("Laws: " + gLawsDesc);
			} });
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
//		this._currTime.setText("Time: " + time);
//		this._numOfBodies.setText("Bodies: " + bodies.size());
//		this._currLaws.setText("Laws: " + gLawsDesc);
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
				_currLaws.setText("Laws: " + gLawsDesc);
			} });
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {		
//		this._numOfBodies.setText("Bodies: " + bodies.size());
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				_numOfBodies.setText("Bodies: " + bodies.size());
			} });
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
//		this._currTime.setText("Time: " + time);
//		this._numOfBodies.setText("Bodies: " + bodies.size());
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
			} });
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
//		this._currLaws.setText("Laws: " + gLawsDesc);
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				_currLaws.setText("Laws: " + gLawsDesc);
			} });
	}
}

