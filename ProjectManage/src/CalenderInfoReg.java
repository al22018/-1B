/*******************************************************************
*** File Name : CalenderInfoReg.java
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/07/03
*** Purpose : accessTokenを用いてGoogleCalenderに予定の入っている日を取得する
***
*******************************************************************/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ユーザの予定情報を取得するクラス */
public class CalenderInfoReg {
	
	/* ユーザの予定情報をAPIに問い合わせ，成型後に返すメソッド */
	public static String getCalenderInfo(String accessToken) throws Exception {

		// GoogleCalenderの予定情報を取得するAPIのURL
		String url = "https://www.googleapis.com/calendar/v3/freeBusy";

		// GoogleCalenderAPIに渡すプロパティ: 予定情報取得開始時刻
		OffsetDateTime date = OffsetDateTime.now();

		// GoogleCalenderAPIに渡すプロパティ: レスポンスで用いるタイムゾーン
		ZoneId timeZone = ZoneId.of("Asia/Tokyo");

		// GoogleCalenderAPIに渡すプロパティ: カレンダーID
		ArrayList<String> calenderId = getCalenderId(accessToken);
		
		/* GoogleCalenderAPIに渡すプロパティの構成 */
		String param = "{"
				+ "\"timeMin\": \"" + date + "\","
				+ "\"timeMax\": \"" + date.plusMonths(2) + "\","
				+ "\"timeZone\": \"" + timeZone + "\","
				+ "\"items\": [";
		for (int i = 0; i < calenderId.size(); ++i) {
			if (i != 0) {
				param += ",";
			}
			param += "{\"id\": \"" + calenderId.get(i) + "\"}";
		}
		param += "]}";

		/* APIから値の取得 */
		HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.connect();
		OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
		os.write(param);
		os.close();

		// レスポンスを格納する
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String response = "";
		String line;
		while ((line = br.readLine()) != null) {
			response += line;
		}
		br.close();
		conn.disconnect();
		
		/* レスポンスを渡し，成型後の予定が入っている日を取得する */
		String busyDays = busyDays(response);
		
		return busyDays;
	}

	/* API問い合わせに用いるため，ユーザが持っているカレンダーIDを取得するメソッド */
	private static ArrayList<String> getCalenderId(String accessToken) throws Exception {
		// ユーザが持っているカレンダーID
		ArrayList<String> calenderId = new ArrayList<String>();

		// GoogleCalenderのカレンダーIDを取得するAPIのURL
		String url = "https://www.googleapis.com/calendar/v3/users/me/calendarList";
		url = url + "?access_token=" + URLEncoder.encode(accessToken, "UTF-8");

		/* APIから値の取得 */
		HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
		conn.setRequestMethod("GET");

		// レスポンスを格納する
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		String line;

		// レスポンスからカレンダーIDを取得するためのパターン
		Pattern idPattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

		/* レスポンスからカレンダーIDを取得 */
		while ((line = br.readLine()) != null) {
			Matcher matcher = idPattern.matcher(line);
			if (matcher.find()) {
				calenderId.add(matcher.group(1));
			}
		}
		br.close();
		conn.disconnect();
		return calenderId;
	}
	
	/* カレンダー情報を取得した日付の範囲，予定の開始時刻，終了時刻を取得するメソッド */
	public static String busyDays(String responce) throws Exception{ 
		// 成型後の予定が入っている日
		// YYYY-MM-DD YYYY-MM-DD,YYYY-MM-DD YYYY-MM-DD ...
		// 予定情報取得開始日 予定情報取得終了日,予定が入っている日 予定が入っている日 ...
		// 利用する場合はsplit(",")で予定情報取得日と予定が入っている日を分けたあと，split(" ")でそれぞれの日を切り出してください
		String busyDays = "";
		
		// 予定日を入れる配列 成型に利用したもの
		ArrayList<String> busyDaysArray = new ArrayList<String>();
		
		// 予定情報取得開始日,予定情報取得終了日
		OffsetDateTime busyStart = null;
		OffsetDateTime busyEnd = null;
		
		/* レスポンスからデータを取り出す処理 */
		String[] resSplit = responce.split("\"");
		
		// 状態を示す
		// 0:予定情報取得開始日(timeMin)取得待ち，1:予定情報取得終了日(timeMax)取得待ち
		// 2:予定開始時刻(start)取得待ち　　　　, 3:予定終了時刻(end)取得待ち
		int status = 0;
		for (int i = 0; i < resSplit.length; ++i) {
			if (status == 0) {
				if (resSplit[i].equals("timeMin")) {
					busyDays += resSplit[i + 2].split("T")[0] + " ";
					status = 1;
				}
			}
			else if (status == 1) {
				if (resSplit[i].equals("timeMax")) {
					busyDays += resSplit[i + 2].split("T")[0] + ",";
					status = 2;
				}
				
			}
			else if (status == 2) {
				if (resSplit[i].equals("start")) {
					busyStart = OffsetDateTime.parse(resSplit[i + 2]);
					status = 3;
				}
			}
			else if (status == 3) {
				if (resSplit[i].equals("end")) {
					busyEnd = OffsetDateTime.parse(resSplit[i + 2]);
					status = 2;
					
					/* 予定開始時刻，予定終了時刻から予定が入っている日を取得する */
					busyDaysArray = calcBusyDays(busyStart, busyEnd, busyDaysArray);
				}
			}
			else {
				throw new Exception();
			}
		}
		
		/* レスポンス用に予定が入っている日を成型する処理 */
		for (int i = 0; i < busyDaysArray.size(); ++i) {
			if (i != 0) {
				busyDays += " ";
			}
			busyDays += busyDaysArray.get(i);
		}
		return busyDays;
	}
	
	/* 予定開始時刻，予定終了時刻から予定が入っている日を取得するメソッド */
	private static ArrayList<String> calcBusyDays(OffsetDateTime start, OffsetDateTime end, ArrayList<String> busyDays) {
		// 予定開始時刻，予定終了時刻から予定開始日，予定終了日，予定終了時刻を取得する
		LocalDate startDate= start.toLocalDate();
		LocalDate endDate= end.toLocalDate();
		LocalTime endTime= end.toLocalTime();
		
		/* 予定終了時刻が午前0時ピッタリの場合，その日は予定がある日年Þえ扱わない */
		if (endTime.toString().equals("00:00")) {
			endDate = endDate.minusDays(1);
		}
		
		/* 予定開始日を一つずつ進め，予定終了日と同じになるまで予定がある日として取得する */
		while (startDate.compareTo(endDate) <= 0) {
			if (!busyDays.contains(startDate.toString())) {
				busyDays.add(startDate.toString());
			}
			startDate = startDate.plusDays(1);
		}
		return busyDays;
	}
}
