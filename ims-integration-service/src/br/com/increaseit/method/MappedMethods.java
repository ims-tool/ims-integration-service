package br.com.increaseit.method;

import java.util.Map;

import org.json.JSONObject;

public class MappedMethods {

	public void getCustomer(JSONObject object, Map<String, String> parameters) {
		
		
		object.put("called", "getCustomer");
		object.put("return", "OK");
		

	}
	
}
