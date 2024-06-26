import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/*
 * File Name	:UserInfo.java
 * Version		:Ver1.1
 * Designer		:荻野新
 * Date			:2024.06.16
 * Purpose		:ユーザの情報を扱うためのクラス
 * 
 *

 * Revision :
 * V1.0 : 荻野新, 2024.06.16
 * V1.1 : 相内優真, 2024.06.26 updateUserInfo, getUserInfoを追加
*/

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
	
	//UserID、更新するフィールド、更新する値を渡すとテーブルを更新するメソッド
	public void updateUserInfo(int userID, String updatefield, String value) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "UPDATE UserTableNinth SET " + updatefield + "=? WHERE UserID=" + userID;
            PreparedStatement prestmt = con.prepareStatement(sql);

            prestmt.setString(1, value);

            prestmt.executeUpdate();
            prestmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	//引数としてgetinfo(知りたいフィールド名), knowninfo(既知のフィールド名), knownvalue(既知のフィールドの値)を与えると、知りたいフィールドの値をString型で返すメソッド
	//※注意　・引数はSQLのフィールド名を正式名称で入力してください
	//　　　　・UserIDもString型で返します
	//　　　　・knowninfoは個人が特定できる情報にしてください(表示名などは重複可なのでNG)
	public String getUserInfo(String getinfo, String knowninfo, String knownvalue) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "SELECT " + getinfo + "FROM UserTableNinth WHERE " + knowninfo + "=" + knownvalue;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            String result;
            if(getinfo.equals("UserID")) {
            	result = Integer.toString(rs.getInt(getinfo));
            }else {
            	result = rs.getString(getinfo);
            }
            
            stmt.close();
            con.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
