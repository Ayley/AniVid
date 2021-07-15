package me.kleidukos.anicloud.ui.stream

import android.os.Bundle
import android.util.Log
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
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.adapter.SeasonAdapterListener
import me.kleidukos.anicloud.models.anicloud.Episode
import me.kleidukos.anicloud.room.user.User
import me.kleidukos.anicloud.models.anicloud.Stream
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.util.EndPoint
import me.kleidukos.anicloud.util.StringUtil

class StreamView : AppCompatActivity() {

    lateinit var title: TextView
    lateinit var back: ImageButton
    lateinit var thumbnail: ImageView
    lateinit var description: ExpandableTextView
    lateinit var seasons: Spinner
    lateinit var container: RecyclerView

    lateinit var scope: Job

    var user: User? = null
    lateinit var stream: Stream
    var seasonsMap: MutableMap<String, List<Episode>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream_view)

        title = findViewById(R.id.stream_title)
        back = findViewById(R.id.back)
        thumbnail = findViewById(R.id.stream_thumbnail)
        description = findViewById(R.id.stream_description)
        container = findViewById(R.id.season_container)
        seasons = findViewById(R.id.stream_season_selector)

        container.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val displayStream = intent.extras?.getSerializable("display") as RoomDisplayStream

        title.text = displayStream.title

        Picasso.get().load(displayStream.poster).into(thumbnail)

        back.setOnClickListener {
            scope.cancel()
            finish()
        }

        user = MainActivity.database().userDao().getUser()

        GlobalScope.launch(Dispatchers.Default) {
            loadStream(displayStream.title)
        }
    }

    override fun onRestart() {
        loadSeason(seasons.selectedItemPosition)
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    fun loadSeason(pos: Int) {
        user = MainActivity.database().userDao().getUser()

        seasonsMap.put(stream.seasons[pos].name, stream.seasons[pos].episodes)
    }

    private fun loadStream(title: String){
        stream = EndPoint.getStream(title)

        runOnUiThread {
            description.text = stream.description
        }

        val seasonNames = mutableListOf<String>()

        for (info in stream.seasons) {
            seasonNames.add(info.name)
        }

        runOnUiThread {
            seasons.adapter = ArrayAdapter(
                applicationContext,
                R.layout.spinner_item,
                seasonNames
            )
        }

        var index: Int = if (stream.seasons.size == 1) {
            0
        } else {
            if (seasonNames.contains("Alle Filme")) {
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
