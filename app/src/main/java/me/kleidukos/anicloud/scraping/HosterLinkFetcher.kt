package me.kleidukos.anicloud.scraping

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class HosterLinkFetcher {

    companion object {
        fun veo(link: String): String = runBlocking {
            Log.d("Hoster", "Load VOE Video: $link")

            val docNode = Jsoup.parse(link)

            val content = docNode.html()

            val regex: Regex = "\"hls\": \"(.*?)\"".toRegex()

            val hls = regex.find(content)?.groups?.get(1)?.value!!

            val conn = URL(hls).openConnection() as HttpURLConnection

            val result = conn.inputStream.bufferedReader().readLines()[2]

            conn.disconnect()

            Log.d("Hoster", "Video Original Url: $result")

            if (result.startsWith("https://delivery")) {
                return@runBlocking URLDecoder.decode(result, "UTF-8")
            }
            return@runBlocking ""
        }

        fun vidoza(link: String): String = runBlocking {
            Log.d("Hoster", "Load VIDOZA Video: $link")

            val docNode = Jsoup.parse(link)

            val videoNode = docNode.getElementById("player_html5_api")

            return@runBlocking videoNode.attr("src")
        }

        fun streamtape(link: String): String = runBlocking {
            Log.d("Hoster", "Load STREAMTAPE Video: $link")

            val docNode = Jsoup.parse(link)

            val videoNode = docNode.getElementById("videolink")

            Log.d("Hoster", videoNode.html())

            val src = videoNode.text().replace("amp;", "")

            return@runBlocking URLDecoder.decode("https:$src")
        }
    }

}