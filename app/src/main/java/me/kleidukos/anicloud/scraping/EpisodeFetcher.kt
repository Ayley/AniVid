package me.kleidukos.anicloud.scraping

import android.util.Log
import me.kleidukos.anicloud.models.Episode
import org.jsoup.Jsoup

class EpisodeFetcher {

    companion object{
        fun loadEpisode(link: String, seen: Boolean): Episode {
            Log.d("EpisodeFetcher", "Fetch: $link")

            val doc = Jsoup.connect(link)

            val docNode = doc.get()

            val titleGermanNode = docNode.getElementsByClass("episodeGermanTitle").first()

            val titleEnglishNode = docNode.getElementsByClass("episodeEnglishTitle").first()

            var titleGerman: String? = ""

            if(titleGermanNode != null){
                titleGerman = titleGermanNode.text()
            }

            var titleEnglish: String? = ""

            if(titleEnglishNode != null){
                titleEnglish = titleEnglishNode.text()
            }

            val descriptionNode = docNode.getElementsByClass("descriptionSpoiler").first()

            val description = descriptionNode?.text()

            var hosterSiteNode = docNode.getElementsByClass("hosterSiteTitle").first()

            val episodeId = hosterSiteNode.attr("data-episode").toInt()

            val hoster = HosterFetcher.loadHosters(docNode)

            return Episode(titleGerman, titleEnglish, description, seen, episodeId,link, hoster)
        }
    }

}