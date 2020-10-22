package simulator.model;

import java.util.List;
import simulator.misc.Vector;

public class FallingToCenterGravity implements GravityLaws{

	static private final double g = 9.81; 
	protected double f;
	protected Vector _F;
	protected double[] d = {0,0};
	 
	public FallingToCenterGravity() {
		super();
	}

	// a = -g*d
	@Override 
	public void apply(List<Body> bodies) {
		if(!bodies.isEmpty()) {
			for(int i = 0; i < bodies.size(); i++) {
			Vector x = (bodies.get(i).getPosition().direction()).scale(-g);
				bodies.get(i).setAcceleration(x);
			}
		}		
	}
	
	public String toString(){
		return "Falling To Center";
		
	}
}
