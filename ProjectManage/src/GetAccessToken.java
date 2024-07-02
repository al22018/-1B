/*******************************************************************
*** File Name : GetAccessToken.java
*** Version : V1.1
*** Designer : 村田 悠真
*** Date : 2024/07/02
*** Purpose : accessTokenをGetリクエストから受信，nickName登録画面に遷移する
***
*******************************************************************/
/*
*** Revision :
*** V1.0 : 村田悠真, 2024.06.27
*** V1.1 : 村田悠真, 2024.07.02 doGet
*/


import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* accessTokenを取得，DB登録，nickName登録画面への遷移を行うクラス */
public class GetAccessToken extends HttpServlet {

	/* accessTokenを含んだGetリクエストを受信するメソッド */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
    	
        request.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");
    	
    	// Getリクエストから取得したaccessToken
        String accessToken = request.getParameter("accessToken");
        
        // From. Changed 村田悠真 2024/07/02
        try {
        /* ユーザID，email，accessTokenをDBに登録する */
        UserInfoReg.userInfoReg(accessToken);
        
        /* 今後のDBアクセスで利用するため，userIDをセッションに格納 */
        HttpSession session = request.getSession();
        session.setAttribute("userID", UserInfoReg.userID);
        
        /* nickName登録ページに遷移する処理 */
        // userIDでNickName問い合わせ
        // DBプログラムが完成後，要更新
        /*
        // DBに登録済のニックネーム　未登録の場合は""を想定
        String nickName = selectNickName(userID);
        */
        }
        /* DBアクセスが失敗した時の例外処理 */
        catch (Exception e) {

            // エラー画面のURL
            String errorUrl = "/ProjectManage/loginError.html";
            response.sendRedirect(errorUrl);
        }
        // To. Changed 村田悠真 2024/07/02
        
		// 暫定的な処理
        // DBプログラムが完成後，削除
        String nickName = "田中";
        // ここまで
        
        // nickName登録画面のURL
        String nickNameUrl = "/ProjectManage/nickName.html";
        /* 登録済の場合URLにnickNameパラメータを加えて送信 */
        if (nickName != "") {
        	nickNameUrl = nickNameUrl + "?nickName=" + URLEncoder.encode(nickName, "UTF-8");
        }
        
        response.sendRedirect(nickNameUrl);
        
    }
}