package me.kleidukos.anicloud.scraping

import me.kleidukos.anicloud.enums.Language
import me.kleidukos.anicloud.models.Hoster
import org.jsoup.nodes.Document

class HosterFetcher {

    companion object{
        fun loadHosters(docNode: Document): List<Hoster>{

            val hosterSiteNode = docNode.getElementsByClass("hosterSiteVideo").first()

            val hosterNodes = hosterSiteNode.select("ul").first().children()

            val data = mutableMapOf<String, MutableMap<Language, Int>>()

            val hoster = mutableListOf<Hoster>()

            for (node in hosterNodes){
                val name = node.select("h4").text()

                if(name.equals("PlayTube", true)){
                    continue
                }

                val redirectId = node.attr("data-link-id").toInt()
                val languageId = node.attr("data-lang-key").toInt()
                val language = Language.getById(languageId)
                if(data.containsKey(name)){
                    data?.get(name)?.put(language, redirectId)
                }else{
                    val map = mutableMapOf<Language, Int>()
                    map.put(language, redirectId)

                    data.put(name, map)
                }
            }

            for (name in data.keys){
                val map = data.get(name)

                hoster.add(Hoster(name, map as HashMap<Language, Int>))
            }

            return hoster
        }
    }

}