/*
 * File Nmae			:GetWeather.java
 * Verision				:Ver1.0
 * Designer				:荻野新
 * 
 * Purpose				:都道府県名から天気を検索するクラス
 * 
 */

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.HttpURLConnection;
 import java.net.URL;
 
 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.w3c.dom.NodeList;
 
 public class GetWeather {
 
     public GetWeather() {// コンストラクタ
     }
 
     // 天気予報APIを利用して指定した都市の天気予報を得るメソッド
     public static WeatherOfPrefecture getForecast(String city) {
         String response = null; // Webサーバからの応答
 
         try {
             WeatherOfPrefecture res = getCityIDs(city);
             for (int j = 0; j < res.cityIDs.size(); j++) {
                 // System.out.println(res.areas.length);
             }
             int numOfAreas = res.cityIDs.size();
             // System.out.println("numOfAreas: " + numOfAreas);
             res.pref = city;
 
             for (int i = 0; i < res.cityIDs.size(); i++) {
                 String cityID = res.cityIDs.get(i);
                 try {
                     // Web APIのリクエストURLを構築する
                     String url = "http://weather.tsukumijima.net/api/forecast/city/" + cityID;
 
                     // HTTP接続を確立し，処理要求を送る
                     HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
                     conn.setRequestMethod("GET"); // GETメソッド
                     conn.setRequestProperty("User-Agent", "");
 
                     // Webサーバからの応答を受け取る
                     BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                     response = "";
                     String line;
                     while ((line = br.readLine()) != null) {
                         response += line;
                     }
                     br.close();
                     conn.disconnect();
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
                 // telopキーの次の文字列を取得して返す
                 String forecast = ""; // 返り値
                 if (response != null && response.length() > 0) {
                     int offset = response.indexOf("telop") + 7; // telopキーの次の位置
                     offset = response.indexOf("\"", offset) + 1; // 天気予報文字列の先頭（文字列を囲っている " の次）の位置
                     int end = response.indexOf("\"", offset); // 天気予報文字列の最後の " の位置
                     forecast = response.substring(offset, end);
                 }
                 res.weatherOfArea.add(forecast);
             }
             return res;
         } catch (Exception e) {
             e.printStackTrace();
         }
         return null;
     }
 
     // 都道府県名からCityIDを取得するメソッド
     public static WeatherOfPrefecture getCityIDs(String City) throws Exception {
 
         WeatherOfPrefecture res = new WeatherOfPrefecture();
 
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse("../datas/prefectureCode.xml");
         Element channel = document.getDocumentElement();
         NodeList prefectures = document.getElementsByTagName("pref");
         for (int i = 0; i < prefectures.getLength(); i++) {
             Element prefecture = (Element) prefectures.item(i);
             if (prefecture.getAttribute("title").equals(City)) {
                 NodeList cities = prefecture.getElementsByTagName("city");
                 for (int j = 0; j < cities.getLength(); j++) {
                     Element city = (Element) cities.item(j);
                     res.areas.add(city.getAttribute("title"));
                     res.cityIDs.add(city.getAttribute("id"));
                 }
                 break;
             }
         }
         return res;
     }
 
     // テスト用mainメソッド
     public static void main(String[] args) {
         GetWeather instance = new GetWeather();
         try {
             // ArrayList<String> cityIDs = getCityIDs("東京都");
             WeatherOfPrefecture res = getForecast("埼玉県");
             System.out.println(res.pref);
             for (int i = 0; i < res.areas.size() - 1; i++) {
                 // res.weatherOfArea.add(instance.getForecast(res.cityIDs.get(i)));
                 System.out.println(res.areas.get(i) + " : " + res.weatherOfArea.get(i));
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 
 }
 