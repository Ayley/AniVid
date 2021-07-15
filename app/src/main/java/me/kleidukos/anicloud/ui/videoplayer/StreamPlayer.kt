package me.kleidukos.anicloud.ui.videoplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.adapter.HosterAdapterListener
import me.kleidukos.anicloud.adapter.JavaScriptInterface
import me.kleidukos.anicloud.models.anicloud.Language
import me.kleidukos.anicloud.models.anicloud.Stream
import me.kleidukos.anicloud.room.user.User
import me.kleidukos.anicloud.util.StringUtil
import java.io.DataOutputStream
import java.lang.reflect.Field
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class StreamPlayer : AppCompatActivity() {

    lateinit var videoPlayer: VideoView

    private lateinit var back: ImageButton
    private lateinit var hoster: Spinner
    private lateinit var playerTitle: TextView
    private lateinit var externalPlayer: ImageButton

    private var webView: WebView? = null

    private var user: User? = null
    lateinit var stream: Stream
    var season: Int = 0
    lateinit var language: Language
    var episode: Int = 0
    var id: Int = 0

    private lateinit var controlContainer: RelativeLayout
    private lateinit var play: ImageView
    private lateinit var forward: ImageView
    private lateinit var backward: ImageView
    private lateinit var next: ImageView
    private lateinit var timeline: SeekBar
    private lateinit var endTime: TextView
    private lateinit var currentTime: TextView

    private var run: Boolean = true
    private var seeking: Boolean = false
    private var count: Int = 0
    private var seenTime = 0

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var scheduler: ScheduledFuture<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        videoPlayer = findViewById(R.id.videoplayer)
        back = findViewById(R.id.back)
        hoster = findViewById(R.id.hoster_selector)
        playerTitle = findViewById(R.id.player_title)
        externalPlayer = findViewById(R.id.external_player)

        webView = findViewById(R.id.webview)

        user = MainActivity.database().userDao().getUser()

        stream = getStream(intent.extras?.getString("stream")!!)
        season = intent.extras?.getInt("season")!!
        language = intent.extras?.getSerializable("language") as Language
        episode = intent.extras?.getInt("episode")!!

        controlContainer = findViewById(R.id.control_content)
        play = findViewById(R.id.video_play)
        forward = findViewById(R.id.video_forward)
        backward = findViewById(R.id.video_backward)
        next = findViewById(R.id.next_episode)
        timeline = findViewById(R.id.video_timeline)
        endTime = findViewById(R.id.end_video_time)
        currentTime = findViewById(R.id.current_video_time)

        actionBar?.hide()

        setupViewBack()

        setupVideoPlayer()

        loadHosterSpinner()

        setupExternalPlayer()
    }

    private fun getStream(title: String): Stream {
        val url = URL("http://75.119.146.158:8000/specific")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            connectTimeout = 5000
            readTimeout = 10000

            println(StringUtil.prepare(title) + " || " + title)

            val outputStream = DataOutputStream(outputStream)
            outputStream.write(("title=${StringUtil.prepare(title)}").toByteArray(Charsets.UTF_8))
            outputStream.flush()

            if (responseCode == 404) {
                return null!!
            }

            val result = inputStream.bufferedReader().readText()

            println(result)

            return Gson().fromJson(result, Stream::class.java)
        }
    }

    private fun setupExternalPlayer() {
        externalPlayer.setOnClickListener {
            val uri = getVideoUri()

            if (uri.toString().isEmpty()) {
                Handler(Looper.getMainLooper()).post {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Externer Videoplayer")
                        .setMessage("Warte bis die Url geladen ist")
                        .setNeutralButton("OK") { _, _ ->
                            //Nothing
                        }.show()
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "video/*")

                startActivity(Intent.createChooser(intent, "Wähle einen Player aus"))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("vid", MODE_PRIVATE)

        seenTime = prefs.getInt("currentTime", 0)

        val url = prefs.getString("url", "")

        if (url != null && url != "") {
            loadPlayer(url, false)
        }
    }

    private fun getVideoUri(): Uri {
        return try {
            val mUriField: Field = VideoView::class.java.getDeclaredField("mUri")
            mUriField.isAccessible = true
            mUriField.get(videoPlayer) as Uri
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    private fun update() {
        scheduler = executor.scheduleAtFixedRate(Runnable {
            if (count > 0 && !seeking) {
                count--
            }
            if (count == 0) {
                runOnUiThread {
                    controlContainer.visibility = View.GONE
                }
                count = -1
            }

            val current = videoPlayer.currentPosition

            if (!seeking) {
                runOnUiThread {
                    timeline.progress = current
                }
                Log.d("VideoPlayer", "Change Time: $current")
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    private fun stopUpdate() {
        if(this::scheduler.isInitialized && scheduler != null){
            scheduler.cancel(true)
        }
    }

    private fun loadHosterSpinner() {
        val episodeStream = stream.seasons[id].episodes[episode]

        val hosterNames = mutableListOf<String>()

        for (hoster in episodeStream.providers.filter { it.language == language }) {
            if (hoster.name.equals("playtube", true)) {
                continue
            }
            hosterNames.add(hoster.name)
        }

        hoster.adapter = ArrayAdapter(applicationContext, R.layout.player_spinner_item, hosterNames)
        hoster.onItemSelectedListener = HosterAdapterListener(this)
    }

    fun loadVideo(redirectLink: String, hosterName: String) {
        stopUpdate()
        videoPlayer.stopPlayback()
        val player = this
        GlobalScope.launch(Dispatchers.Main) {
            webView?.addJavascriptInterface(JavaScriptInterface(hosterName, player), "HTMLOUT")
            webView?.settings?.javaScriptEnabled = true
            webView?.loadUrl(redirectLink)


            webView?.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    webView?.visibility = View.GONE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    when {
                        url.equals(stream.seasons[season].episodes[episode].url) -> {
                            webView?.visibility = View.GONE
                            return
                        }
                        redirectLink.equals(url, true) -> {
                            webView?.visibility = View.VISIBLE
                        }
                        else -> {
                            webView?.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                            webView?.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    fun loadPlayer(link: String, start: Boolean) {
        stopUpdate()

        id = if (!stream.seasons.none { it.name.equals("alle filme", true) }) {
            season
        } else {
            season - 1
        }
        playerTitle.text = stream.seasons[id].episodes[episode].titleGerman
            ?: stream.seasons[id].episodes[episode].titleEnglish

        if (link == null || link.isEmpty() || link == "") {
            Handler(Looper.getMainLooper()).post {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Hoster")
                    .setMessage("Die episode ist nicht mehr verfügbar. Wähle einen anderen Hoster aus.")
                    .setNeutralButton("OK") { _, _ ->
                        //Nothing
                    }.show()
            }
            return
        }
        /*if (user != null && webView != null) {
            webView?.visibility = View.INVISIBLE
            CookieManager.getInstance()
                .setCookie(season.episodes[episode].url, "rememberLogin=${user?.id}")
            webView?.loadUrl(season.episodes[episode].url)
        }*/
        Log.d("MediaPlayer", link)
        videoPlayer.setVideoURI(Uri.parse(link))
        if (start) {
            videoPlayer.start()
        } else {
            videoPlayer.seekTo(seenTime)
        }

        update()
    }

    private fun setupViewBack() {
        back.setOnClickListener {
            if (videoPlayer.isPlaying) {
                videoPlayer.stopPlayback()
            }
            finish()
        }
    }

    override fun onPause() {
        stopUpdate()

        val prefs = getSharedPreferences("vid", MODE_PRIVATE)

        val edit = prefs.edit()

        edit.putString("url", getVideoUri().toString())
        edit.putInt("currentTime", videoPlayer.currentPosition)
        edit.commit()

        super.onPause()
    }

    private fun setupVideoPlayer() {
        id = if (!stream.seasons.none { it.name.equals("alle filme", true) }) {
            season
        } else {
            season - 1
        }
        playerTitle.text = stream.seasons[id].episodes[episode].titleGerman
            ?: stream.seasons[id].episodes[episode].titleEnglish

        controlContainer.setOnClickListener {
            controlContainer.visibility = View.VISIBLE
            count = 5
        }

        play.setOnClickListener {
            if (videoPlayer.isPlaying) {
                videoPlayer.pause()
                play.setImageResource(R.drawable.play)
            } else {
                videoPlayer.start()
                play.setImageResource(R.drawable.pause)
            }
            count = 5
        }

        forward.setOnClickListener {
            videoPlayer.seekTo(videoPlayer.currentPosition + 10000)
            videoPlayer.start()
            count = 5
        }

        backward.setOnClickListener {
            videoPlayer.seekTo(videoPlayer.currentPosition - 10000)
            videoPlayer.start()
            count = 5
        }

        timeline.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                currentTime.text = formatTime(p1.toLong())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                seeking = true
                count = 5
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                videoPlayer.seekTo(seekbar?.progress!!)
                videoPlayer.start()
                count = 5
                seeking = false
            }
        })

        videoPlayer.setOnPreparedListener {
            val maxDuration = videoPlayer.duration.toLong()
            endTime.text = formatTime(maxDuration)
            timeline.max = maxDuration.toInt()
            timeline.progress = seenTime
        }

        videoPlayer.setOnClickListener {
            controlContainer.visibility = View.VISIBLE
            count = 5
        }

        videoPlayer.setOnCompletionListener {
            if (videoPlayer.duration > 0) {
                loadNextVideo()
            }
        }

        next.setOnClickListener {
            loadNextVideo()
            count = 5
        }
    }

    @SuppressLint("NewApi")
    private fun formatTime(time: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time)
        val timeOfDay = LocalTime.ofSecondOfDay(seconds)
        return timeOfDay.toString()
    }

    private fun loadNextVideo() {
        if (videoPlayer.isPlaying) {
            videoPlayer.stopPlayback()
        }

        val lastEpisode = stream.seasons[season].episodes.size - 1
        val lastSeason = stream.seasons.size - 1

        if (lastEpisode == episode) {
            if (lastSeason == season) {
                finish()
            } else if (lastSeason > season) {
                season = stream.seasons[season + 1].season
            }
            episode = 0
        } else {
            episode++
        }

        if (lastEpisode == episode && lastSeason == season) {
            next.visibility = View.INVISIBLE
        }

        loadHosterSpinner()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdate()

        getSharedPreferences("vid", MODE_PRIVATE).edit().remove("currentTime").remove("url")
            .commit()

        if (webView != null) {
            webView?.destroy()
            webView = null
        }
    }
}