# Multi-threaded web server
A multi-threaded (e.g. file-based) web server with thread-pooling
implemented in Java.


### Request flow
1. Server receives a request.
2. Server creates a Worker (Runnable) for this request.
3. Worker parses the request into a HttpRequest, Based on the request headers the response is recieved worker prints to output.
4. Worker closes the streams.

** Default Port: 9090, Default root : web (if the respective values are missing)

A compiled version `MTFBWebServer-1.0-SNAPSHOT.jar` can be found in the root directory.
 
`MTFBWebServer` - listens for connections and delegates them to worker threads.

`MTFBWorkerRunnable` - handles a single request, by parsing it and prints the status. The static files are stored "web" folder and will be served on request.


## Installation
 `mvn clean install`
## Usage
`java -jar MTFBWebServer-<version>.jar <port> <root>`
