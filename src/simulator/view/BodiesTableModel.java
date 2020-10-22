package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Body> _bodies;
	private List<SimulatorObserver> observadores;
	private String[] _columnIds;
	private Double _time;
	private String _law;
	
	
	public BodiesTableModel(Controller ctrl) { 
		//_bodies; 
		this._columnIds = new String[] {"Id", "Mass", "Position","Velocity", "acceleration"};
		ctrl.addObserver(this); 
		this.observadores = new ArrayList<>();
		
	}

	@Override 
	public int getRowCount() {
		return this._bodies.size();
	}

	@Override 
	public int getColumnCount() {
	 	return this._columnIds.length;
	}

	@Override 
	public String getColumnName(int column) {
		return this._columnIds[column]; 
	}
	
	public void initializeTable() {
		for(int i = 0; i < this._bodies.size(); i++) {
			this.setValueAt(this._bodies.get(i).getId(), i, 0);
			this.setValueAt(this._bodies.get(i).getMass(), i, 1);
			this.setValueAt(this._bodies.get(i).getPosition(), i, 2);
			this.setValueAt(this._bodies.get(i).getVelocity(), i, 3);
			this.setValueAt(this._bodies.get(i).getAcceleration(), i, 4);
		}
	}

	@Override 
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object s = null;
		switch(columnIndex) {
			case 0: 
				s = this._bodies.get(rowIndex).getId();
				break;
			case 1: 
				s = this._bodies.get(rowIndex).getMass();
				break;
			case 2: 
				s = this._bodies.get(rowIndex).getPosition();
				break;
			case 3: 
				s = this._bodies.get(rowIndex).getVelocity();
				break;
			case 4: 
				s = this._bodies.get(rowIndex).getAcceleration();
				break;
			default: 
				assert (false);
				break;
		}
		return s;

	}
	// SimulatorObserver methods // ...	

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		this._bodies = bodies;
		this._time = time;
		this._law = gLawsDesc;
		
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				fireTableStructureChanged();
			} });
		
		
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		this._bodies = bodies;
		this._time = time;
		this._law = gLawsDesc;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				fireTableStructureChanged();
			} });
		
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		this._bodies = bodies;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				fireTableStructureChanged();
			} });
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		this._bodies = bodies;
		this._time = time;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				fireTableStructureChanged();
			} });
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		this._law = gLawsDesc;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				fireTableStructureChanged();
			} });
	}

}
