import org.json.simple.JSONObject;

public class JoinRegister {
	public void sendData(JSONObject requestData) {
		String string=(String)requestData.get("projectID");
		if(string!=null) {
			int projectID=Integer.parseInt(string);
			
			System.out.println(projectID);
		}else {
			System.out.println("通信エラーが発生しました");
		}
	}
}
