import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/data")
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static List<JSONObject> jsonDataList = new ArrayList<>();

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
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

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", "POST received");
            jsonResponse.put("data", requestData);

            if ("event".equals(action)) {
                EventRegister eventRegister = new EventRegister();
                int eventResponse = eventRegister.sendData(requestData);
                jsonResponse.put("projectID", eventResponse);
            } else if ("join".equals(action)) {
                JoinRegister joinRegister = new JoinRegister();
                joinRegister.sendData(Integer.parseInt((String) requestData.get("projectID")),(String) requestData.get("displayName"),2);
            } else if("dispose".equals(action)) {
            	EventRegister eventRegister = new EventRegister();
            	eventRegister.disposeData(requestData);
            }

            String jsonResponseString = jsonResponse.toJSONString();
            response.setContentType("application/json");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setStatus(HttpServletResponse.SC_OK);
            try (OutputStream os = response.getOutputStream()) {
                os.write(jsonResponseString.getBytes(StandardCharsets.UTF_8));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONArray jsonArray = new JSONArray();
        
        try {
            String projectIDParam = request.getParameter("projectID");

            if (projectIDParam != null) {
                int requestedProjectID = Integer.parseInt(projectIDParam);

                // Search for matching projectID in jsonDataList
                for (JSONObject data : jsonDataList) {
                    int projectID = ((Integer) data.get("projectID")).intValue(); // Cast to int from Long
                    if (projectID == requestedProjectID) {
                        jsonArray.add(data);
                    }
                }
            } else {
                // If no projectID parameter provided, return all data
                for (JSONObject data : jsonDataList) {
                    jsonArray.add(data);
                }
            }

            // Prepare response
            String jsonDataString = jsonArray.toJSONString();
            System.out.println("response"+jsonDataString);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            try (OutputStream os = response.getOutputStream()) {
                os.write(jsonDataString.getBytes(StandardCharsets.UTF_8));
            }
        } catch (NumberFormatException e) {
            // Handle invalid projectID parameter
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid projectID parameter");
            e.printStackTrace(); // Optionally log the exception
        } catch (Exception e) {
            // Handle other exceptions
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error");
            e.printStackTrace(); // Optionally log the exception
        }
    }

}
