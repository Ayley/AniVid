package me.kleidukos.anicloud.ui.stream

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ms.square.android.expandabletextview.ExpandableTextView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.SeasonAdapterListener
import me.kleidukos.anicloud.models.anicloud.Episode
import me.kleidukos.anicloud.models.anicloud.Season
import me.kleidukos.anicloud.models.anicloud.SimpleStream
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.room.user.User
import me.kleidukos.anicloud.tmdb.models.Result
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.util.EndPoint
import me.kleidukos.anicloud.util.StreamConverter

class StreamView : AppCompatActivity() {

    lateinit var displayStream: SimpleStream
    lateinit var title: TextView
    lateinit var back: ImageButton
    lateinit var thumbnail: ImageView
    lateinit var description: TextView
    lateinit var seasons: Spinner
    lateinit var container: RecyclerView

    lateinit var scope: Job

    var user: User? = null
    lateinit var seasonList: List<Season>
    var seasonsMap: MutableMap<Int, List<Episode>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_view)

        setComponents()

        setBackButtonClick()

        val stream = EndPoint.getSimpleStream(intent.extras?.getString("title")!!)

        displayStream = StreamConverter.convert(stream)

        createUI()

        user = MainActivity.database().userDao().getUser()

        GlobalScope.launch(Dispatchers.Default) {
            displayStream.loadSeasons()
            showSeason()
        }
    }

    private fun setComponents(){
        title = findViewById(R.id.stream_title)
        back = findViewById(R.id.back)
        thumbnail = findViewById(R.id.stream_thumbnail)
        description = findViewById(R.id.stream_description)
        container = findViewById(R.id.season_container)
        seasons = findViewById(R.id.stream_season_selector)
    }

    private fun setBackButtonClick(){
        back.setOnClickListener {
            if (this::scope.isInitialized && scope != null) {
                scope.cancel()
            }
            finish()
        }
    }

    private fun createUI(){
        container.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        displayStream.setData(thumbnail, description)

        title.text = displayStream.title
    }

    override fun onRestart() {
        loadSeason(seasons.selectedItemPosition)
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::scope.isInitialized && scope != null) {
            scope.cancel()
        }
    }

    fun loadSeason(pos: Int) {
        user = MainActivity.database().userDao().getUser()

        val episodes = EndPoint.getEpisodes(displayStream, seasonList[pos].season, user)

        seasonsMap.put(seasonList[pos].season, episodes)
    }

    private fun showSeason() {
        seasonList = displayStream.seasons!!

        runOnUiThread {
            description.text = displayStream.description
        }

        val seasonNames = mutableListOf<String>()

        for (info in seasonList) {
            seasonNames.add(info.title)
        }

        runOnUiThread {
            seasons.adapter = ArrayAdapter(
                applicationContext,
                R.layout.spinner_item,
                seasonNames
            )
        }

        var index: Int = if (seasonList.size == 1) {
            0
        } else {
            if (seasonNames.contains("Filme")) {
                1
            } else {
                0
            }
        }

        runOnUiThread {
            seasons.setSelection(index)
            seasons.onItemSelectedListener = SeasonAdapterListener(this)
        }
    }
}
