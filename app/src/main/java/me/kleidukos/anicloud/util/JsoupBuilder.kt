package me.kleidukos.anicloud.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class JsoupBuilder {

    companion object {
        fun getDocument(url: String?): Document? {
            return Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 OPR/77.0.4054.203").ignoreContentType(true).get()
        }
    }
}