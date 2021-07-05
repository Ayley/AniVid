package me.kleidukos.anicloud.ui.videoplayer

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doWhile
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.HosterAdapterListener
import me.kleidukos.anicloud.adapter.JavaScriptInterface
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.enums.Language
import me.kleidukos.anicloud.models.Season
import me.kleidukos.anicloud.models.Stream
import me.kleidukos.anicloud.room.User
import me.kleidukos.anicloud.scraping.HosterLinkFetcher
import me.kleidukos.anicloud.scraping.SeasonFetcher
import java.io.FileNotFoundException
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.Exception as Exception


class StreamPlayer : AppCompatActivity() {

    lateinit var videoPlayer: VideoView

    private lateinit var back: ImageButton
    private lateinit var hoster: Spinner
    private lateinit var playerTitle: TextView

    private var webView: WebView? = null

    private var user: User? = null
    lateinit var stream: Stream
    lateinit var season: Season
    lateinit var language: Language
    var episode: Int = 0

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        videoPlayer = findViewById(R.id.videoplayer)
        back = findViewById(R.id.back)
        hoster = findViewById(R.id.hoster_selector)
        playerTitle = findViewById(R.id.player_title)

        webView = findViewById(R.id.webview)

        try {
            user = DataChannelManager.dataChannelStorage("StreamView") as User
        } catch (e: java.lang.Exception) {
            //Nothing
        }
        stream = intent.extras?.getSerializable("stream") as Stream
        season = intent.extras?.getSerializable("season") as Season
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

        setupViewBack()

        setupVideoPlayer()

        loadHosterSpinner()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("currentTime", videoPlayer.currentPosition)
        try {
            val mUriField: Field = VideoView::class.java.getDeclaredField("mUri")
            mUriField.setAccessible(true)
            outState.putString("uri", mUriField.get(videoPlayer) as String)
        } catch (e: Exception) {
        }

        stopUpdate()

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        videoPlayer.setVideoURI(Uri.parse(savedInstanceState.getString("uri")))
        videoPlayer.start()
        videoPlayer.seekTo(savedInstanceState.getInt("currentTime"))
        update()

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun update() {
        run = true
        val scheduler = buildSchedule {
            seconds {
                from(0) every 1
            }
        }

        GlobalScope.launch(newSingleThreadContext("ScreenSync")) {
            scheduler.doWhile {
                if (!run) {
                    false
                }
                if (count > 0 && !seeking) {
                    count--
                }
                if (count == 0) {
                    runOnUiThread {
                        controlContainer.visibility = View.INVISIBLE
                    }
                }

                val current = videoPlayer.currentPosition

                if(!seeking){
                    runOnUiThread {
                        timeline.progress = current

                        Log.d("VideoPlayer", "Change Time: $current")
                    }
                }

                true
            }
        }
    }

    private fun stopUpdate() {
        run = false
    }

    private fun loadHosterSpinner() {
        val episodeStream = season.episodes[episode]

        val hosterNames = mutableListOf<String>()

        for (hoster in episodeStream.hoster) {
            hosterNames.add(hoster.name)
        }

        hoster.adapter = ArrayAdapter(applicationContext, R.layout.player_spinner_item, hosterNames)
        hoster.onItemSelectedListener = HosterAdapterListener(this)
    }

    fun loadVideo(redirectLink: String, hosterName: String) {
        val player = this
        GlobalScope.launch(Dispatchers.Main) {
            webView?.addJavascriptInterface(JavaScriptInterface(hosterName, player), "HTMLOUT")
            videoPlayer.stopPlayback()
            webView?.settings?.javaScriptEnabled = true

            webView?.loadUrl(redirectLink)


            webView?.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    webView?.visibility = View.GONE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    when {
                        url.equals(season.episodes[episode].link, true) -> {
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

    fun loadPlayer(link: String) {
        if (user != null && webView != null) {
            webView?.visibility = View.INVISIBLE
            CookieManager.getInstance()
                .setCookie(season.episodes[episode].link, "rememberLogin=${user?.id}")
            webView?.loadUrl(season.episodes[episode].link)
        }
        Log.d("MediaPlayer", link)
        videoPlayer.setVideoURI(Uri.parse(link))
        videoPlayer.start()
    }

    private fun setupViewBack() {
        back.setOnClickListener {
            if(videoPlayer.isPlaying){
                videoPlayer.stopPlayback()
            }
            finish()
        }
    }

    private fun setupVideoPlayer() {
        playerTitle.text =
            season.episodes[episode].titleGerman ?: season.episodes[episode].titleEnglish ?: ""

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
            playerTitle.text = season.episodes[episode].titleGerman ?: season.episodes[episode].titleEnglish ?: ""
            val maxDuration = videoPlayer.duration.toLong()
            endTime.text = formatTime(maxDuration)
            timeline.max = maxDuration.toInt()
            try {
                videoPlayer.start()
            } catch (e: java.lang.Exception) {
            }
            update()
        }

        videoPlayer.setOnClickListener {
            controlContainer.visibility = View.VISIBLE
            count = 5
        }

        videoPlayer.setOnCompletionListener {
            loadNextVideo()
        }

        next.setOnClickListener {
            loadNextVideo()
            count = 5
        }
    }

    private fun formatTime(time: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time)
        val timeOfDay = LocalTime.ofSecondOfDay(seconds)
        return timeOfDay.toString()
    }

    private fun loadNextVideo() {
        if (videoPlayer.isPlaying) {
            videoPlayer.stopPlayback()
        }

        if (season.episodes.size - 1 == episode) {
            if (stream.seasons.size - 1 == season.seasonId) {
                finish()
            } else if (stream.seasons.size - 1 > season.seasonId) {
                val currentSeason = stream.seasons.keys.indexOf(season.name)
                season = if (user != null) {
                    SeasonFetcher.loadSeasonWithAccount(
                        stream.seasons.keys.toList()[currentSeason + 1],
                        stream.seasons.values.toList()[currentSeason + 1],
                        user?.id!!,
                        currentSeason + 1
                    )
                } else {
                    SeasonFetcher.loadSeason(
                        stream.seasons.keys.toList()[currentSeason + 1],
                        stream.seasons.values.toList()[currentSeason + 1],
                        currentSeason + 1
                    )
                }
            }
            episode = 0
        }else{
            episode++
        }

       loadHosterSpinner()

    }

    override fun onDestroy() {
        super.onDestroy()

        if (webView != null) {
            webView?.destroy()
            webView = null
        }
    }
}