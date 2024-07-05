
/*************************************************
 * File Name			:Notification.java
 * Version				:Ver1.1
 * Designer				:荻野新
 * Date					:2024.06.16
 * Purpose				:通知関連の制御、情報の取得、通知の実行
 * 
 * Notification Noti = new Notification(projectID);
 * ↑で主処理(各必要データの取得)ができる
 */

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class Notification {

	String subject = "";// メールの要件名
	String text = "";// メール本文
	String from = "システムのメールアドレス";// 送信元のメールアドレス。
	ArrayList<String> to = new ArrayList<String>();// 送信先のメールアドレスのリスト
	String host = "smtp.gmail.com";// SMTPサーバのアドレス

	final String username = new String(from);// googleのメールを使用するのに必要
	final String password = "";// googleアカウント二段階認証をオンにしたうえでGoogleアカウント->設定->アプリパスワード で生成したパスワード

	ProjectInfo project = new ProjectInfo();// 企画の詳細の情報を持つクラス
	String weatherSentence = "";// メールで送る天気情報の文章

	WeatherOfPrefecture weather;// 都道府県の天気情報を検索、保持するクラス
	String leader = "";

	// コンストラクタでprojectIDから企画情報とメール送信先を取得
	Notification(int projectID) {

		ProjectInfo pro = new ProjectInfo();
		project = pro.getProjectInfo(projectID);
		UserInfo usr = new UserInfo();
		to = usr.getEmails(projectID);
		/*
		 * 幹事の名前を取得を追加(ver1.1)
		 */
		leader = usr.getUserName(project.managerID);

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
		String managerName = 
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
		project.projectNmae = "test project";
		project.dateTime = new DateTime(2024, 7, 2, 13, 20);
		project.category = "test";
		project.destination = "test destination";
		project.managerID = "test manager";
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

	public static void main(String[] args) {
		Notification Noti = new Notification(1);
		Noti.notificationTest();
	}
}
