import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * File Name: EventRegister.java
 * Version: V1.0
 * Designer: 新保陽己
 * Date: 06/10
 * Purpose: UIサーバよりデータを受け取り、C8 企画情報管理部へ送信する。
 *          通信失敗時には、通信エラーが起きたことを報告し、W2ホーム画面に戻る。
 */
public class EventRegister {
    private static final String UI_SERVER_URL = "";

    public static void sendData(userID) {
        Map<String, String> receivedData = receiveDataFromUIServer();
        receivedData.put("managerID", userID);

        if (receivedData != null) {
            //C8企画情報管理部へ送信
        } else {
            System.out.println("通信エラーが発生しました");
            // W2ホーム画面に戻る処理を追加する
        }
    }

    /**
     * UIサーバからデータを受信するメソッド
     * @return 受信したデータのマップ
     */
    private static Map<String, String> receiveDataFromUIServer() {
        try {
            URL url = new URL("UI_SERVR_URL");　//サーバーのURlを入れる
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


            return parseResponse(response.toString());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * UIサーバからのレスポンスをパースするメソッド
     * @param response レスポンスの文字列
     * @return パースされたデータのマップ
     */
    private static Map<String, String> parseResponse(String response) {
    Map<String, String> parsedData = new HashMap<>();

    // レスポンスからカテゴリと地域を解析して取得
    String[] parts = response.split(","); // カンマで分割
    if (parts.length >= 3) {
        String category = parts[0].trim(); // カテゴリ
        String region = parts[1].trim(); // 地域
        String start_time = parts[2].trim(); //開始時間
        parsedData.put("category", category);
        parsedData.put("region", region);
        parsedData.put("start_time", start_time);
    }
    

    return parsedData;
    }
}
