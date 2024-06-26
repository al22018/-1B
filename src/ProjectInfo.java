import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

/*
 * File Name	:ProjectInfo.java
 * Version		:Ver1.0
 * Designer		:荻野新
 * Date			:2024.06.16
 * Purpose		:企画の情報を扱うためのクラス
 * 
 * set
 * ProjectInfo project = new ProjectInfo();
 * project.projectID = ....
 * .
 * .
 * .
 * .
 * .
 * project.setProjectInfo();
 * 
 * 
 * get
 * ProjectInfo cons = new ProjectInfo();
 * ProjectInfo project = cons.getProjectInfo(projectID);
 * 
 */

public class ProjectInfo {
    int projectID;
    String projectName = "";
    Timestamp dateTime;
    String category = "";
    String destination = "";
    int managerID;
    String region = "";
    String leader = "";// 幹事のユーザネーム

    // DB接続のためのアドレスなど
    String server = "//172.21.40.30:5432/"; // seserverのIPアドレス
    String dataBase = "firstdb";
    String user = "shibaura";
    String passWord = "toyosu";
    String url = "jdbc:postgresql:" + server + dataBase;

    // 企画IDから企画情報を取得、projectinfoクラスのフィールドに保存するメソッド
    public ProjectInfo getProjectInfo(int projectID) {

        // DB接続
        try {
            ProjectInfo ret = new ProjectInfo();

            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(url, user, passWord);
            Statement stmt = con.createStatement();
            // 検索の実施と結果の格納
            String sql = "SELECT * FROM ProjectsTableNinth WHERE ProjectID = " + projectID;
            ResultSet rs = stmt.executeQuery(sql);

            ret.projectName = rs.getString("Name");
            ret.destination = rs.getString("Destination");
            ret.region = rs.getString("Region");
            ret.dateTime = rs.getTimestamp("DateTime");

            stmt.close();
            con.close();

            return ret;
        } catch (Exception e) {
            ProjectInfo ret = new ProjectInfo();
            e.printStackTrace();
            return ret;
        }
    }

    public void setProjectInfo() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, passWord);

            String sql = "INSERT INTO ProjectsTableNinth VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement prestmt = con.prepareStatement(sql);

            prestmt.setInt(1, projectID);
            prestmt.setString(2, projectName);
            prestmt.setTimestamp(3, dateTime);
            prestmt.setString(4, category);
            prestmt.setString(5, destination);
            prestmt.setInt(6, managerID);
            prestmt.setString(7, region);

            prestmt.executeUpdate();
            prestmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
