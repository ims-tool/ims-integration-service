package br.com.increaseit.method;

import java.util.Map;

import org.json.JSONObject;

import br.com.increaseit.util.Constants;

public class MappedMethods {

	public void getCustomer(JSONObject object, Map<String, String> parameters) {
		
		
		object.put("called", "getCustomer");
		/**
		 * Atualizar contexto
		 */
		
		
		object.put(Constants.RETURN_VALUE, Constants.VALUE_OK);
		object.put(Constants.RETURN_CODE, Constants.CODE_OK);
		
		
		

	}
	
}
