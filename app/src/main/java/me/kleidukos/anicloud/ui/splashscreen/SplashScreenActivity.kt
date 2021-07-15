package me.kleidukos.anicloud.ui.splashscreen

import android.content.*
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.models.anicloud.DisplayStream
import me.kleidukos.anicloud.room.AppDatabase
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.util.DownloadManager
import me.kleidukos.anicloud.util.EndPoint
import me.kleidukos.anicloud.util.GitHub
import me.kleidukos.anicloud.util.StringUtil
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

        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AniCloud")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val db = database.seriesDao()
        db.clear()

        val type = object : TypeToken<List<DisplayStream>>() {}.type
        val streams: MutableList<DisplayStream> = mutableListOf()

        try {
            streams.addAll(
                Gson().fromJson<List<DisplayStream>>(
                    EndPoint.getAll(),
                    type
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (stream in streams) {
            val room = RoomDisplayStream(
                StringUtil.serialize(stream.title),
                stream.poster,
                stream.url,
                stream.genres
            )

            Log.d("SaveStream", StringUtil.serialize(stream.title))

            db.insertDisplayStreams(room)
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
        if (update.first) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@SplashScreenActivity)
                    .setTitle("Neues Update")
                    .setMessage("https://github.com/Ayley/AniVid | Patches")
                    .setNeutralButton("Later") { _, _ ->
                        init()
                    }
                    .setPositiveButton("Just") { _, _ ->
                        val downloadManager = DownloadManager(update.second, this)
                        downloadManager.startDownload()
                    }.show()
            }
        } else {
            init()
        }
    }
}