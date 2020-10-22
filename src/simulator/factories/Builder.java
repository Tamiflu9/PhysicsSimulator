package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector;

public abstract class Builder<T> {
	
	private String _tipoObj; 
	private String _descripcion; 
	
	public Builder(String t, String d){
		this._tipoObj = t;
		this._descripcion = d;
	}
	
	/* si la información suministrada por info es correcta,entonces crea un objeto de tipo T
	 * (i.e.,una instancia de una subclase de T). 
	 * En otro caso devuelve null para indicar que este constructor es incapaz de 
	 * reconocer ese formato.
	 * En caso de que reconozca el campo type pero haya un error en alguno de los valores 
	 * suministrados por la sección data, el método lanza una excepcion IllegalArgumentException*/

	public T createInstance(JSONObject info) {   
		T b = null;   
		if (_tipoObj != null && _tipoObj.equals(info.getString("type"))) {       
			b = createTheInstance(info.getJSONObject("data"));   
		}
		return b; 
	} 
	
	protected abstract T createTheInstance(JSONObject jsonObject);


	/* devuelve un objeto JSON que sirve de plantilla para el correspondiente constructor, i.e.,
	 * un valor válido para el parámetro de createInstance (ver getInfo() de Factory<T>). */
	public JSONObject getBuilderInfo() {   
		JSONObject info = new JSONObject(); 
		info.put("type", _tipoObj);   
		info.put("data", createData()); 
		info.put("desc", _descripcion);  
		return info; 
	} 
	
	protected JSONObject createData() {  
		return new JSONObject(); 
	}		

	protected double[] jsonArrayTodoubleArray(JSONArray ja) {  
		double[] da = { ja.getDouble(0), ja.getDouble(1)};
		/*new double[ja.length()];  
		for (int i = 0; i < da.length; i++) {     
			da[i] = ja.getDouble(i);  
		}*/
		return da; 
	} 
	
	protected Vector transformar(double[] d) {
		Vector v = new Vector(d);
		return v;
		
	}
}
