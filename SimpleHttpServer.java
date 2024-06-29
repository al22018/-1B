import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {
	public static JSONObject jsonData = new JSONObject();

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Server started on port " + port);

        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if (method.equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            if (method.equalsIgnoreCase("POST")) {
                handlePost(exchange);
            } else if (method.equalsIgnoreCase("GET")) {
                handleGet(exchange);
            }
        }

        private void handleOptions(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(200, -1);
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getRequestHeaders();
            int contentLength = Integer.parseInt(headers.getFirst("Content-length"));
            byte[] requestBodyBytes = new byte[contentLength];
            exchange.getRequestBody().read(requestBodyBytes);

            String requestBody = new String(requestBodyBytes);
            System.out.println("Request Body: " + requestBody);

            try {
                JSONParser parser = new JSONParser();
                JSONObject requestData = (JSONObject) parser.parse(requestBody);
                String action = (String) requestData.get("action");
               
                JSONObject response = new JSONObject();
                response.put("message", "POST received");
                response.put("data", requestData);
                
                String jsonResponse = response.toJSONString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                if ("event".equals(action)) {
                    EventRegister eventregister = new EventRegister();
                    eventregister.sendData(requestData);
                } else if ("join".equals(action)) {
                    JoinRegister joinregister = new JoinRegister();
                    joinregister.sendData(requestData);
                }
                os.close();
            } catch (ParseException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, 0); // Bad request
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            // JSONデータを文字列に変換
            // レスポンスを送信
        	 String jsonDataString = jsonData.toJSONString();
        	 System.out.println(jsonDataString);
             exchange.getResponseHeaders().set("Content-Type", "application/json");
             exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
             exchange.sendResponseHeaders(200, jsonDataString.getBytes().length);
             OutputStream os = exchange.getResponseBody();
             os.write(jsonDataString.getBytes());
             os.close();
        }
    }
}

