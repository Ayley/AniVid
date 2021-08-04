package me.kleidukos.anicloud.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.kleidukos.anicloud.models.anicloud.Genre;
import me.kleidukos.anicloud.models.anicloud.SimpleStream;
import me.kleidukos.anicloud.tmdb.TMDB;
import me.kleidukos.anicloud.tmdb.models.Result;
import me.kleidukos.anicloud.util.JsoupBuilder;

public class AnimeScraper {

    public static List<SimpleStream> scrapAll(){

        Document document = JsoupBuilder.Companion.getDocument("https://anicloud.io/animes", null);

        if(document == null){
            return null;
        }

        List<Element> streamElements = document.selectFirst("div#seriesContainer").select("li");

        List<SimpleStream> simpleStreams = new ArrayList<>();

        for (Element element : streamElements){
            Element a = element.selectFirst("a");

            String name = a.selectFirst("a").text();

            System.out.println(name);

            String url = "https://anicloud.io" + a.attr("href");

            String year = "";

            String altName = !a.attr("data-alternative-title").isEmpty() ? a.attr("data-alternative-title").split(",")[0] : "";


            simpleStreams.add(new SimpleStream(name, altName, url, "", "", "", year, null, null));
        }

        return simpleStreams;
    }
}
