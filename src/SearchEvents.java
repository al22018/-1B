/*
 * File Nmae			:SearchEvent.java
 * Verision				:Ver1.0
 * Designer				:荻野新
 * 
 * Purpose				:都道府県名とジャンル名からイベントの情報を検索するクラス
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchEvents {
    String url = "https://www.walkerplus.com/event_list/";
    Map<String, String> regionIDMap = new HashMap<>();
    Map<String, String> genreIDMap = new HashMap<>();

    SearchEvents() {
        try (BufferedReader br = new BufferedReader(new FileReader("../datas/regionIDMap.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    regionIDMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader("../datas/genreIDMap.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    genreIDMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ArrayList<Event> search(String region, String genre, String dateTime) {
        String month = dateTime.split("-")[1];
        if (genre.equals("花見") || genre.equals("紅葉")) {
            month = "date" + month + "00";
        }
        String regionID = regionIDMap.get(region);
        String genreID = genreIDMap.get(genre);
        url += month + "/" + regionID + "/" + genreID;
        if (genreID.contains("http")) {
            if (genre.equals("花火")) {
                url = genreID + month + "/" + regionID;
            } else if (genre.equals("花見")) {
                url = genreID + regionID + "/ss0008/" + month;
            } else if (genre.equals("紅葉")) {
                url = genreID + month + "/" + regionID;
            }
        }
        ArrayList<Event> events = new ArrayList<Event>();
        System.out.println(url);
        try {
            // Jsoupを使用して指定されたURLからレスポンスを取得
            Document doc = Jsoup.connect(url).get();

            // <script type="application/ld+json">を含む要素を選択
            Elements scriptElements = doc.select("script[type=application/ld+json]");

            // JSONデータを解析するJacksonのObjectMapperを作成
            ObjectMapper mapper = new ObjectMapper();

            for (Element scriptElement : scriptElements) {
                // スクリプトの内容を取得
                String json = scriptElement.html();

                // JSONデータを解析し、JsonNodeに変換
                JsonNode rootNode = mapper.readTree(json);

                // JsonNodeから必要な情報を抽出
                JsonNode eventNode;
                for (int i = 0; i < rootNode.size(); i++) {
                    eventNode = rootNode.get(i);
                    if (eventNode != null) {
                        Event event = new Event();
                        event.name = eventNode.get("name").asText();
                        event.startDate = eventNode.get("startDate").asText();
                        event.endDate = eventNode.get("endDate").asText();
                        event.location = eventNode.get("location").get("name").asText();
                        event.description = eventNode.get("description").asText();
                        event.eventUrl = eventNode.get("url").asText();
                        events.add(event);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static void main(String[] args) {
        SearchEvents sea = new SearchEvents();
        String region = "東京都";
        String genre = "花火";
        ArrayList<Event> events = sea.search(region, genre);
        if (events.isEmpty()) {
            System.out.println("イベントがありません");
        } else {
            for (Event event : events) {
                System.out.println(event.name);
                System.out.println(event.description);
            }
        }
    }

}
