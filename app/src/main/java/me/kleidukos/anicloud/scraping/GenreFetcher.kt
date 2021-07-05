package me.kleidukos.anicloud.scraping

import android.util.Log
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.models.DisplayStream
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class GenreFetcher {

    companion object {

        private val baseUrl = "https://anicloud.io"

        fun loadGenre(genre: Genre): List<DisplayStream> {

            Log.d("Home", "Load genre: ${genre.genreName} url:" + baseUrl + "/" + genre.url)

            val doc = Jsoup.connect(baseUrl + "/" + genre.url)

            val docNode = doc.get()

            val streams = docNode.getElementsByClass("seriesListContainer row").first().children()

            val displaySeasons = mutableListOf<DisplayStream>()

            for (stream in streams) {
                val streamName = stream.select("h3").text()
                val streamUrl = baseUrl + stream.select("a").attr("href")
                val streamImgUrl = baseUrl + stream?.select("img")?.attr("data-src")!!

                displaySeasons.add(DisplayStream(streamName, streamImgUrl, streamUrl))
            }

            return displaySeasons
        }

        fun loadGenreWithPages(genre: Genre): List<DisplayStream> {
            try {
                val url = baseUrl + "/" + genre.url

                Log.d("Home", "Load genre with pages: ${genre.genreName} url: $url")

                val doc = Jsoup.connect(url).timeout(5000)

                val docNode = doc.get()

                val displaySeasons = mutableListOf<DisplayStream>()

                for (page in 1.rangeTo(pages(docNode))) {

                    var pageNode: Document = if (page == 1) {
                        Jsoup.connect("$url").get()
                    } else {
                        Jsoup.connect("$url/$page").get()
                    }

                    val streams =
                        pageNode.getElementsByClass("seriesListContainer row").first().children()

                    for (stream in streams) {
                        val container: Elements = stream.children()
                        val streamName = container.select("h3").text()
                        val streamUrl = baseUrl + stream.select("a").attr("href")
                        val streamImgUrl = baseUrl + stream.child(0).child(0).attr("data-src")

                        Log.d("Stream", streamName + "  " + streamUrl)

                        displaySeasons.add(DisplayStream(streamName, streamImgUrl, streamUrl))
                    }
                }

                Log.d("Home", "Genre: ${genre.genreName} hat ${displaySeasons.size - 1} Streams")

                return displaySeasons
            } catch (e: java.lang.Exception) {
                return null!!
            }
        }

        private fun pages(doc: Document): Int {
            val pagination =
                doc.getElementsByClass("hosterSiteDirectNav pagination").first() ?: return 1

            if (pagination.child(0).children().size == 0) {
                return 1
            }

            return try {
                pagination.child(0).children().size - 1
            } catch (e: Exception) {
                1
            }
        }
    }

}