/*******************************************************************
*** File Name : GetNickName.java
*** Version : V1.0
*** Designer : 村田 悠真
*** Date : 2024/06/27
*** Purpose : nickNameをGetリクエストから受信，ホーム画面に遷移する
***
*******************************************************************/
/*
*** Revision :
*** V1.0 : 村田悠真, 2024.06.27
*/


import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* nickNameを取得，DB登録，ホーム画面への遷移を行うクラス */
public class GetNickName extends HttpServlet {

	/* nickNameを含んだGetリクエストを受信するメソッド */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        request.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");

    	// Getリクエストから取得したnickName
    	String nickName = request.getParameter("nickName");
    	
    	//セッションからuserID取得
        HttpSession session = request.getSession();
        int userID = (int)session.getAttribute("userID");
        
        //userIDでNickName更新
        //DBプログラムが完成後，要更新
        /*
        String nickName = updateNickName(userID, nickName);
        */
        
        // 暫定的な処理
        // ホーム画面完成後，要更新
        String homeUrl = "/ProjectManage/homeLoginTest.html";
        homeUrl = homeUrl + "?userID=" + userID;
        homeUrl = homeUrl + "&nickName=" + URLEncoder.encode(nickName, "UTF-8");
        //ここまで
        
        response.sendRedirect(homeUrl);
    }
}