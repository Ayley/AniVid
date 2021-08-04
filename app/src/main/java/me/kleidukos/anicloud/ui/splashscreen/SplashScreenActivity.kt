package me.kleidukos.anicloud.ui.splashscreen

import android.content.*
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.room.AppDatabase
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.scraping.AnimeScraper
import me.kleidukos.anicloud.util.DownloadManager
import me.kleidukos.anicloud.util.GitHub
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        title = findViewById(R.id.splashscreen_title)

        buildPolicy()

        Handler(Looper.getMainLooper()).postDelayed({
            GlobalScope.launch(Dispatchers.IO) {
                update()
            }
        }, 1000)
    }

    private fun init() {
        GlobalScope.launch(Dispatchers.Default) {
            loadAllSeries()

            val intent = Intent(applicationContext, MainActivity::class.java)

            runOnUiThread {
                startActivity(intent)
            }
        }
    }

    private fun loadAllSeries() {
        runOnUiThread {
            title.text = "Loading animes"
        }

        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AniCloud").allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val db = database.seriesDao()
        db.clear()

        val streams = AnimeScraper.scrapAll()

        for (stream in streams) {
            Log.d("SaveStream", stream.title)

            db.insertDisplayStreams(RoomDisplayStream(stream.title, stream.altTitle, stream.year, stream.url, stream.description, "", null))
        }
    }

    private fun buildPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    private fun update() {
        val update = GitHub.getUpdate(this@SplashScreenActivity)
        val link = update.second.split(";")[0]
        val description = update.second.split(";")[1] ?: ""
        if (update.first) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@SplashScreenActivity)
                    .setTitle("New update")
                    .setMessage(description)
                    .setNeutralButton("Later") { _, _ ->
                        init()
                    }
                    .setPositiveButton("Now") { _, _ ->
                        val downloadManager = DownloadManager(link, this)
                        downloadManager.startDownload()
                    }.show()
            }
        } else {
            init()
        }
    }
}