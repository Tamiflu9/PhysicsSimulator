package simulator.model;

import simulator.misc.Vector;

public class MassLossingBody extends Body{
	
	protected double lossFactor; //entre 0 y 1
	protected double lossFrecuency;  // un número positivo (double) que indica el intervalo de tiempo (ensegundos) después del cual el objeto pierde masa.
	protected double accumulatedTime;
	protected String id;
	protected Double m; //masa
	protected Vector v; // vector velocidad
	protected Vector a; // vector aceleracion
	protected Vector p; // vector posicion
	protected double[] d = {0,0};
	
	public MassLossingBody(String id, Double m, Vector v, Vector p, double lossFactor, double lossFrecuency) {
		super(id, p, v, m);
		this.lossFactor = lossFactor;
		this.lossFrecuency = lossFrecuency;
		this.accumulatedTime = 0.0;
		this.id = id;
		this.m = m;
		this.v = v;
		this.a = new Vector(d);
		this.p = p;
	}

	void move(double t) {
		accumulatedTime = 0.0;
		Double newM;
		super.move(t);
		if(accumulatedTime >= lossFrecuency) {
			newM = m*(1-lossFactor);
			super.setM(newM);
			accumulatedTime = 0.0;
		}
		accumulatedTime++;
	}
	
	public double getLossFactor() {
		return lossFactor;
	}

	public void setLossFactor(double lossFactor) {
		this.lossFactor = lossFactor;
	}

	public double getLossFrecuency() {
		return lossFrecuency;
	}

	public void setLossFrecuency(double lossFrecuency) {
		this.lossFrecuency = lossFrecuency;
	}

	
}
