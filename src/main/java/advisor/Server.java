package advisor;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer server;
    private String responseShownOnWebsite;
    private String query;

    public Server() {
        try {
            server = HttpServer.create(new InetSocketAddress(8888), 0);
            server.createContext("/SZAWspotify", httpExchange -> {
                System.out.println("____Link has been clicked.");

                query = httpExchange.getRequestURI().getQuery();

                if (query == null) {
                    responseShownOnWebsite = "Waiting for code.";
                } else if (isPotentialCode()) {
                    responseShownOnWebsite = "Got the code. Return back to your program.";
                } else {
                    responseShownOnWebsite = "Authorization code not found. Try again.";
                }

                httpExchange.sendResponseHeaders(200, responseShownOnWebsite.length());
                httpExchange.getResponseBody().write(responseShownOnWebsite.getBytes());
                httpExchange.getResponseBody().flush();
                httpExchange.getResponseBody().close();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPotentialCode() {
        return query.contains("code=") && query.length() > 5;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    public String getQuery() {
        return query;
    }
}
