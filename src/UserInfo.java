package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	String server = "//172.21.40.30:5432/"; 
	String dataBase = "firstdb";
	String user = "shibaura";
	String passWord = "toyosu";
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
	
	//未投票のユーザと投票済みのユーザのリストのリストを返すメソッド
	public List<List<String>> getVoterList(int projectID){
		List<String> yet = new ArrayList<String>();
        List<String> done = new ArrayList<String>();
        
        try {
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager.getConnection(url, user, passWord);
	        Statement stmt = con.createStatement();
	        
	       
	        String sql1 = "SELECT a.Displayname FROM UsersTableNinth a JOIN UserAndProjectsDetailsTableNinth b ON a.UserID = b.UserID WHERE b.genre IS NULL AND b.ProjectID = " + projectID;
	        String sql2 = "SELECT a.Displayname FROM UsersTableNinth a JOIN UserAndProjectsDetailsTableNinth b ON a.UserID = b.UserID WHERE b.genre IS NOT NULL AND b.ProjectID = " + projectID;
	        ResultSet rs1 = stmt.executeQuery(sql1);
	        ResultSet rs2 = stmt.executeQuery(sql2);
	        
	        while(rs1.next() || rs2.next()) {
	        	if(rs1.getString("Displayname") == null) {
	        		yet.add("");
	        	}else {
	        		yet.add(rs1.getString("Displayname"));
	        	}
	        	if(rs2.getString("Displayname") == null) {
	        		done.add("");
	        	}else {
	        		done.add(rs2.getString("Displayname"));
	        	}
			}
	        
	        List<List<String>> voterList = new ArrayList<>();
	        voterList.add(yet);
	        voterList.add(done);
			
			stmt.close();
			con.close();
			return voterList;
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
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
	public void updateUserInfo(int userID, String updatefield, String value) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "UPDATE UsersTableNinth SET " + updatefield + "=? WHERE UserID=" + userID;
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
	public String getUserInfo(String getinfo, String knowninfo, String knownvalue) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "SELECT " + getinfo + " FROM UsersTableNinth WHERE " + knowninfo + "='" + knownvalue + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            rs.next();
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
	
	//userIDの最大値をint型で返すメソッドです
	public int getMaxID() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "SELECT MAX(userID) FROM UsersTableNinth";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            rs.next();
            int result = rs.getInt(1);
         
            stmt.close();
            con.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

	