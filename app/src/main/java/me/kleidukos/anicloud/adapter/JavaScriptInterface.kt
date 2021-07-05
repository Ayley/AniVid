package me.kleidukos.anicloud.adapter

import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import me.kleidukos.anicloud.scraping.HosterLinkFetcher
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer

class JavaScriptInterface(val hoster: String, val streamPlayer: StreamPlayer) {

    var loaded = false;

    @JavascriptInterface
    fun processHTML(html: String?) {
        Log.d("MYHTML", html!!)

        when (hoster) {

            "VOE" -> {
                var src = HosterLinkFetcher.veo(html)
                Log.d("MYSRC", src)
                if (src != "")
                    if (!loaded) {
                        loaded = true
                        Log.d("MYSRC", src)
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src)
                        }
                    }
            }
            "Streamtape" -> {
                var src = HosterLinkFetcher.streamtape(html)
                Log.d("MYSRC", src)
                if (src != "")
                    if (!loaded) {
                        loaded = true
                        Log.d("MYSRC", src)
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src)
                        }
                    }
            }
            "Vidoza" -> {
                var src = HosterLinkFetcher.vidoza(html)
                Log.d("MYSRC", src)
                if (src != "")
                    if (!loaded) {
                        loaded = true
                        Log.d("MYSRC", src)
                        streamPlayer.runOnUiThread {
                            streamPlayer.loadPlayer(src)
                        }
                    }
            }
            "PlayTube" -> {
            }
        }
    }
}