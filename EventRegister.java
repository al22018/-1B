import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;

/**
 * File Name: EventRegister.java
 * Version: V1.0
 * Designer: 新保陽己
 * Date: 06/10
 * Purpose: UIサーバよりデータを受け取り、C8 企画情報管理部へ送信する。
 *          通信失敗時には、通信エラーが起きたことを報告し、W2ホーム画面に戻る。
 */
public class EventRegister {
	private static final AtomicInteger projectIDCounter = new AtomicInteger(1);
	String managerName;
	int managerID;
	String projectName;
	String category;
	String region;
	String timeHour;
	String timeMinute;
	Timestamp dateTime;
	public int sendData(JSONObject requestBody) {
		int projectID=projectIDCounter.getAndIncrement();
		managerName = (String) requestBody.get("managerName");
		managerID = ((Number) requestBody.get("managerID")).intValue();
		projectName = (String) requestBody.get("projectName");
		category = (String) requestBody.get("category");
		region = (String) requestBody.get("region");
		timeHour = (String) requestBody.get("timeHour");
		timeMinute = (String) requestBody.get("timeMinute");
		LocalDate epochDate = LocalDate.of(1970, 1, 1);
		LocalTime localTime = LocalTime.of(Integer.parseInt(timeHour), Integer.parseInt(timeMinute));
		LocalDateTime localDateTime = LocalDateTime.of(epochDate, localTime);
		dateTime = Timestamp.valueOf(localDateTime);
		String value = projectName + category + region + dateTime;
		//データベースに保存
		/*ProjectInfo project = new ProjectInfo();
		project.projectID = 1; // 例
		project.projectName = projectName;
		project.dateTime = dateTime; // タイムスタンプ
		project.category = category;
		project.destination = "目的地";
		project.managerID = 123; // 例
		project.region = region;
		project.progressStatus = "Registration";*/
		//project.setProjectInfo();//
		JSONObject projectJson = new JSONObject();
		projectJson.put("managerName",managerName);
		projectJson.put("managerID",managerID);
		projectJson.put("projectID",projectID);
        projectJson.put("projectName", projectName);
        projectJson.put("category", category);
        projectJson.put("region", region);
        projectJson.put("dateTime", dateTime.toString());
        MyServlet.jsonDataList.add(projectJson);
        JoinRegister joinRegister = new JoinRegister();
        joinRegister.sendData(projectID,managerName,managerID);
		System.out.println("成功\n" + value);
		return projectID;
		
	}
	public void disposeData(JSONObject requestBody) {
		int projectID = Integer.parseInt((String)requestBody.get("projectID"));
		System.out.println("close:"+projectID);
		synchronized (MyServlet.jsonDataList) {
            Iterator<JSONObject> iterator = MyServlet.jsonDataList.iterator();
            while (iterator.hasNext()) {
                JSONObject data = iterator.next();
                int id = ((Integer) data.get("projectID")).intValue();
                if (id == projectID) {
                    iterator.remove();
                }
            }
        }
	}
	
}
