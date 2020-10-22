package simulator.factories;

import java.util.List;
import org.json.JSONObject;

public interface Factory<T> {
	
	/* recibe una structura JSON que describe el objeto a crear (ver la sintaxis más abajo), 
	 * y devuelve una instancia de la clase correspondiente–una instancia de un subtipo de T.
	 * Encasodequeinfoseaincorrecto, entonces lanza una excepción del tipo IllegalArgumentException. */
	public T createInstance(JSONObject info);
	
	/* devuelve una lista de objetos JSON, que son “plantillas” para structuras JSON válidas.
	 * Los objetos de esta lista se pueden pasarcomoparámetroalmétodocreateInstance.
	 * Estoesmuyútilparasabercuálesson los valores válidos para una factoría concreta, 
	 * sin saber mucho sobre la factoría en si misma.
	 * Por ejemplo, utilizaremos este método cuando mostremos al usuario los posibles valores 
	 * de las leyes de la gravedad. */
	public List<JSONObject> getInfo();
}
