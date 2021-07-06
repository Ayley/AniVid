package me.kleidukos.anicloud.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.BuildConfig
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.DisplayStreamContainer
import me.kleidukos.anicloud.scraping.GenreFetcher
import me.kleidukos.anicloud.util.GitHub

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        buildPolicy()

        val thread = Thread {
            loadAllSeries()

            finish()
        }

        Thread {
            Handler(Looper.getMainLooper()).postDelayed({
                GlobalScope.launch(Dispatchers.IO) {
                    update(thread)
                }
            }, 0)
        }.start()
    }

    private fun loadAllSeries() {
        val genreDisplaySeasons = mutableMapOf<Genre, List<DisplayStream>>()
        val allSeries = mutableListOf<DisplayStream>()
        for (genre in Genre.values()) {
            if (genre == Genre.MAGICAL_GIRL || genre == Genre.ALL) {
                continue
            }
            var stream: List<DisplayStream> = GenreFetcher.loadGenre(genre)

            genreDisplaySeasons.put(genre, stream)
            for (displayStream in stream) {
                if (!allSeries.contains(displayStream)) {
                    allSeries.add(displayStream)
                }
            }
        }
        DataChannelManager.sendData("Main", DisplayStreamContainer(genreDisplaySeasons, allSeries))
    }

    private fun buildPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    private fun update(thread: Thread){
        val update = GitHub.getUpdate(this@SplashScreenActivity)
        if (update.first) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@SplashScreenActivity)
                    .setTitle("Neues Update")
                    .setMessage("https://github.com/Ayley/AniVid | Patches")
                    .setNeutralButton("Später") { _, _ ->
                        thread.start()
                    }
                    .setPositiveButton("Jetzt") { _, _ ->
                        val request = DownloadManager.Request(Uri.parse(update.second))
                        var destination =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .toString() + "/"
                        val fileName = "AniVid.apk"
                        destination += fileName
                        val uri = Uri.parse("file://$destination")
                        request.setTitle("AniVid Update")
                        request.setDescription("Test")
                        request.setMimeType("application/vnd.android.package-archive")
                        request.setDestinationUri(uri)

                        val downloadManager =
                            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                        val broadcast = object : BroadcastReceiver() {
                            override fun onReceive(p0: Context?, p1: Intent?) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    val contentUri = FileProvider.getUriForFile(
                                        applicationContext,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        uri.toFile()
                                    )
                                    val install = Intent(Intent.ACTION_VIEW)
                                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    install.putExtra(
                                        Intent.EXTRA_NOT_UNKNOWN_SOURCE,
                                        true
                                    )
                                    install.data = contentUri
                                    startActivity(install)
                                }else {
                                    val install = Intent(Intent.ACTION_VIEW)
                                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    install.setDataAndType(
                                        uri,
                                        "\"application/vnd.android.package-archive\""
                                    )
                                    applicationContext.startActivity(install)

                                    unregisterReceiver(this)
                                }
                            }
                        }

                        registerReceiver(
                            broadcast,
                            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        )

                        downloadManager.enqueue(request)
                    }.show()
            }
        } else {
            thread.start()
        }
    }
}