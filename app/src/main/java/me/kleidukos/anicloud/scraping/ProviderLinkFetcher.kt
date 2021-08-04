package me.kleidukos.anicloud.scraping

import org.jsoup.Jsoup
import java.net.URLDecoder

class ProviderLinkFetcher {

    companion object{
        fun voe(html: String): String?{
            val regex = "hls\": \"(.*?)\"".toRegex()

            val match = regex.find(html) ?: return null

            return match?.groups!![1]?.value!!
        }

        fun vidoza(html: String): String?{
            val doc = Jsoup.parse(html)

            val url = doc.getElementById("player_html5_api") ?: return null

            return URLDecoder.decode(url.attr("src"), "UTF-8")
        }

        fun streamtape(html: String): String?{
            val doc = Jsoup.parse(html)

            val url = doc.getElementById("videolink") ?: return null

            return URLDecoder.decode("https:${url.text()}", "UTF-8")
        }

        fun ninjastream(html: String): String?{
            val doc = Jsoup.parse(html)

            val url = doc.selectFirst("video.jw-video").attr("src")

            return url
        }
    }
}