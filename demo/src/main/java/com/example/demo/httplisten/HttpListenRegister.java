package com.example.demo.httplisten;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@Component
public class HttpListenRegister {
    
    public static void main(String[] args) {
        InetSocketAddress socketAddr = new InetSocketAddress(8066);
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create(socketAddr, 0);
            httpServer.createContext("/a", new HttpInboundTransport());
            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class HttpInboundTransport implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            OutputStream outputStream = httpExchange.getResponseBody();
            httpExchange.sendResponseHeaders(200, 0);
            outputStream.write("abc123".getBytes());
            outputStream.close();
        }

       

    }
}
