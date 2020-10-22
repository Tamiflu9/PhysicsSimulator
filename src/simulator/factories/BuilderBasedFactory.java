package simulator.factories;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {

	protected List<Builder<T>> _builders; // lista de constructores
	protected List<JSONObject> _factoryElemnts; // lista de objetos JSON construidos por defecto
	
	public BuilderBasedFactory(List<Builder<T>> builders) {
		super();
		_builders = new ArrayList<Builder<T>>(builders);
		_factoryElemnts = new ArrayList<JSONObject>();
		for(int i = 0; i < builders.size(); i++) {
			JSONObject j = builders.get(i).getBuilderInfo();
			_factoryElemnts.add(builders.get(i).getBuilderInfo());
		}
	}
	
	/* recibe una structura JSON que describe el objeto a crear (ver la sintaxis más abajo), 
	 * y devuelve una instancia de la clase correspondiente–una instancia de un subtipo de 
	 * T.
	 * Encasodequeinfoseaincorrecto, entonces lanza una excepción del tipo IllegalArgumentException. */
	
	@Override
	public T createInstance(JSONObject info) throws IllegalArgumentException{
		T object;
		for(Builder<T> builder : _builders) {
			object = builder.createInstance(info);
			if(object != null) {
				return object;
			}
		}
		throw new IllegalArgumentException("no existe la ley, es null");
		/*
		for(int i = 0; i < _builders.size(); i++) {
			if(_builders.get(i).createInstance(info) != null ) {
				// crea el objeto a partir del constructor que es distinto de null
				return _builders.get(i).createInstance(info);
			}
			else {
				throw new IllegalArgumentException("no existe la ley");
			}
		}
		return null;*/
	}

	/* devuelve una lista de objetos JSON, que son “plantillas” para structuras JSON válidas.
	 * Los objetos de esta lista se pueden pasar como parámetro al método createInstance.
	 * Esto es muy útil para saber cuáles son los valores válidos para una factoría concreta, 
	 * sin saber mucho sobre la factoría en si misma.
	 * Por ejemplo, utilizaremos este método cuando mostremos al usuario los posibles valores 
	 * de las leyes de la gravedad. */
	//  devuelve en una lista las estructuras JSON devueltas por getBuilderInfo(). 
	
	@Override
	public List<JSONObject> getInfo() {
		List<JSONObject> lista = new ArrayList<JSONObject>();;
		for(int i = 0; i < _builders.size(); i++) {
			lista.add(_builders.get(i).getBuilderInfo());
		}
		return lista;
	}
}
