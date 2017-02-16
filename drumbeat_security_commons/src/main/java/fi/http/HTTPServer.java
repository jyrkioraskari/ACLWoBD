package fi.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


// http://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api


public class HTTPServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8300), 0);
        server.createContext("/company1", new MyHandler1());
        server.createContext("/company2", new MyHandler2());
        server.setExecutor(null); // creates a default executor
        server.start();
        
        

    }

    static class MyHandler1 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	System.out.println(t.getHttpContext().getPath());
            String response = "This is the response 1";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    
    static class MyHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	System.out.println(t.getHttpContext().getPath());
            String response = "This is the response 2";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}