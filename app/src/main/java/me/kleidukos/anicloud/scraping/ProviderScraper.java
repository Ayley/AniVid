package me.kleidukos.anicloud.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kleidukos.anicloud.models.anicloud.Episode;
import me.kleidukos.anicloud.models.anicloud.Language;
import me.kleidukos.anicloud.models.anicloud.Provider;
import me.kleidukos.anicloud.util.JsoupBuilder;

public class ProviderScraper {
    public static Map<Language, List<Provider>> scrapProviders(Episode episode) {
        Document document = JsoupBuilder.Companion.getDocument("https://anicloud.io" + episode.getEpisode_url(), null);

        Elements elements = document.selectFirst("div.hosterSiteVideo > ul").select("li");

        Map<Language, List<Provider>> languageListMap = new HashMap<>();

        for (Element element : elements){
            Language language = Language.Companion.getById(Integer.parseInt(element.attr("data-lang-key")));

            if(!languageListMap.containsKey(language)){
                languageListMap.put(language, new ArrayList<>());
            }

            String name = element.selectFirst("h4").text();
            int redirectId = Integer.parseInt(element.attr("data-link-id"));

            Provider provider = new Provider(name, redirectId);

            languageListMap.get(language).add(provider);

        }

        return languageListMap;
    }
}
