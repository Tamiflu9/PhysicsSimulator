package simulator.model;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSimulator {

	private GravityLaws _gravityLaws; //leyes de la gravedad a aplicar. 
	private List<Body> _bodies; //cuerpos de la simulación; 
	private double _dt; //incremento del tiempo. 
	private double _time; //número de pasos que se ejecuta la simulación. 
	private List<SimulatorObserver> observadores;

	public PhysicsSimulator(GravityLaws _gravityLaws, double _dtime) {
		this._gravityLaws = _gravityLaws;
		_bodies = new ArrayList<Body>();
		this._dt = _dtime;
		this._time = 0.0;
		this.observadores = new ArrayList<>();
	}

	/*aplica un paso de simulación, i.e., primero llama al método
	  apply de las leyes de la gravedad, después llama a move(dt) para cada cuerpo, donde
	  dt es el tiempo real por paso, y finalmente incrementa el tiempo actual en dt segundos.
	  4
	 *un número de tipo double que representa el tiempo (en segundos) que corresponde 
	 *a un paso de simulación — se pasará al método move de los cuerpos. 
	 *Debe lanzar IllegalArgumentException en caso de que el valor no sea válido.*/
	
	public void advance() throws IllegalArgumentException{ 
		if(this._gravityLaws != null) {
			this._gravityLaws.apply(this._bodies);
			for(int i = 0; i < this._bodies.size(); i++) {
				this._bodies.get(i).move(this._dt);
				
			}
			this._time = this._time + this._dt;
		}else {
			throw new IllegalArgumentException("la ley de gravedad el null");
		}
		for(int j = 0; j < observadores.size(); j++) {
			observadores.get(j).onAdvance(_bodies, _time);
		}
	}

	/* añade el cuerpo b al simulador. El método debe comprobar que no existe ningún 
	 * otro cuerpo en el simulador con el mismo identificador.
	 Si existiera, el método debe lanzar una excepción del tipo IllegalArgumentException. */
	public void addBody(Body b) { 
		int size = _bodies.size();
		if(_bodies.isEmpty()) {
			_bodies.add(b);
		}
		else {
			boolean esta = false;
			int i = 0;
			while(i < size) {
				if(!_bodies.get(i).getId().equals(b.getId()) && !esta) {
					_bodies.add(b);
					esta = true;
					for(int j = 0; j < observadores.size(); j++) {
						observadores.get(j).onBodyAdded(_bodies, b);
					}
				}
				i++;
			}
		}
	}

	/* devuelve un string que representa un estado del simulador,
	utilizando el siguiente formato JSON: */
	//{ "time": T, "bodies": [json1, json2, ...] } donde T es el tiempo actual 
	//jsoni es el string devuelto por el método toString 
	public String toString() {
		String s = "{ \"time\": " + this._time + ", \"bodies\": " + "[ ";
		if(this._bodies.size() == 1) {
			s += this._bodies.get(0).toString();
		}else {
			for(int i = 0; i < this._bodies.size()-1; i++) {
				s += this._bodies.get(i).toString() + ", ";
			}
			s += this._bodies.get(this._bodies.size()-1).toString();
		}
		
		s +=" ] }";
		return s ; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_bodies == null) ? 0 : _bodies.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhysicsSimulator other = (PhysicsSimulator) obj;
		if (_bodies == null) {
			if (other._bodies != null)
				return false;
		} else if (!_bodies.equals(other._bodies))
			return false;
		return true;
	}

	// vacía la lista de cuerpos y pone el tiempo a 0,0. 
	public void reset() {
		this._bodies.clear();
		this._time = 0.0;
		for(int j = 0; j < observadores.size(); j++) {
			observadores.get(j).onReset(_bodies, _time, _dt, _gravityLaws.toString());
		}
	}

	// cambia el tiempo real por paso (delta-time de aquí en adelante) a dt. 
	// Si dt tiene un valor no válido lanza una excepción de tipo IllegalArgumentException.
	public void setDeltaTime(double dt) throws IllegalArgumentException{
		this._time = dt;
		for(int j = 0; j < observadores.size(); j++) {
			observadores.get(j).onDeltaTimeChanged(dt);
		}
	}
	
	// cambia las leyes de gravedad del simulador a gravityLaws. 
	// Lanza una IllegalArgumentException si el valor no es válido, es decir, si es null. 
	public void setGravityLaws(GravityLaws gravityLaws) {
		if(gravityLaws != null) {
			this._gravityLaws = gravityLaws;
			for(int j = 0; j < observadores.size(); j++) {
				observadores.get(j).onGravityLawChanged(gravityLaws.toString());
			}
		}else {
			throw new IllegalArgumentException("gravity law is null"); 
		}
	}
	
	public void addObserver(SimulatorObserver o) {
		if (o != null && !this.observadores.contains(o)) { 
			this.observadores.add(o); 
			o.onRegister(_bodies, _time, _dt, _gravityLaws.toString());
		}
	}
	// envio de notificaciones
	/* --Al final del método addObserver envía una notificación onRegister 
	 * 	solo al observador que se acaba de registrar para pasarle el estado actual del simulador.
	 * --Al final del método reset envía una notificación onReset a todos los observadores.
	 * Al final del método addBody envía una notificación onBodyAdded a todos los observadores.
	 * --Al final del método advance envía una notificación onAdvance a todos los observadores.
	 * --Al final del método setDeltaTime envía una notificación onDeltaTimeChanged a todos
		los observadores.
	 *-- Al final del método setGravityLaws envía una notificación onGravityLawsChanged a
		todos los observadores.*/

	public List<Body> getBodies() {
		return _bodies;
	}

}
