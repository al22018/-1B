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
	public int userID;
	public String name = "";
	public String authToken = "";
	public String email = "";
	public String calendarInfo = "";
	
	//DB接続のためのアドレスなど
	String server = "//172.18.80.1:5432/"; // seserverのIPアドレス
    String dataBase = "test1";
    String user = "oops";
    String passWord = "pass";
    String url = "jdbc:postgresql:" + server + dataBase;

	//企画IDから、参加者のメールアドレスを取得してtoリストに保存するメソッド
	public ArrayList<String> getEmails(int projectID) {
		
		ArrayList<String> to = new ArrayList<String>();		
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
	
	//UserInfoクラスのフィールドに格納されたデータをデータベースに登録するメソッド
	public void setUserInfo() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager.getConnection(url, user, passWord);

			String sql = "INSERT INTO UsersTableNinth VALUES (?, ?, ?, ?, ?)";
			PreparedStatement prestmt = con.prepareStatement(sql);

			prestmt.setInt(1, userID);
			prestmt.setString(2, name);
			prestmt.setString(3, authToken);
			prestmt.setString(4,  email);
			prestmt.setString(5,  calendarInfo);

			prestmt.executeUpdate();
			prestmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//UserID、更新するフィールド、更新する値を渡すとテーブルを更新するメソッド
	public void updateUserInfo(int userID, String updateField, String value) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "UPDATE UsersTableNinth SET " + updateField + "=? WHERE UserID=" + userID;
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
	//※注意　・引数はSQLのフィールド名を入力してください
	//　　　　・UserIDもString型で返します
	//　　　　・knowninfoは個人が特定できる情報にしてください(表示名などは重複可なのでNG)
	public String getUserInfo(String getInfo, String knownField, String knownValue) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "SELECT " + getInfo + " FROM UsersTableNinth WHERE " + knownField + "='" + knownValue + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            rs.next();
            String result;
            if(getInfo.equals("UserID")) {
            	result = Integer.toString(rs.getInt(getInfo));
            }else {
            	result = rs.getString(getInfo);
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

	