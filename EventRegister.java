import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
	String category;
	String region;
	String timeHour;
	String timeMinute;
	Timestamp dateTime;
	public void sendData(JSONObject requestBody) {

		category = (String) requestBody.get("category");
		region = (String) requestBody.get("region");
		timeHour = (String) requestBody.get("timeHour");
		timeMinute = (String) requestBody.get("timeMinute");
		LocalDate epochDate = LocalDate.of(1970, 1, 1);
		LocalTime localTime = LocalTime.of(Integer.parseInt(timeHour), Integer.parseInt(timeMinute));
		LocalDateTime localDateTime = LocalDateTime.of(epochDate, localTime);
		dateTime = Timestamp.valueOf(localDateTime);
		String value = category + region + dateTime;
		//C8企画情報管理部へ送信
		ProjectInfo project = new ProjectInfo();
		project.projectID = 1; // 例として企画IDを設定します
		project.projectName = "プロジェクト名";
		project.dateTime = dateTime; // タイムスタンプを設定します
		project.category = category;
		project.destination = "目的地";
		project.managerID = 123; // マネージャーのIDを設定します
		project.region = region;
		SimpleHttpServer.jsonData.put("category", category);
        SimpleHttpServer.jsonData.put("region", region);
        SimpleHttpServer.jsonData.put("dateTime", dateTime.toString());
		System.out.println("成功\n" + value);
	}
	
}