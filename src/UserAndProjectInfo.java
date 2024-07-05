import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * File Name	:UserAndProjectInfo.java
 * Version		:Ver1.0
 * Designer		:相内 優真
 * Date			:2024.06.18
 * Purpose		:企画とユーザーの情報を扱うためのクラス
 * 
 *
 * 
 */

public class UserAndProjectInfo {
    public int projectID;
    public int userID;
    public String genre = "";
    public String budget1 = "";
    public String budget2 = "";
    public String review = "";

    // DB接続のためのアドレスなど
    String server = "//172.18.80.1:5432/"; // seserverのIPアドレス
    String dataBase = "test1";
    String user = "oops";
    String passWord = "pass";
    String url = "jdbc:postgresql:" + server + dataBase;

    // 企画IDとユーザIDから投票情報を取得、UserAndProjectInfoクラスのオブジェクトに保存するメソッド
    public UserAndProjectInfo getVoteInfo(int projectID, int userID) {

        // DB接続
        try {
            UserAndProjectInfo ret = new UserAndProjectInfo();

            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(url, user, passWord);
            Statement stmt = con.createStatement();
            // 検索の実施と結果の格納
            String sql = "SELECT * FROM UserAndProjectsDetailsTableNinth WHERE ProjectID=" + projectID + " AND UserID="
                    + userID;
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            ret.genre = rs.getString("Genre");
            ret.budget1 = rs.getString("Budget1");
            ret.budget2 = rs.getString("Budget2");
            ret.review = rs.getString("Review");

            stmt.close();
            con.close();

            return ret;
        } catch (Exception e) {
            UserAndProjectInfo ret = new UserAndProjectInfo();
            e.printStackTrace();
            return ret;
        }
    }

    // useIDとprojectIDを紐づけてデータベースに保存するメソッド
    public void setUserAndProjectInfo() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "INSERT INTO UserAndProjectsDetailsTableNinth VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement prestmt = con.prepareStatement(sql);

            prestmt.setInt(1, userID);
            prestmt.setInt(2, projectID);
            prestmt.setString(3, null);
            prestmt.setString(4, null);
            prestmt.setString(5, null);
            prestmt.setString(6, null);

            prestmt.executeUpdate();
            prestmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // userIDとprojectIDから投票データを更新するメソッド
    public void updateVoteInfo(int userID, int projectID) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "UPDATE UserAndProjectsDetailsTableNinth SET Genre=?, Budget1=?, Budget2=?, review=? WHERE UserID="
                    + userID + " AND projectID=" + projectID;
            PreparedStatement prestmt = con.prepareStatement(sql);

            prestmt.setString(1, genre);
            prestmt.setString(2, budget1);
            prestmt.setString(3, budget2);
            prestmt.setString(4, review);

            prestmt.executeUpdate();
            prestmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
