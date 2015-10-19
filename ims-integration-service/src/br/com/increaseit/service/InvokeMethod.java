package br.com.increaseit.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.increaseit.util.Constants;


@Path("/InvokeMethod")
public class InvokeMethod {

	  @Path("/execute")	  
	  @POST
	  @Produces("application/json")	  
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Response execute(String object) throws JSONException {

		  /**
		   * initializing variables
		   */
		  
		  final JSONObject objectIn = new JSONObject(object);
		  
		  boolean active = true;
		  
		  int timeout = 5;
		  
		  final String method = objectIn.get("method").toString();
		  
		  final Map<String, String> parameters = new HashMap<String, String>();
		  
		  final JSONObject objectOut = new JSONObject();
		  
		  objectOut.put("context", objectIn.get("context").toString());
		
		  
		  if(objectIn.has("active")) {
			  if(objectIn.get("active").toString().equals("false")) {
				  active = false;
			  }
		  }
		  if(objectIn.has("timeout")) {
			  
			  if(StringUtils.isNumeric(objectIn.get("timeout").toString())) {
				  timeout = Integer.valueOf(objectIn.get("timeout").toString());
			  }
		  }
		  if(objectIn.has("parameters")) { 
			  JSONObject jsParameters = new JSONObject(objectIn.get("parameters").toString());
			  
			  Iterator<?> keys = jsParameters.keys();

			  while( keys.hasNext() ) {
			      String key = (String)keys.next();
			      parameters.put(key, jsParameters.get(key).toString());			      
			  }
		  }
		  
		  		 
		  /**
		   * calling method
		   * 
		   */
		  if(active) {
			  final Runnable thread = new Thread() {
				  @Override 
				  public void run() { 
					  try {
						execute(method,objectOut, parameters);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				  }
				};
				
			  ExecutorService executor = Executors.newSingleThreadExecutor();
			  final Future<?> future = executor.submit(thread);
			  executor.shutdown();			
			  try {
				  future.get(timeout, TimeUnit.SECONDS);
			  } catch (TimeoutException e ) {
				  e.printStackTrace();	
				  future.cancel(true);
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
			  if (!executor.isTerminated())
				    executor.shutdownNow();


		  } else {
			  objectOut.put(Constants.RETURN_CODE, Constants.CODE_OK);
			  objectOut.put(Constants.RETURN_MSG, "Method "+method+" with status INACTIVE");
		  }
		  
		  return Response.status(200).entity(objectOut.toString()).build();
		
		
		
	  }
	  

	  public void execute(final String methodName,JSONObject object, final Map<String, String> parameters) throws Exception {
		  try {
				Class<?> cls = Class.forName("br.com.increaseit.method.MappedMethods");
				Method method = cls.getDeclaredMethod(methodName, JSONObject.class, Map.class);
				
								
				Object obj = cls.newInstance();
				method.invoke(obj, object, parameters);

		  } catch (NoSuchMethodException e) {
				System.out.println("M�todo " + methodName + " n�o encontrado");
				
		  }
	  }
		
	  
	  public JSONObject execute(final String methodName,final String context, final Map<String, String> parameters) throws Exception {
		  JSONObject objectOut = new JSONObject();
		  objectOut.put("context", context);
		  try {
				Class<?> cls = Class.forName("br.com.increaseit.method.MappedMethods");
				Method method = cls.getDeclaredMethod(methodName, JSONObject.class, Map.class);
				
								
				Object obj = cls.newInstance();
				method.invoke(obj, objectOut, parameters);

		  } catch (NoSuchMethodException e) {
				System.out.println("M�todo " + methodName + " n�o encontrado");
				
		  }
		  System.out.println(objectOut.toString());
		  return objectOut;
		  
	}
	  	

}
