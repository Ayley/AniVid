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
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.SeasonAdapterListener
import me.kleidukos.anicloud.adapter.SeasonAdapterRecycler
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.models.Season
import me.kleidukos.anicloud.models.Stream
import me.kleidukos.anicloud.room.User
import me.kleidukos.anicloud.scraping.SeasonFetcher
import java.lang.Exception
import kotlin.streams.toList

class StreamView : AppCompatActivity() {

    lateinit var title: TextView
    lateinit var back: ImageButton
    lateinit var thumbnail: ImageView
    lateinit var description: ExpandableTextView
    lateinit var seasons: Spinner
    lateinit var container: RecyclerView

    var user: User? = null
    lateinit var stream: Stream
    var seasonsMap: MutableMap<String, Season> = mutableMapOf()

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

        stream = intent.extras?.getSerializable("stream") as Stream

        title.text = stream.name

        description.text = stream.description

        Picasso.get().load(stream.thumbnailUrl).into(thumbnail)

        back.setOnClickListener {
            finish()
        }

        try {
            user = DataChannelManager.dataChannelStorage("StreamView") as User
        } catch (e: Exception) {
            //Nothing
        }

        seasons.adapter = ArrayAdapter(
            applicationContext,
            R.layout.spinner_item,
            stream.seasons.keys.stream().toList()
        )

        var index: Int = if (stream.seasons.size == 1) {
            0
        } else {
            if (stream.seasons.containsKey("Alle Filme")) {
                1
            } else {
                0
            }
        }

        seasons.setSelection(index)

        seasons.onItemSelectedListener = SeasonAdapterListener(this)
    }

    override fun onRestart() {
        try {
            user = DataChannelManager.dataChannelStorage("StreamView") as User
        } catch (e: Exception) {
            //Nothing
        }

        val seasonName = seasons.selectedItem as String

        if (user != null) {
            Log.d("Season", "Load Season with user")
            seasonsMap.put(
                seasonName,
                SeasonFetcher.loadSeasonWithAccount(
                    seasonName,
                    stream.seasons?.get(seasonName)!!,
                    user!!.id,
                    stream.seasons?.keys.indexOf(seasonName)
                )
            )
        } else {
            seasonsMap.put(
                seasonName,
                SeasonFetcher.loadSeason(
                    seasonName,
                    stream.seasons?.get(seasonName)!!,
                    stream.seasons?.keys.indexOf(seasonName)
                )
            )
        }

        runOnUiThread {
            container.adapter = SeasonAdapterRecycler(
                applicationContext,
                seasonsMap[seasonName]!!,
                stream
            )
        }
        super.onRestart()
    }
}
