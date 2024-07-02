import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JoinRegister {
    String displayName = "新保";
    int userID = 1;
    public void sendData(JSONObject requestData) {
        String string = (String) requestData.get("projectID");
        if (string != null) {
            int projectID = Integer.parseInt(string);
            System.out.println(projectID);

            ProjectInfo cons = new ProjectInfo();
            ProjectInfo project = cons.getProjectInfo(projectID);
            if("Registration".equals(project.progressStatus)) {
                UserAndProjectInfo userproject = new UserAndProjectInfo;
                useproject.userID=userID;
                useproject.projectID=projectID;
                setUserAndProjectInfo()
            }

            for (JSONObject data : SimpleHttpServer.jsonDataList) {
            	Number projectIDNumber = (Number) data.get("projectID");
            	int existingProjectID = projectIDNumber.intValue();
                if (existingProjectID == projectID) {
                    // Add participants array if it does not exist
                    if (!data.containsKey("participants")) {
                        data.put("participants", new JSONArray());
                    }

                    // Get the participants array
                    JSONArray participantsArray = (JSONArray) data.get("participants");

                    // Check if the participant already exists
                    boolean participantExists = false;
                    for (Object obj : participantsArray) {
                        JSONObject participant = (JSONObject) obj;
                        if (participant.get("userID").equals(userID)) {
                            participantExists = true;
                            break;
                        }
                    }

                    // Add the participant if they do not already exist
                    if (!participantExists) {
                        JSONObject participant = new JSONObject();
                        participant.put("displayName", displayName);
                        participant.put("userID", userID);
                        participantsArray.add(participant);
                    }

                    break;
                }
            }
        }
    }
}
