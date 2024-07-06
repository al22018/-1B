
/*************************************************
 * File Name			:Notification.java
 * Version				:Ver1.0
 * Designer				:荻野新
 * Date					:2024.06.16
 * Purpose				:通知関連の制御、情報の取得、通知の実行
 * 
 * Notification Noti = new Notification(projectID);
 * ↑で主処理(各必要データの取得)ができる
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Notification {

	String subject = "";// メールの要件名
	String text = "";// メール本文
	String from = "aratanew2525@gmail.com";// 送信元のメールアドレス。
	ArrayList<String> to = new ArrayList<String>();// 送信先のメールアドレスのリスト
	String host = "smtp.gmail.com";// SMTPサーバのアドレス
	String leader = "test leader";// 幹事の名前

	final String username = new String(from);// googleのメールを使用するのに必要
	final String password = "ecre aqcs cysz eaoi";// googleアカウント二段階認証をオンにしたうえでGoogleアカウント->設定->アプリパスワード で生成したパスワード

	ProjectInfo project = new ProjectInfo();// 企画の詳細の情報を持つクラス
	String weatherSentence = "";// メールで送る天気情報の文章

	WeatherOfPrefecture weather;// 都道府県の天気情報を検索、保持するクラス

	// コンストラクタでprojectIDから企画情報とメール送信先を取得
	Notification(int projectID) {

		ProjectInfo pro = new ProjectInfo();
		project = pro.getProjectInfo(projectID);
		UserInfo usr = new UserInfo();
		to = usr.getEmails(projectID);
		to.add("al22091@shibaura-it.ac.jp");

	}

	// toリストに保存されている宛先にメールを送信するメソッド
	public boolean notificationFirst() {
		String dateTimeStr = project.dateTime.toString();
		String[] tmp = dateTimeStr.split(" ");
		String tmpDate = tmp[0];
		String tmpTime = tmp[1];
		String year = tmpDate.split("-")[0];
		String month = tmpDate.split("-")[1];
		String date = tmpDate.split("-")[2];
		String hour = tmpTime.split(":")[0];
		String minute = tmpTime.split(":")[1];
		// メール末尾の注意書き
		String note = "\n\n\n遊びの予定自動でたてるシステムより。\n※このメールは自動送信です。\n企画に関しての詳細のお問い合わせは幹事の"
				+ leader + "にお問い合わせください。\nシステムに関するお問い合わせは09班までお問い合わせください。";

		subject = "遊び企画[" + project.projectName + "] の詳細";
		text = "遊び企画[" + project.projectName + "]の目的地は" + project.destination + ", 日時は" + year + "年" + month + "月"
				+ date + "日, " + hour + "時" + minute + "分"
				+ "です。" + note;

		// メール送信
		try {

			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(text);
			for (int i = 0; i < to.size(); i++) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.get(i)));
				Transport.send(message);
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	// 2回目のメールを送信
	public boolean notificationSecond() {
		String dateTimeStr = project.dateTime.toString();
		String[] tmp = dateTimeStr.split(" ");
		String tmpDate = tmp[0];
		String tmpTime = tmp[1];
		String year = tmpDate.split("-")[0];
		String month = tmpDate.split("-")[1];
		String date = tmpDate.split("-")[2];
		String hour = tmpTime.split(":")[0];
		String minute = tmpTime.split(":")[1];
		// メール末尾の注意書き
		String note = "\n\n\n遊びの予定自動でたてるシステムより。\n※このメールは自動送信です。\n企画に関しての詳細のお問い合わせは幹事の"
				+ leader + "にお問い合わせください。\nシステムに関するお問い合わせは09班までお問い合わせください。";

		subject = "遊び企画[" + project.projectName + "] の予定が近くなってきました！！";
		text = "遊び企画[" + project.projectName + "]の目的地は" + project.destination + ", 日時は" + year + "年" + month + "月"
				+ date + "日, " + hour + "時" + minute + "分"
				+ "です。" + note;

		GetWeather getWeather = new GetWeather();
		try {
			weatherSentence = project.destination + "の位置する" + project.region + "の天気は\n";
			weather = getWeather.getForecast(project.region);
			for (int i = 0; i < weather.cityIDs.size(); i++) {
				weatherSentence += weather.areas.get(i) + " : ";
				weatherSentence += weather.weatherOfArea.get(i) + "\n";
			}
		} catch (Exception e) {

		}
		try {

			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(text);
			for (int i = 0; i < to.size(); i++) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.get(i)));
				Transport.send(message);
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public boolean notificationTest() {
		ProjectInfo project = new ProjectInfo();
		project.projectName = "test project";
		project.dateTime = new Timestamp(124, 6, 2, 13, 20, 0, 0);// 年は-1900する必要あり、月はn月→n-1に設定する必要あり
		project.category = "test";
		project.destination = "test destination";
		project.managerID = 0;
		project.region = "test region";
		project.progressStatus = "test progress status";
		String leader = "test leader";
		String dateTimeStr = project.dateTime.toString();
		String[] tmp = dateTimeStr.split(" ");
		String tmpDate = tmp[0];
		String tmpTime = tmp[1];
		String year = tmpDate.split("-")[0];
		String month = tmpDate.split("-")[1];
		String date = tmpDate.split("-")[2];
		String hour = tmpTime.split(":")[0];
		String minute = tmpTime.split(":")[1];
		// メール末尾の注意書き
		String note = "\n\n\n遊びの予定自動でたてるシステムより。\n※このメールは自動送信です。\n企画に関しての詳細のお問い合わせは幹事の"
				+ leader + "にお問い合わせください。\nシステムに関するお問い合わせは09班までお問い合わせください。";

		subject = "遊び企画[" + project.projectName + "] の詳細";
		text = "遊び企画[" + project.projectName + "]の目的地は" + project.destination + ", 日時は" + year + "年" + month + "月"
				+ date + "日, " + hour + "時" + minute + "分"
				+ "です。" + note;

		// メール送信
		System.out.println("メール送信");
		try {

			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			System.out.println("session");

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(text);
			System.out.println(to.size());
			for (int i = 0; i < to.size(); i++) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.get(i)));
				Transport.send(message);
				System.out.println("送信しました");
			}
			return true;

		} catch (Exception e) {
			System.out.println("失敗");
			e.printStackTrace();
			return false;
		}
	}

	public boolean notificationTestSecond() {
		ProjectInfo project = new ProjectInfo();
		project.projectName = "test project";
		project.dateTime = new Timestamp(124, 6, 6, 22, 20, 0, 0);// 年は-1900する必要あり、月はn月→n-1に設定する必要あり
		project.category = "test";
		project.destination = "test destination";
		project.managerID = 0;
		project.region = "test region";
		project.progressStatus = "test progress status";
		String leader = "test leader";
		String dateTimeStr = project.dateTime.toString();
		String[] tmp = dateTimeStr.split(" ");
		String tmpDate = tmp[0];
		String tmpTime = tmp[1];
		String year = tmpDate.split("-")[0];
		String month = tmpDate.split("-")[1];
		String date = tmpDate.split("-")[2];
		String hour = tmpTime.split(":")[0];
		String minute = tmpTime.split(":")[1];
		// メール末尾の注意書き
		String note = "\n\n\n遊びの予定自動でたてるシステムより。\n※このメールは自動送信です。\n企画に関しての詳細のお問い合わせは幹事の"
				+ leader + "にお問い合わせください。\nシステムに関するお問い合わせは09班までお問い合わせください。";

		subject = "遊び企画[" + project.projectName + "] の予定が近くなってきました！！";
		text = "遊び企画[" + project.projectName + "]の目的地は" + project.destination + ", 日時は" + year + "年" + month + "月"
				+ date + "日, " + hour + "時" + minute + "分"
				+ "です。" + note;

		GetWeather getWeather = new GetWeather();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(year) + 1900);
		calendar.set(Calendar.MONTH, Integer.parseInt(month));
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date scheduledTime = calendar.getTime();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					weatherSentence = project.destination + "の位置する" + project.region + "の天気は\n";
					weather = getWeather.getForecast(project.region);
					for (int i = 0; i < weather.cityIDs.size(); i++) {
						weatherSentence += weather.areas.get(i) + " : ";
						weatherSentence += weather.weatherOfArea.get(i) + "\n";
					}
				} catch (Exception e) {

				}
				try {

					Properties props = new Properties();
					props.put("mail.smtp.host", host);
					props.put("mail.smtp.port", "587");
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");

					Session session = Session.getInstance(props, new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});

					Message message = new MimeMessage(session);

					message.setFrom(new InternetAddress(from));
					message.setSubject(subject);
					message.setText(text);
					for (int i = 0; i < to.size(); i++) {
						message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.get(i)));
						Transport.send(message);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		timer.schedule(task, scheduledTime);
		return true;

	}

	public static void main(String[] args) {
		Notification Noti = new Notification(0);
		Noti.notificationTest();
		// Noti.notificationTestSecond();

	}
}
