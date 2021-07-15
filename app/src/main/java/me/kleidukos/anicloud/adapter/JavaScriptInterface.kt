package me.kleidukos.anicloud.adapter

import android.util.Log
import android.webkit.JavascriptInterface
import me.kleidukos.anicloud.scraping.parsing.ProviderLinkFetcher
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer

class JavaScriptInterface(val hoster: String, val streamPlayer: StreamPlayer) {

    var loaded = false

    @JavascriptInterface
    fun processHTML(html: String?) {
        Log.d("MYHTML", html!!)

        when (hoster) {

            "VOE" -> {
                var src = ProviderLinkFetcher.voe(html) ?: ""
                Log.d("MYSRC", src)
                    if (!loaded) {
                        loaded = true
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src, true)
                        }
                    }
            }
            "Streamtape" -> {
                var src = ProviderLinkFetcher.streamtape(html) ?: ""
                Log.d("MYSRC", src)
                    if (!loaded) {
                        loaded = true
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src, true)
                        }
                    }
            }
            "Vidoza" -> {
                var src = ProviderLinkFetcher.vidoza(html) ?: ""
                Log.d("MYSRC", src)
                    if (!loaded) {
                        loaded = true
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src, true)
                        }
                    }
            }
            "PlayTube" -> {
            }
        }
    }
}