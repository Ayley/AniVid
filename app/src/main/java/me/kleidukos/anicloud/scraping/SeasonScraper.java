package me.kleidukos.anicloud.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kleidukos.anicloud.models.anicloud.Season;
import me.kleidukos.anicloud.util.JsoupBuilder;

public class SeasonScraper {

    public static List<Season> scrapSeasons(String url) {

        Document document = JsoupBuilder.Companion.getDocument(url);

        if(document == null){
            return null;
        }

        List<Season> seasons = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : getSeasonIds(document).entrySet()){
            seasons.add(new Season(entry.getKey(), entry.getValue()));
        }

        return seasons;
    }

    private static Map<Integer, String> getSeasonIds(Document document){
        Elements elements = document.selectFirst("div#stream > ul").select("li");

        Map<Integer, String> ids = new HashMap<>();

        for (Element element : elements){
            if(element.text().equals("Staffeln:")){
                continue;
            }
            if(element.text().equals("Filme")){
                ids.put(0, "Filme");
                continue;
            }
            int id = Integer.parseInt(element.text());

            ids.put(id, "Staffel " + id);
        }

        return ids;
    }
}
