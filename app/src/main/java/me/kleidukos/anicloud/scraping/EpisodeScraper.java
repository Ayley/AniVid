package me.kleidukos.anicloud.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.kleidukos.anicloud.models.anicloud.Episode;
import me.kleidukos.anicloud.models.anicloud.Language;
import me.kleidukos.anicloud.models.anicloud.SimpleStream;
import me.kleidukos.anicloud.room.series.RoomDisplayStream;
import me.kleidukos.anicloud.tmdb.TMDB;
import me.kleidukos.anicloud.util.JsoupBuilder;

import java.util.ArrayList;
import java.util.List;

public class EpisodeScraper {

    public static List<Episode> scrapEpisodes(String url, SimpleStream stream, int season) {

        Document document = JsoupBuilder.Companion.getDocument(buildSeasonUrl(url, season));

        Elements elements = document.selectFirst("tbody#season" + season).select("tr");

        List<Episode> episodes = new ArrayList<>();

        List<me.kleidukos.anicloud.tmdb.models.Episode> episodeList = TMDB.getEpisodes(stream.getTitle(), stream.getAltTitle(), stream.getYear(), season);

        for (Element element : elements){
            int episode = Integer.parseInt(element.attr("data-episode-season-id"));

            String title_english = element.selectFirst("span").text();

            String title_german = element.selectFirst("strong") != null ? element.selectFirst("strong").text() : null;

            String episodeUrl = element.selectFirst("a").attr("href");

            String description = null;

            String poster = null;

            if(episodeList != null){
                description = episodeList.stream().filter(it -> it.getEpisode_number() == episode).findFirst().get().getOverview();
                poster = episodeList.stream().filter(it -> it.getEpisode_number() == episode).findFirst().get().getPoster();
            }

            if(description == null || description.isEmpty()){
                description = null;
            }

            episodes.add(new Episode(season, episode, title_english, title_german, episodeUrl, description, poster, getLanguages(element), false));
        }
        return episodes;
    }

    private static String buildSeasonUrl(String url, int season){
        if(season == 0){
            return url + "/filme";
        }else {
            return url + ("/staffel-" + season);
        }
    }

    private static List<Language> getLanguages(Element element){
        List<Language> languages = new ArrayList<>();

        for(Element img : element.selectFirst("td.editFunctions").select("img")){
            languages.add(Language.Companion.getBySrc(img.attr("src")));
        }

        return languages;
    }
}
