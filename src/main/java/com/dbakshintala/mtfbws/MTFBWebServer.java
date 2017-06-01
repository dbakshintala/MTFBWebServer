package com.dbakshintala.mtfbws;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dbakshintala.mtfbws.worker.MTFBWorkerRunnable;

/**
 * This class creates a multi-threaded web server with thread-pooling.
 */
public class MTFBWebServer {

    private static final Logger logger = Logger.getLogger(MTFBWebServer.class.getCanonicalName());

    private static final int DEFAULT_PORT = 9090;
    private static final int MAX_THREADS = 10;
    private static final String DEFAULT_ROOT = "web/\\";


    private final String root;
    private final int port;

    private ServerSocket serverSocket;

    private ExecutorService threadPool;

    /**
     * Class constructor that receives a server root and a port.
     * @param root The root of the server.
     * @param port Port used by the server.
     */
    public MTFBWebServer(int port, String root) {
        this.root = root;
        this.port = port;
    }

    /**
     * Starts the web server.
     */
    public void start() {
        threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server listening on port " + port + "(press CTRL-C to quit)");

            // listen for client connections
            while (true) {
                Socket client;
                try {
                    client = serverSocket.accept();                    
                    threadPool.submit(new MTFBWorkerRunnable(client,root));
                    
                } catch (IOException ex) {
                    logger.error("Client couldn't connect: " + ex.getMessage());
                } catch (RejectedExecutionException ex) {
                    logger.error("Couldn't start server task: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            logger.error("Couldn't start web server at port " + port + ": " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid Port " + port + " " + ex.getMessage());
        } catch (RuntimeException ex) {
            logger.error("Runtime exception: " + ex.getMessage());
        } finally {
            stop();
        }
    }

    /**
     * Closes the socket and shutsdown the thread pool.
     */
    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                logger.info("Server stopped");
            } catch (IOException ex) {
                logger.error("Couldn't stop web server: " + ex.getMessage());
            }
        }

        try {
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            logger.info("Running tasks interrupted");
        }
        finally {
            threadPool.shutdownNow();
        }
    }

    /**
     * Main program entry.
     * @param args First argument is be a valid port number (>0).
     * 				Second argument must be the server's root directory.
     */
    public static void main(String[] args) {
       
       
        
        int port = DEFAULT_PORT;
        String root = DEFAULT_ROOT;
        if (args.length > 0) {
        	// read port argument
            try {
                port = Integer.parseInt(args[0]);

                // if port is out of range, use the default one
                if (port < 0 || port > 65535) {
                    logger.info("Invalid port, using default one");
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException ex) {
                logger.error("Port not a number: " + ex.getMessage());
            }
        }
            
        // read root argument
 		
 		if (args.length > 1) {
 			root = args[1];
 			if(root==null || "".equals(root)){
 				root = DEFAULT_ROOT;
 				logger.info("Server root is null, using the default root : "+ root);
 			} 			
 		}        

        // create and start web server
        MTFBWebServer server = new MTFBWebServer(port,root);
        if (server != null) {
            server.start();
        }
    }
}
