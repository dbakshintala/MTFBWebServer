package com.dbakshintala.mtfbws.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
/**
 * HttpRequest Class, this class parses the request
 *
 */
public class MTFBHttpRequest {
	
	 private static final Logger logger = Logger.getLogger(MTFBHttpRequest.class);

	   	private InputStream inputStream;
	    private String method;
		private String url;
	    private String protocol;
	    private List<String> body = new ArrayList<String>();
	    private NavigableMap<String, String> headers = new TreeMap<String, String>();
	    
	    /**
	     * Class constructor that receives a inputStream from the client.
	     * @param in the client's reader
	     */
	    public MTFBHttpRequest(InputStream inputStream) {
	        this.inputStream = inputStream;
	    }
	    /**
	     * No arg Constructor
	     */
	    public MTFBHttpRequest() {
			// TODO Auto-generated constructor stub
		}
	    
	    /**begin setter,getter methods **/
	    
	    public void setMethod(String method) {
			this.method = method;
		}
	    public String getMethod() {
			return method;
		}
	    
	    public void setUrl(String url){
	    	this.url = url;
	    }
	    
	    public String getUrl(){
	    	return this.url;
	    }
	    
	    public void setProtocol(String protocol){
	    	this.protocol = protocol;
	    }
	    
	    public String getProtocol(){
	    	return this.protocol;
	    }
	    /** end of setter,getter methods**/

		/**
	     * This method parses the request.
	     * @return
	     */
	    public MTFBHttpRequest parseRequest(){
	    	
	    	try{
		    	MTFBHttpRequest httpRequest = new MTFBHttpRequest();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = reader.readLine();
				if (line == null) {
					throw new IOException("Server accepts only HTTP requests.");
				}
				String[] requestLine = line.split(" ", 3);
				if (requestLine.length != 3) {
					throw new IOException("Cannot parse request line from \"" + line + "\"");
				}
				if (!requestLine[2].startsWith("HTTP/")) {
					throw new IOException("Server accepts only HTTP requests.");
				}
				httpRequest.setMethod(requestLine[0]);
				httpRequest.setUrl(requestLine[1]);
				httpRequest.setProtocol(requestLine[2]);
				
				line = reader.readLine();
				while(line != null && !line.equals("")) {
					String[] header = line.split(": ", 2);
					if (header.length != 2)
						throw new IOException("Cannot parse header from \"" + line + "\"");
					else 
						httpRequest.headers.put(header[0], header[1]);
					line = reader.readLine();
				}
				
				while(reader.ready()) {
					line = reader.readLine();
					httpRequest.body.add(line);
				}
				
				return httpRequest;
	    	} catch (IOException e) {
				logger.error("Exception occurred while parsing the request:: "+e.getMessage());
			}
			return null;
	    }
	    

	    @Override
		public String toString() {
			String result = method + " " + url + " " + protocol + "\n";
			for (String key : headers.keySet()) {
				result += key + ": " + headers.get(key) + "\n";
			}
			result += "\r\n";
			for (String line : body) {
				result += line + "\n"; 
			}
			return result;
		}
	    
		
	    /**
	     * Inner Class for HTTPMethods (GET, POST, DELETE etc).
	     * @author DurgaAkshintala
	     *
	     */
		public static class HttpMethod {
			public static final String GET = "GET";
			public static final String HEAD = "HEAD";
			public static final String POST = "POST";
			public static final String DELETE = "DELETE";
		}

}
