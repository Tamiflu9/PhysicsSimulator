package simulator.factories;

import simulator.misc.Vector;

import org.json.JSONObject;

import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body>{

	// aceleracion (0,..,0)
	// _typeTag será “basic” y _desc será “Default Body”.
	protected Vector vel; // vector velocidad -- ListObjSON 
	protected Vector a; // vector aceleracion -- ListObjSON 
	protected Vector pos; // vector posicion

	public BasicBodyBuilder() {
		super("basic", "Default body");
	}

	@Override
	protected Body createTheInstance(JSONObject jsonObject) {
		String id = jsonObject.getString("id");
		double[] p = jsonArrayTodoubleArray(jsonObject.getJSONArray("pos")); 
		double[] v = jsonArrayTodoubleArray(jsonObject.getJSONArray("vel"));
		
		//double[] a = jsonArrayTodoubleArray(jsonObject.getJSONArray("acc"));
		double m = jsonObject.getDouble("mass"); 
		this.pos = transformar(p);
		this.vel = transformar(v);
		return new Body(id,pos,vel,m);
	}

	@Override
	protected JSONObject createData() {
		JSONObject data = new JSONObject();
		data.put("id", "the identifier");
		data.put("pos", "the position");
		data.put("vel", "the velocity");
		data.put("mass", "the mass");
		return data;	
	}

}
