package me.kleidukos.anicloud.ui.videoplayer

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doWhile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.ProviderAdapterListener
import me.kleidukos.anicloud.adapter.JavaScriptInterface
import me.kleidukos.anicloud.adapter.OnSwipeTouchListener
import me.kleidukos.anicloud.models.anicloud.*
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.util.EndPoint
import me.kleidukos.anicloud.util.StreamConverter
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

class StreamVideoPlayer : AppCompatActivity() {

    //loading container
    private lateinit var loadingContainer: ConstraintLayout

    //videoView webView
    private lateinit var videoView: VideoView

    private lateinit var webView: WebView
    //player controls container
    private lateinit var controlContainer: ConstraintLayout

    //player controls top
    private lateinit var backArrow: ImageButton

    private lateinit var playerTitle: TextView
    private lateinit var providerSpinner: Spinner
    private lateinit var externalPlayers: ImageButton
    //player controls bottom
    private lateinit var playedTime: TextView

    private lateinit var timeline: SeekBar
    private lateinit var playTime: TextView
    private lateinit var previousVideo: ImageButton
    private lateinit var skipBackward: ImageButton
    private lateinit var mediaControl: ImageButton
    private lateinit var skipForward: ImageButton
    private lateinit var nextVideo: ImageButton
    //volume controls
    private lateinit var volumeContainer: ConstraintLayout

    private lateinit var volumeBar: SeekBar
    //brightness controls
    private lateinit var brightnessContainer: ConstraintLayout

    private lateinit var brightnessBar: SeekBar
    //variables
    internal var count: Int = 0

    internal var isTimelineInUse: Boolean = false
    private lateinit var job: Job
    internal lateinit var stream: SimpleStream
    internal lateinit var episode: Episode
    private lateinit var episodes: List<Episode>
    private lateinit var season: Season
    internal lateinit var seasons: List<Season>
    private lateinit var selectedLanguage: Language
    internal lateinit var currentProviders: List<Provider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vidoeplayer)

        setComponents()

        setVariables()

        setVideoTitle()

        setBackArrowListener()

        hideDecor()

        setVideoViewControlsListener()

        loadProviderSpinner()

        setupExternalPlayers()

        checkChangeEpisodes()
    }

    private fun checkChangeEpisodes() {
        if (seasons[0].season == episode.season && episode.episode == 1){
            previousVideo.visibility = View.INVISIBLE
        }else{
            previousVideo.visibility = View.VISIBLE
        }

        if(seasons[seasons.lastIndex].season == episode.season && episode.episode == episodes.size){
            nextVideo.visibility = View.INVISIBLE
        }else{
            nextVideo.visibility = View.VISIBLE
        }
    }

    private fun loadProviderSpinner() {
        val providersPoint = EndPoint.getProviderValues(episode, selectedLanguage)
        val providers = providersPoint?.filter { it.name != "PlayTube" || it.name != "NinjaStream"}
        println(providers)
        currentProviders = if(providers == null){
            Handler(Looper.getMainLooper()).post {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Unsupported language")
                    .setMessage("This episode doesn't contains this stream with the selected language. \nPlease choose another one. The player will close after clicking OK.")
                    .setNeutralButton("OK") { _, _ ->
                        finish()
                }.show()
            }
            return
        }else{
            providers
        }

        val  names = mutableListOf<String>()

        for (provider in currentProviders){
            if(!names.contains(provider.name)){
                names.add(provider.name)
            }
        }

        providerSpinner.adapter = ArrayAdapter(applicationContext, R.layout.player_spinner_item, names)
        providerSpinner.onItemSelectedListener = ProviderAdapterListener(this)
    }

    private fun setVariables() {
        val s = EndPoint.getSimpleStream(intent.extras?.getString("stream")!!)
        stream = StreamConverter.convert(s)
        episode = intent.extras?.getSerializable("episode")!! as Episode
        val seasonId = intent.extras?.getInt("season")!!
        stream.loadSeasons()
        seasons = stream.seasons!!
        season = seasons.first { it.season == seasonId }
        episodes = EndPoint.getEpisodes(stream, season.season, null)
        selectedLanguage = intent.extras?.getSerializable("language") as Language
    }

    private fun hideDecor() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setVideoViewControlsListener() {
        //show hide controls
        controlContainer.setOnClickListener {
            toggleControlContainerVisibility()
        }

        controlContainer.setOnTouchListener(
            OnTouch(
                applicationContext,
                volumeBar,
                volumeContainer,
                brightnessBar,
                brightnessContainer,
                this
            )
        )

        videoView.setOnClickListener {
            toggleControlContainerVisibility()
        }

        //set player controls listener
        previousVideo.setOnClickListener {
            loadPreviousVideo()
        }

        skipBackward.setOnClickListener {
            videoViewSeekTo(-10)
        }

        mediaControl.setOnClickListener {
            togglePlaying()
        }

        skipForward.setOnClickListener {
            videoViewSeekTo(10)
        }

        nextVideo.setOnClickListener {
            loadNextVideo()
        }

        //set viewView listener
        videoView.setOnCompletionListener {
            loadNextVideo()
        }

        videoView.setOnPreparedListener {
            setVideoTitle()

            timeline.max = videoView.duration

            playTime.text = formatTime(videoView.duration)
        }

        videoView.setOnTouchListener(
            OnTouch(
                applicationContext,
                volumeBar,
                volumeContainer,
                brightnessBar,
                brightnessContainer,
                this
            )
        )

        //set timeline listener
        timeline.setOnSeekBarChangeListener(OnTimelineChanged(videoView, playedTime, this))
    }

    internal fun formatTime(time: Int): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(Date(time.toLong() - 1000 * 60 * 60))
    }

    private fun setVideoTitle() {
        playerTitle.text = if(episode.title_german == null || episode.title_german?.isEmpty()!!){
            episode.title_english
        }else{
            episode.title_german
        }
    }

    private fun togglePlaying() {
        if (videoView.isPlaying) {
            videoView.pause()
            mediaControl.setImageResource(R.drawable.player_play)
        } else {
            videoView.start()
            mediaControl.setImageResource(R.drawable.player_pause)
        }
    }

    private fun videoViewSeekTo(amountInSeconds: Int) {
        val currentTime = videoView.currentPosition

        if (videoView.isPlaying) {
            videoView.seekTo(currentTime + (amountInSeconds * 1000))
            videoView.start()
        } else {
            videoView.seekTo(currentTime + (amountInSeconds * 1000))
        }
    }

    private fun loadNextVideo() {
        if(episode.episode == episodes.last().episode){
            season = seasons.first{ it.season == season.season +1}

            episodes = EndPoint.getEpisodes(stream, season.season, null)

            episode = episodes.first()
        }else{
            episode = episodes.first{ it.episode == episode.episode + 1 }
        }

        setVideoTitle()
        loadProviderSpinner()
        checkChangeEpisodes()
    }

    private fun loadPreviousVideo() {
        if(episode.episode == 1){
            season = seasons.first{ it.season == season.season -1}

            episodes = EndPoint.getEpisodes(stream, season.season, null)

            episode = episodes.last()
        }else{
            episode = episodes.first{ it.episode == episode.episode -1 }
        }

        println(episode.toString())

        setVideoTitle()
        loadProviderSpinner()
        checkChangeEpisodes()
    }

    private fun toggleControlContainerVisibility() {
        if (!isTimelineInUse) {
            if(controlContainer.visibility == View.GONE){
                controlContainer.visibility = View.VISIBLE
                providerSpinner.isEnabled = true
                count = 5
            }else{
                controlContainer.visibility == View.GONE
                volumeContainer.visibility == View.GONE
                brightnessContainer.visibility = View.GONE
                providerSpinner.isEnabled = false
                count = 0
            }
        }
    }

    private fun setBackArrowListener() {
        backArrow.setOnClickListener {

            if (this::job.isInitialized && job != null && job.isActive) {
                job.cancel()
            }

            finish()
        }
    }

    private fun setComponents() {
        //loading container
        loadingContainer = findViewById(R.id.loading_container)

        //videoView webView
        videoView = findViewById(R.id.videoplayer)
        webView = findViewById(R.id.webview)

        //player controls container
        controlContainer = findViewById(R.id.control_container)

        //player controls top
        backArrow = findViewById(R.id.back_arrow)
        playerTitle = findViewById(R.id.player_title)
        providerSpinner = findViewById(R.id.providers)
        externalPlayers = findViewById(R.id.external_player)

        //player controls bottom
        playedTime = findViewById(R.id.player_played_time)
        timeline = findViewById(R.id.time_bar)
        playTime = findViewById(R.id.player_play_time)
        previousVideo = findViewById(R.id.previous_video)
        skipBackward = findViewById(R.id.skip_backwards)
        mediaControl = findViewById(R.id.control_video)
        skipForward = findViewById(R.id.skip_forward)
        nextVideo = findViewById(R.id.next_video)

        //volume controls
        volumeContainer = findViewById(R.id.volume_container)
        volumeBar = findViewById(R.id.volume_bar)

        //brightness controls
        brightnessContainer = findViewById(R.id.brightness_container)
        brightnessBar = findViewById(R.id.brightness_bar)
    }

    private fun getVideoUri(): Uri {
        return try {
            val mUriField: Field = VideoView::class.java.getDeclaredField("mUri")
            mUriField.isAccessible = true
            mUriField.get(videoView) as Uri
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    override fun onResume() {
        loadingContainer.visibility = View.VISIBLE

        val prefs = getSharedPreferences("player", MODE_PRIVATE)

        val url = prefs.getString("url", "")!!

        val seekTime = prefs.getInt("seek", 0)!!

        if (url != null && url != "") {
            loadVideo(url, seekTime, false)
        }

        super.onResume()
    }

    override fun onPause() {

        val prefs = getSharedPreferences("player", MODE_PRIVATE)

        val edit = prefs.edit()

        edit.putString("url", getVideoUri().toString())
        edit.putInt("seek", videoView.currentPosition)

        edit.commit()

        if (this::job.isInitialized && job != null && job.isActive) {
            job.cancel()
        }

        super.onPause()
    }

    override fun onDestroy() {
        getSharedPreferences("player", MODE_PRIVATE).edit().remove("seek").remove("url").commit()
        super.onDestroy()
    }

    internal fun loadVideoUrl(url: String, providerName: String) {
        loadingContainer.visibility = View.VISIBLE

        videoView.stopPlayback()
        webView.addJavascriptInterface(JavaScriptInterface(providerName, this), "HTMLOUT")
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)

        webView.webViewClient = CustomWebViewClient(this)
    }

    internal fun loadVideo(url: String, seekTime: Int, directStart: Boolean) {
        videoView.setVideoURI(Uri.parse(url))

        if (directStart) {
            videoView.start()
            updateUI()
        } else {
            videoView.seekTo(seekTime)
        }
        loadingContainer.visibility = View.GONE
    }

    private fun setupExternalPlayers() {
        externalPlayers.setOnClickListener {
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

    private fun updateUI() {
        val scheduler = buildSchedule {
            seconds {
                from(0) every 1
            }
        }

        job = GlobalScope.launch(Dispatchers.Default) {
            scheduler.doWhile {

                if (count > 0 && !isTimelineInUse) {
                    count--
                }

                if (count == 0) {
                    runOnUiThread {
                        controlContainer.visibility = View.GONE
                        volumeContainer.visibility = View.GONE
                        brightnessContainer.visibility = View.GONE
                        providerSpinner.isEnabled = false
                    }
                }

                if (!isTimelineInUse) {
                    runOnUiThread {
                        timeline.progress = videoView.currentPosition
                    }
                }

                true
            }
        }
    }

    internal fun toggleWebView(show: Boolean) {
        if (show) {
            webView.visibility == View.VISIBLE
        } else {
            webView.visibility == View.GONE
        }
    }

    internal fun webViewScrapVideoSrc() {
        webView?.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        toggleWebView(false)
    }
}

class OnTimelineChanged(
    val videoView: VideoView,
    val playedTime: TextView,
    val streamVideoPlayer: StreamVideoPlayer
) : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        //change current time by seekbar change
        playedTime.text = streamVideoPlayer.formatTime(videoView.currentPosition)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //don't hide controlContainer while seeking
        streamVideoPlayer.isTimelineInUse = true
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        //now controlContainer can hide
        streamVideoPlayer.isTimelineInUse = false
        //update time
        playedTime.text = streamVideoPlayer.formatTime(p0?.progress!!)
        //set count to hide controlContainer
        streamVideoPlayer.count = 5
        //seek videoView
        videoView.seekTo(p0?.progress!!)
        videoView.start()
    }
}

class OnTouch(
    val ctx: Context,
    val volume: SeekBar,
    val volumeContainer: ConstraintLayout,
    val brightness: SeekBar,
    val brightnessContainer: ConstraintLayout,
    val streamVideoPlayer: StreamVideoPlayer
) : OnSwipeTouchListener(ctx) {

    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onSwipeTop(event: MotionEvent) {
        if (event.x < ctx.resources.displayMetrics.widthPixels / 2) {
            brightness.progress =
                Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            brightnessContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustBrightness(255 / 15)
        } else {
            volume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustVolume(1)
        }
    }

    override fun onSwipeBottom(event: MotionEvent) {
        if (event.x < ctx.resources.displayMetrics.widthPixels / 2) {
            brightness.progress =
                Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS)

            brightnessContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustBrightness(-(255 / 15))
        } else {
            volume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustVolume(-1)
        }
    }

    private fun adjustBrightness(amount: Int) {
        val current = brightness.progress

        setBrightness(current + amount)

        brightness.progress = current + amount
    }

    private fun adjustVolume(amount: Int) {
        val current = volume.progress

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            current + amount,
            AudioManager.FLAG_VIBRATE
        )

        volume.progress = current + amount
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setBrightness(brightness: Int) {
        var b = brightness
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(streamVideoPlayer.applicationContext)) {
                val cResolver = streamVideoPlayer.applicationContext.contentResolver;
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, b);

            } else {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.data = Uri.parse("package:" + streamVideoPlayer.packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                streamVideoPlayer.startActivity(intent);
            }
        }
    }
}

class CustomWebViewClient(val streamVideoPlayer: StreamVideoPlayer) : WebViewClient() {

    private var url: String = ""

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        this.url = url!!
    }

    override fun onPageFinished(view: WebView?, url: String?) {streamVideoPlayer.toggleWebView(true)
        if (url!!.contains("redirect")) {
            streamVideoPlayer.toggleWebView(true)
        } else {
            Log.d("WebView", url!!)
            streamVideoPlayer.webViewScrapVideoSrc()
        }
    }
}