package simulator.control;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;

public class Controller {

	private PhysicsSimulator _sim;
	private Factory<Body> _bodiesFactory;
	private Factory<GravityLaws> _gravityLawsFactory; 
	
	public Controller(PhysicsSimulator _sim, Factory<Body> _bodiesFactory, Factory<GravityLaws> _gravityLawsFactory) {
		this._sim = _sim;
		this._bodiesFactory = _bodiesFactory;
		this._gravityLawsFactory = _gravityLawsFactory;
	}

	public void loadBodies(InputStream in) throws FileNotFoundException, JSONException {    
		JSONObject jsonInupt = new JSONObject(new JSONTokener(in));    
		JSONArray bodies = jsonInupt.getJSONArray("bodies");     
		for (int i = 0; i < bodies.length(); i++) {   
			_sim.addBody(_bodiesFactory.createInstance(bodies.getJSONObject(i)));
		}
	}
	
	public void run(Double n, OutputStream out) {
		PrintStream p = (out == null) ? null : new PrintStream(out);
		p.append("{").append(System.lineSeparator());
		p.append("\"states\": [").append(System.lineSeparator());
		p.print(_sim.toString()); p.append(",").append(System.lineSeparator());
		for(int i = 0; i < n; i++) {
			_sim.advance();
			if(i<n-1) {
				p.print(_sim.toString()); p.append(",").append(System.lineSeparator());
			}else {
				p.println(_sim.toString()); 
			}	
		}
		p.append("]").append(System.lineSeparator());
		p.append("}");
	}
	
	// invoca al método reset del simulador.
	public void reset() {
		this._sim.reset();
	}
	
	// invoca al método setDeltaTime del simulador.
	public void setDeltaTime(double dt) {
		_sim.setDeltaTime(dt);
	}
	
	// invoca al método addObserver del simulador.
	public void addObserver(SimulatorObserver o) {
		this._sim.addObserver(o);
	}
	
	// ejecuta n pasos del simulador sin escribir nada en consola.
	public void run(int n) {
		for(int i = 0; i < n; i++) {
			_sim.advance();
		}
	}
	
	// devuelve la factoría de leyes de gravedad.
	public Factory<GravityLaws> getGravityLawsFactory(){
		return this._gravityLawsFactory;
	}
	
	// usa la factoría de leyes de gravedad actual para crear un nuevo objeto de tipo 
	//  GravityLaws a partir de info y cambia las leyes de la gravedad del simulador por él.
	public void setGravityLaws(JSONObject info) {
		GravityLaws gl = this._gravityLawsFactory.createInstance(info);
		_sim.setGravityLaws(gl);
	}

	public List<Body> getBodies() {
		return _sim.getBodies();
	}
	
}
