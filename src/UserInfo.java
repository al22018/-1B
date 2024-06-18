import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/*
 * File Name	:UserInfo.java
 * Version		:Ver1.0
 * Designer		:荻野新
 * Date			:2024.06.16
 * Purpose		:企画の情報を扱うためのクラス
 * 
 */

//Emailに対応するID、ニックネームを取得したい
//IDに対応するcalndarInfoを取得

//updateも実装(UID, 変更箇所を指定してアップデート)

//UserProjectsTable
//ユーザ企画テーブルー＞アップデートメソッドを用意
public class UserInfo {
	int UserID;
	String Name = "";
	String AuthToken = "";
	String Email = "";
	String calendarInfo = "";
	
	//DB接続のためのアドレスなど
	String server = "//172.21.40.30:5432/"; // seserverのIPアドレス
	String dataBase = "firstdb";
	String user = "shibaura";
	String passWord = "toyosu";
	String url = "jdbc:postgresql:" + server + dataBase;

	//企画IDから、参加者のメールアドレスを取得してtoリストに保存するメソッド
	public ArrayList<String> getEmails(int projectID) {
		
		ArrayList<String> to = new ArrayList<String>();
		String server = "//172.21.40.30:5432/"; // seserverのIPアドレス
		String dataBase = "firstdb";
		String user = "shibaura";
		String passWord = "toyosu";
		String url = "jdbc:postgresql:" + server + dataBase;
		
		try {
			Class.forName("org.postgresql.Driver");
			
			Connection con = DriverManager.getConnection(url, user, passWord);
			Statement stmt = con.createStatement();
			//検索の実施と結果の格納
			String sql = "SELECT a.Email FROM UsersTableNinth a JOIN UserAndProjectsDetailsTableNinth b ON a.UserID = b.UserID WHERE b.ProjectID = " + projectID;
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String email = rs.getString("Email");
				to.add(email);
			}
			
			stmt.close();
			con.close();
			
			return to;
		}catch(Exception e){
			e.printStackTrace();
			return to;
		}
	}
	
	public void setUserInfo(UserInfo userInfo) {
		try {
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager.getConnection(url, user, passWord);

			String sql = "INSERT INTO UsersTableNinth VALUES (?, ?, ?, ?, ?)";
			PreparedStatement prestmt = con.prepareStatement(sql);

			prestmt.setInt(1, userInfo.UserID);
			prestmt.setString(2, userInfo.Name);
			prestmt.setString(3, userInfo.AuthToken);
			prestmt.setString(4,  userInfo.Email);
			prestmt.setString(5,  userInfo.calendarInfo);

			prestmt.executeUpdate();
			prestmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
