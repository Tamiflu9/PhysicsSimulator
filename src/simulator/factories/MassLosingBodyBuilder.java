package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.MassLossingBody;

public class MassLosingBodyBuilder extends Builder<Body>{

	// aceleracion (0,..,0)
	protected Vector vel; // vector velocidad -- ListObjSON 
	protected Vector a; // vector aceleracion -- ListObjSON 
	protected Vector pos; // vector posicion
	public MassLosingBodyBuilder() {
		super("mlb", "Mass losing body");
	}

	@Override
	protected Body createTheInstance(JSONObject jsonObject) {
		String id = jsonObject.getString("id");
		double[] p = jsonArrayTodoubleArray(jsonObject.getJSONArray("pos")); 
		double[] v = jsonArrayTodoubleArray(jsonObject.getJSONArray("vel"));
		double m = jsonObject.getDouble("mass"); 
		//double[] a = jsonArrayTodoubleArray(jsonObject.getJSONArray("acc"));
		double fr = jsonObject.getDouble("freq");
		double fac = jsonObject.getDouble("factor");
		this.pos = transformar(p);
		this.vel = transformar(v);		
		return new MassLossingBody(id,m,vel,pos,fr,fac);
	}
	
	@Override
	protected JSONObject createData() {
		JSONObject data = new JSONObject();
		data.put("id", "the identifier");
		data.put("pos", "the position");
		data.put("vel", "the velocity");
		data.put("mass", "the mass");
		data.put("freq", "the frequency");
		data.put("factor", "the factor");
		return data;
	}
}
