import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {
	public static List<JSONObject> jsonDataList = new ArrayList<>();

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
            
            switch (method) {
                case "POST":
                    handlePost(exchange);
                    break;
                case "GET":
                    handleGet(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1); // Method not allowed
                    break;
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
            int contentLength = Integer.parseInt(headers.getFirst("Content-Length"));
            StringBuilder requestBody = new StringBuilder(contentLength);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            System.out.println("Request Body: " + requestBody.toString());

            try {
                JSONParser parser = new JSONParser();
                JSONObject requestData = (JSONObject) parser.parse(requestBody.toString());
                String action = (String) requestData.get("action");

                JSONObject response = new JSONObject();
                response.put("message", "POST received");
                response.put("data", requestData);

                if ("event".equals(action)) {
                    EventRegister eventregister = new EventRegister();
                    int eventResponse = eventregister.sendData(requestData);
                    response.put("projectID", eventResponse);
                } else if ("join".equals(action)) {
                    JoinRegister joinregister = new JoinRegister();
                    joinregister.sendData(requestData);
                }

                String jsonResponse = response.toJSONString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                // 成功時のレスポンスコードをセット
                exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }

            } catch (ParseException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, 0); // Bad request
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0); // Internal server error
            }        
            }

        private void handleGet(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            System.out.println("OK");
            JSONArray jsonArray = new JSONArray();
            String query = exchange.getRequestURI().getQuery(); // Get query parameters from URI

            if (query != null) {
                // Split query parameters by '&'
                String[] params = query.split("&");
                int requestedProjectID = -1;
                
                // Search for 'projectID=' parameter
                for (String param : params) {
                    if (param.startsWith("projectID=")) {
                        requestedProjectID = Integer.parseInt(param.substring("projectID=".length()));
                        break;
                    }
                }
                
                // Process based on requestedProjectID
                if (requestedProjectID != -1) {
                    for (JSONObject data : jsonDataList) {
                        int projectID = ((Long) data.get("projectID")).intValue(); // Cast to int from Long
                        if (projectID == requestedProjectID) {
                            jsonArray.add(data);
                        }
                    }
                } else {
                    // If no valid projectID parameter is provided, return all data
                    for (JSONObject data : jsonDataList) {
                        jsonArray.add(data);
                    }
                }
            } else {
                // If no query parameters are provided, return all data
                for (JSONObject data : jsonDataList) {
                    jsonArray.add(data);
                }
            }

            // Prepare response
            String jsonDataString = jsonArray.toJSONString();
            System.out.println(jsonDataString);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonDataString.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonDataString.getBytes(StandardCharsets.UTF_8));
            }
        }



    }
}

