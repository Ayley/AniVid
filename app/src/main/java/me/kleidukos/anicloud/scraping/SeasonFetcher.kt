package me.kleidukos.anicloud.scraping

import android.util.Log
import me.kleidukos.anicloud.models.Episode
import me.kleidukos.anicloud.models.Season
import org.jsoup.Jsoup

class SeasonFetcher {

    companion object {

        fun loadSeasons(seasonNames: Map<String, String>): List<Season> {

            val seasons = mutableListOf<Season>()

            Log.d("Seasons", seasonNames.keys.toString())
            for (season in seasonNames.keys) {
                val episodes = mutableListOf<Episode>()
                val link = seasonNames?.get(season)!!
                Log.d("Stream", "Load Season: $link")

                seasons.add(loadSeason(season, link, seasonNames.keys.toList().indexOf(season)))
            }

            return seasons
        }

        fun loadSeason(name: String, url: String, seasonId: Int): Season {
            Log.d("Seasons", name)
            val episodes = mutableListOf<Episode>()
            Log.d("Stream", "Load Season: $url")

            val docNode = Jsoup.connect(url).get()

            val episodeList = docNode.getElementsByClass("seasonEpisodesList").first().select("tbody").first()

            Log.d("SeasonFetcher", "Load $url Episodes ${episodeList.children().size}")

            for (episode in 1.rangeTo(episodeList.children().size)) {
                val e: Episode = if (url.contains("filme", true)) {
                    EpisodeFetcher.loadEpisode("$url/film-$episode", false)
                } else {
                    EpisodeFetcher.loadEpisode("$url/episode-$episode", false)
                }
                episodes.add(e)
            }
            return Season(name, seasonId, episodes)
        }

        fun loadSeasonWithAccount(name: String, url: String, login: String, seasonId: Int): Season {
            Log.d("Seasons", name)
            val episodes = mutableListOf<Episode>()
            Log.d("Stream", "Load Season: $url")

            val docNode = Jsoup.connect(url).cookie("rememberLogin", login).get()

            val episodeList = docNode.getElementsByClass("seasonEpisodesList").first().select("tbody").select("tr")
Log.d("EpisodeList", episodeList.html())
            Log.d("SeasonFetcher", "Load $url Episodes ${episodeList.first().children().size}")

            for (episode in 1.rangeTo(episodeList.size)) {
                val seenNode = episodeList[episode - 1].attr("class")
                Log.d("Epsisode", "Ep: $episode" + " " + seenNode)
                val seen = if(seenNode.isNotEmpty() || !seenNode.equals("")){
                    seenNode?.equals("seen") ?: false
                }else{
                    false
                }
                val e: Episode = if (url.contains("filme", true)) {
                    EpisodeFetcher.loadEpisode("$url/film-$episode", seen)
                } else {
                    EpisodeFetcher.loadEpisode("$url/episode-$episode", seen)
                }
                Log.d("Episode", e.titleGerman + "  " + e.titleEnglish + " " + episodeList[episode - 1].outerHtml())
                episodes.add(e)
            }
            return Season(name, seasonId, episodes)
        }
    }
}