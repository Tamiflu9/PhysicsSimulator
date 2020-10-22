package simulator.model;

import simulator.misc.Vector;

public class Body {
	
	protected String id;
	protected Double m; //masa
	protected Vector v; // vector velocidad -- ListObjSON 
	protected Vector a; // vector aceleracion -- ListObjSON 
	protected Vector p; // vector posicion
	protected double[] d = {0,0};
	
	//DecimalFormat df = new DecimalFormat("#.0000000000000000");
	 //System.out.println(df.format(number));
	
	public Body(String id, Vector r, Vector v, double mass) {
		this.id = id;
		this.m = mass;
		this.v = v;
		this.a = new Vector(d);
		this.p = r;
	}

	/* mueve el cuerpo durante t segundos utilizando los atributos del mismo. 
	 * Concretamente cambia la posición FORMULA EN EL GUION 
	 *   plus +, minus -, scale *
	 * posicion = p + v*t + 1/2*a*t*t
	 * velocidad = v + a*t
	 */
	void move(double t) {
		Vector vt = v.scale(t);
		Vector at = a.scale(t);
		Vector att2 = a.scale(t*t).scale(0.5);
		this.p = p.plus(vt).plus(att2);
		this.v = v.plus(at);
	}
	
	// devuelve el identificador del cuerpo.
	public String getId(){
		return id;
	}
	
	//devuelve una copia del vector de velocidad.
	public Vector getVelocity() { 
		return new Vector(v);
	}
	
	// devuelve una copia del vector de aceleración.
	public Vector getAcceleration() { 
		return new Vector(a);
	}
	
	// devuelve una copia del vector de posición.
	public Vector getPosition() { 
		return new Vector(p); // devuelve copia 
	}
	
	// devuelve la masa del cuerpo.
	public double getMass() { 
		return m;
	}
	
	//hace una copia de v y se la asigna al vector de velocidad.
	void setVelocity(Vector _v) { 
		 v = new Vector(_v); 
	}
	
	//hace una copia de a y se la asigna al vector de aceleración.
	void setAcceleration(Vector _a) { 
		 a = new Vector(_a);
	}
	
	//hace una copia de p y se la asigna al vector de posición.
	public void setPosition(Vector _p) {
		 p = new Vector(_p);  
	}
	
	public void setM(Double m) {
		this.m = m;
	}

	
	// devuelve un string con la información del cuerpo en formato JSON;
	// { ”id”: id, ”mass”: m, ”pos”: p, ”vel”: v, ”acc”: a }
	
	public String toString() { 
		String s = "{  \"id\": " + "\""+this.id +"\""+ ", \"mass\": "+this.m+", \"pos\": "+this.p+", \"vel\": "+this.v+", \"acc\": "+this.a+" }";
		return s;
	}
	
}
