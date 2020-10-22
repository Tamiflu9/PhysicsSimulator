package simulator.model;

import java.util.List;
import simulator.misc.Vector;

public class NewtonUniversalGravitation implements GravityLaws{

	static private final double G = 6.67E-11; 
	protected double f;
	protected Vector _F;
	protected double[] d = {0,0};
	protected Vector suma;
	protected Vector cero = new Vector(d);
	
	public NewtonUniversalGravitation() {
		super();
		suma = new Vector(this.d);
	}

	/*		   	  mi ∗mj 
	 * fi,j = G ---------- 		
	 * 		  	|p~j −p~i|2 
	 */
	
	@Override
	public void apply(List<Body> bodies) {
	Vector fuerza = new Vector(d);
		if(!bodies.isEmpty()){
			for(int i = 0; i < bodies.size(); i++) {
				if(bodies.get(i).getMass() == 0.0) {
					bodies.get(i).setAcceleration(new Vector(d));
					bodies.get(i).setVelocity(new Vector(d));
				}else {
					Vector acu = new Vector(d);
					for(int j = 0; j < bodies.size(); j++) {
						if(!bodies.get(i).equals(bodies.get(j))) {
							fuerza = calcularFa_b(bodies.get(i), bodies.get(j));
							acu = acu.plus(fuerza);
							bodies.get(i).setAcceleration(acu.scale(1/bodies.get(i).getMass()));
						}
					}
				}
			}
		}
	}
	
	private Vector calcularFa_b(Body a, Body b) {
		Vector dis = b.getPosition().minus(a.getPosition());
		Double dis_mag = dis.magnitude();
		double fab = (G*(a.getMass()*b.getMass())/(dis_mag* dis_mag));		
		Vector dab = dis.direction().scale(fab);
		setf(fab);
		set_F(dab);
		return dab;
	}
	
	public Vector getSuma() {
		return this.suma;
	}
	
	public void setSuma(Vector v) {
		this.suma = new Vector(v);
	}
	
	public double getf() {
		return f;
	}

	public void setf(double f) {
		this.f = f;
	}

	public Vector get_F() {
		return new Vector(_F); 
	}

	public void set_F(Vector f) {
		this._F = new Vector(f);
	}

	public String toString(){
		return "Newton's Universal Gravitation";
		
	}
	
}
