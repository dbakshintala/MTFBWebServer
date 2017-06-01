package com.dbakshintala.mtfbws.worker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.dbakshintala.mtfbws.http.MTFBHttpRequest;
import com.dbakshintala.mtfbws.http.MTFBHttpRequest.HttpMethod;
import com.dbakshintala.mtfbws.http.MTFBHttpResponse;
import com.dbakshintala.mtfbws.http.MTFBHttpResponse.StatusCode;
/**
 * This is a worker class which handles the requests
 * @author DurgaAkshintala
 *
 */
public class MTFBWorkerRunnable implements Runnable {
	
	private static final Logger logger = Logger.getLogger(MTFBWorkerRunnable.class);

	protected Socket clientSocket = null;
	protected String serverText   = null;
	private MTFBHttpRequest httpRequest;
	private MTFBHttpResponse httpResponse;
	
	private final String root;

	/**
     * Class constructor receiving the client socket and server root.
     * @param clientSocket the client socket
     * @param root the root of the server
     */
	public MTFBWorkerRunnable(Socket clientSocket, String root) {
			
		this.clientSocket = clientSocket;
		this.root   = root;
	}

	public void run() {
		try {
			
			httpRequest = new MTFBHttpRequest(clientSocket.getInputStream()).parseRequest();
           
            if (httpRequest != null) {
				logger.info("Request for " + httpRequest.getUrl() + " is being processed " +
					"by socket at \"localhost\" port:"+ clientSocket.getLocalPort());
								
				String method;
				if ((method = httpRequest.getMethod()).equals(HttpMethod.GET) 
						|| method.equals(HttpMethod.HEAD)) {
					File file = new File(root + httpRequest.getUrl());
					httpResponse = new MTFBHttpResponse(StatusCode.OK).getResponseWithFile(file);
					if (method.equals(HttpMethod.HEAD)) {
						httpResponse.removeBody();
					}
				} else {
					httpResponse = new MTFBHttpResponse(StatusCode.NOT_IMPLEMENTED);
				}
				
				printResponse(clientSocket.getOutputStream());
				
			} else {
				System.err.println("Server accepts only HTTP protocol.");
			}
			
		} catch (IOException e) {
			logger.error("Exception Occurred while processing the request: "+e.getMessage());
		}finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    //logger.info("Client disconnected");
                } catch (IOException ex) {
                    logger.error("Couldn't close client socket: " + ex.getMessage());
                }
            }
        }

	}
	
	/**
	 * 
	 * @param response
	 */
	public void printResponse(OutputStream outputStream) {
		String toSend = httpResponse.toString();
		PrintWriter writer = new PrintWriter(outputStream);
		writer.write(toSend);
		writer.flush();
	}

    
    

}
