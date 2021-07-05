package me.kleidukos.anicloud.activities

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.scraping.GenreFetcher
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.DisplayStreamContainer
import java.lang.Exception

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        buildPolicy()

        // TODO: 30.06.2021 Lade watchlist

        Thread {
            loadAllSeries()

            finish()
        }.start()
    }

    private fun loadAllSeries() {
        val genreDisplaySeasons = mutableMapOf<Genre, List<DisplayStream>>()
        val allSeries = mutableListOf<DisplayStream>()
        for (genre in Genre.values()) {
            if (genre == Genre.MAGICAL_GIRL || genre == Genre.ALL) {
                continue
            }
            var stream: List<DisplayStream>? = null

            stream = GenreFetcher.loadGenreWithPages(genre)

            genreDisplaySeasons.put(genre, stream)
            for (displayStream in stream) {
                if (!allSeries.contains(displayStream)) {
                    allSeries.add(displayStream)
                }
            }
        }
        DataChannelManager.dataStoreInChannel(
            "Main",
            DisplayStreamContainer(genreDisplaySeasons, allSeries)
        )
    }

    private fun buildPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }
}