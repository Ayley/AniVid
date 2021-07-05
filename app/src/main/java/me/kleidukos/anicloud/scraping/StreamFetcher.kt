package me.kleidukos.anicloud.scraping

import android.util.Log
import me.kleidukos.anicloud.models.Stream
import org.jsoup.Jsoup

class StreamFetcher {

    companion object{
        fun loadStream(url: String, title: String, thumbnail: String): Stream{

            Log.d("Stream", "Load: $url")

            val doc = Jsoup.connect(url).timeout(5000)

            val docNode = doc.get()

            val addSeriesNode = docNode.getElementsByClass("add-series").first()

            val trailerUrl = ""

            val descriptionNode = docNode.getElementsByClass("seri_des").first()

            val description = descriptionNode.attr("data-full-description")

            val seasonList = docNode.getElementById("stream").select("ul").first().children().select("a")

            val seasonNames = mutableMapOf<String, String>()

            for (element in seasonList){
                val seasonName = element.attr("title")
                val href = element.attr("href")
                val url = "https://anicloud.io$href";
                seasonNames.put(seasonName, url)
            }

            return Stream(title, description, thumbnail, trailerUrl, seasonNames)
        }
    }

}