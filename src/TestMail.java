import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import junit.framework.TestCase;

public class TestMail extends TestCase {

    /**
    * SMTP認証を使ってメールを送信する
    * username, password にはAPIユーザ設定にて作成した
    * APIユーザ名とAPIキーを指定します。
    */
    public void testSendMessageWithAuth() throws MessagingException {
        try {
            // メッセージを生成する
            Message message = setup();
            // SMTP認証を使ってメールを送信する
            CMCTransport.send(message, "username", "password");
        } catch (SendFailedException e) {
            fail(e.getMessage());
        }
    }

    /**
    * SMTP認証を使わずにメールを送信する
    * サービスプラン Pro でIPアクセス認証を設定した場合、
    * SMTP認証無しでメールを送信することができます。
    */
    public void testSendMessage() {
        try {
            // メッセージを生成する
            Message message = setup();
            // メールを送信する
            CMCTransport.send(message));
        } catch (SendFailedException e) {
            fail(e.getMessage());
        }
    }

    private Message setup() {

        // Properties を作成して、SMTPホスト名を設定する。
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "SUBDOMAIN.smtps.jp");

        // Session を作成する
        // getDefaultInstance はシングルトンのSessionオブジェクトを
        // 返却するため、CMCTransportが正常に動作しません。
        // 必ず getInstance メソッドを使用して Session オブジェクトを
        // 作成してください。
        Session session = Session.getInstance(prop);

        // Message を作成する
        MimeMessage message = new MimeMessage(session);

        try {

            // 宛先(To)アドレスを設定
            message.setRecipient(RecipientType.TO,
                    new InternetAddress("sample@smtps.jp"));

            // 差出人(From)アドレスを設定
            message.setFrom(
                    new InternetAddress("sample@smtps.jp"));

            // 送信日時(Date)を設定
            message.setSentDate(new Date());

            // メールの件名を設定
            message.setSubject("【自動返信】お問い合わせについて", "ISO-2022-JP");

            // メール本文を設定
            message.setText(
                "HENNGE株式会社\r\n" +
                "山田太郎様\r\n" +
                "\r\n" +
                "この度は当社サービスへのお問い合わせ誠にありがとうございます。\r\n" +
                "お問い合わせにつきまして折り返しご連絡いたします。\r\n" +
                "いましばらくお時間頂けますよう宜しくお願いいたします。\r\n",
                "ISO-2022-JP");

            // Content-Transfer-Encoding を 7bit に設定
            message.setHeader("Content-Transfer-Encoding", "7bit");

        } catch (AddressException e) {
            fail(e.getMessage());
        } catch (MessagingException e) {
            fail(e.getMessage());
        }

    // 作成したメッセージを返却
        return message;
    }
}