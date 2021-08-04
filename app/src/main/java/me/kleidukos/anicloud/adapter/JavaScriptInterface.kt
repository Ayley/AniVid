package me.kleidukos.anicloud.adapter

import android.util.Log
import android.webkit.JavascriptInterface
import me.kleidukos.anicloud.scraping.ProviderLinkFetcher
import me.kleidukos.anicloud.ui.videoplayer.StreamVideoPlayer

class JavaScriptInterface(val hoster: String, val streamVideoPlayer: StreamVideoPlayer) {

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
                        streamVideoPlayer.runOnUiThread {
                            streamVideoPlayer.loadVideo(src, 0, true)
                        }
                    }
            }
            "Streamtape" -> {
                var src = ProviderLinkFetcher.streamtape(html) ?: ""
                Log.d("MYSRC", src)
                    if (!loaded) {
                        loaded = true
                        streamVideoPlayer.runOnUiThread {
                            streamVideoPlayer.loadVideo(src, 0, true)
                        }
                    }
            }
            "Vidoza" -> {
                var src = ProviderLinkFetcher.vidoza(html) ?: ""
                Log.d("MYSRC", src)
                    if (!loaded) {
                        loaded = true
                        streamVideoPlayer.runOnUiThread {
                            streamVideoPlayer.loadVideo(src, 0, true)
                        }
                    }
            }
            "PlayTube" -> {
            }
        }
    }
}