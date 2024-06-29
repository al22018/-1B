/*******************************************************************
*** File Name : UserInfoReg.java
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/06/27
*** Purpose : ログイン時，userID，email，accessTokenのDB登録メソッドを呼び出す
***
*******************************************************************/
/*
*** Revision :
*** V1.0 : 村田悠真, 2024.06.27
*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ログイン時，userID，email，accessTokenのDB登録を行うクラス */
public class UserInfoReg {
	// userIDを格納する 初期値-1
	public static int userID = -1;
	
	/* userID，email，accessTokenのDB登録を行うメソッド */
	public static void userInfoReg(String accessToken) {
		// emailを格納する
		String email = getEmailAddress(accessToken);
		
		//emailでUserID DB問い合わせ
        //DBプログラムが完成後，要更新
		/*
		int userID = selectUserID(email);
		*/
		
		//登録済:userID，accessToken DB更新
        //DBプログラムが完成後，要更新
		/*
		if (userID != 0) {
			updateUserAuthToken(userID, accessToken);
		}
		*/
		
		//未登録:最大のUserID DB問い合わせ
		//UserID割り当て
		//UserID，email，accessToken DB挿入
        //DBプログラムが完成後，要更新
		/*
		else {
			int userID = selectUserIDMax();
			insertUserInfo(userID, email, accessToken);
		} 
		*/
		
		//暫定的な処理
        //DBプログラムが完成後，削除
		userID = 1;
		//ここまで
		
	}
	
	/* GoogleAPIを用いてemailを主臆する関数 */
	private static String getEmailAddress(String accessToken){
		// emailを格納する
		String email = "";
	    try {
	    	// GoogleAPIのURL
	        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
	        url = url + "?access_token=" + URLEncoder.encode(accessToken, "UTF-8");
	
	        /* GooaleAPIから値の取得 */
	        HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
	        conn.setRequestMethod("GET");
	        
	        // レスポンスを格納する
	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	        String line;
	        
	        // レスポンスからemailを取得するためのパターン
	        Pattern emailPattern = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
	        
	        /* レスポンスからemailを取得 */
	        while((line = br.readLine()) != null) {
	            Matcher matcher = emailPattern.matcher(line);
	            if (matcher.find()) {
	                email = matcher.group(1);
	                break;
	            }
	        }
	        br.close();
	        conn.disconnect();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return email;
	}
}
