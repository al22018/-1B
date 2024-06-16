/******************************************************************* 
***  File Name  : RequestVotingContent.java 
***  Version  : v1.0
***  Designer  : 井上 泰輝 
***  Date   : 2024.06.12 
***  Purpose        : GooglePlacesAPIを呼び出して、データベースから取得した検索条件から作成したクエリ(検索ワード)での検索結果を返す。 
*** 
*******************************************************************/


import java.io.*;
import java.security.*;
import java.sql.*;
import java.util.*;

import com.google.api.client.googleapis.javanet.*;
import com.google.api.client.http.*;
import com.google.api.client.json.*;
import com.google.api.client.json.gson.*;
import com.google.api.client.util.Key;

public class RequestVotingContent {
    private static final String API_KEY = "AIzaSyBPzkG_kAhyOqpXSfnMH-TIbtevcFvtjsk";
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    
    // C8 企画情報管理部に企画ID に対応した企画の投票内容を取得させる.
    public String request(String arg) {
    	String ret = null;
    	try {
    		// PostgreSQLでのデータベースへの接続
				//  接続先とポート: 172.21.40.30:5432
				// seserverのIPアドレス
				//  データベース:	firstdb 
				//  ユーザ:			shibaura
				//  パスワード:		toyosu
			String server = "//172.21.40.30:5432/";
			String database = "firstdb";
			String url = "jdbc:postgresql:" + server + database;
			
			Class.forName("org.postgresql.Driver"); 
			
			Connection con = DriverManager.getConnection(url, "shibaura", "toyosu");
			
			
			Statement stmt= con.createStatement();
			// 検索の実施と結果の格納
			String sql= "SELECT * FROM ProjectsTableNinth";
			ResultSet rs = stmt.executeQuery(sql);
			// ProjectIDでの検索結果(CategoryカテゴリとRegion地域)を
			String projectID = arg;
			while (rs.next()) {
				if (rs.getString("ProjectID").equals(projectID)) {
					break;
				}
				String category = rs.getString("Category"); // カテゴリを取得
				String region = rs.getString("Region"); // 地域を取得
				ret = category + " " +region;
			}
			
			
			// 検索の実施と結果の格納(未完成)
			sql = "SELECT * FROM UserAndProjectDetailsTableNinth";
			rs = stmt.executeQuery(sql);
			// 検索結果(投票内容VoteContent)を取得
			while (rs.next()) {
				if (rs.getString("ProjectID").equals(projectID)) {
					String VoteContent = rs.getString("VoteContent"); // 投票内容を取得
					String[] splitted = VoteContent.split("|");
					int length = splitted.length;
				}
			}
			
			// DBとの接続を終了
			stmt.close();
			con.close();
		}catch (Exception e) {
		e.printStackTrace();
		}
    	return ret;
    }
    
    
    // 投票結果をもとに検索する. 
    public List<String[]> search(String args) throws GeneralSecurityException, IOException {
    	
    	// HTTP通信
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        // JSONを扱うJsonFactoryオブジェクトを生成, 初期化
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(jsonFactory));
            }
        });

        // クエリ用String型変数queryに引数を代入
        String query = args;
        String url = PLACES_SEARCH_URL + "?query=" + query + "&key=" + API_KEY;

        // URLを用いてリクエストを送りレスポンスを受け取る
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        PlacesSearchResponse response = request.execute().parseAs(PlacesSearchResponse.class);

        // 	検索結果格納用リスト(戻り値)
        List<String[]> list = new ArrayList<String[]>();
        
        // 検索に失敗したかどうか判定する
        if (response.results != null) {
        	// 検索結果が格納されているPlaceオブジェクトのリストresponse.resultsから, 
        	// 店(施設)名,住所,評価値をString配列のリストに格納する.
            for (Place result : response.results) {
            	String[] array = {result.name, result.formatted_address, "" + result.rating};
                list.add(array);
            }
            // 検索に成功した場合検索結果を格納したリストを返す。
            return list;
        } else {
        	// 検索に失敗した場合nullを返す。
        	return null;
        }
    }

    
    // レスポンスを格納するクラス
    public static class PlacesSearchResponse {
        @Key
        public List<Place> results;
    }

    
    // レスポンスの形式用クラス
    public static class Place {
        @Key
        public String name;

        @Key
        public String formatted_address;

        @Key
        public float rating;
    }
}
