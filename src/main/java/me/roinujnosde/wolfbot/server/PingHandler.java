package me.roinujnosde.wolfbot.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PingHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Pinged by = " + exchange.getRemoteAddress().getHostString());
        exchange.sendResponseHeaders(200, 0);
        OutputStream responseBody = exchange.getResponseBody();
        byte[] bytes = "I'm alive".getBytes(StandardCharsets.UTF_8);
        responseBody.write(bytes);
        responseBody.close();
    }
}
