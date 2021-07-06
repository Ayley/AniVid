package me.kleidukos.anicloud.components

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnDetach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.buildSchedule
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinity
import dev.inmo.krontab.doWhile
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler
import me.kleidukos.anicloud.models.DisplayStream
import java.util.Date.from
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class RandomSeries: LinearLayout {

    private val containerList: RecyclerView

    private lateinit var displayStreams: List<DisplayStream>

    private var run: Boolean = true

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)

        containerList = findViewById(R.id.container_list)
    }

    constructor(context: Context, displayStreams: List<DisplayStream>): super(context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)
        containerList = findViewById(R.id.container_list)

        val textView = findViewById<TextView>(R.id.container_name)
        val removeButton = findViewById<ImageButton>(R.id.container_remove)

        textView.visibility = View.GONE
        removeButton.visibility = View.GONE

        setTopMargin(context)

        this.displayStreams = displayStreams

        Log.d("Home", "Add Schedule")

        loadRandomSeries()

        containerList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setTopMargin(context: Context){
        val params = containerList.layoutParams as RelativeLayout.LayoutParams

        params.topMargin = (5 * context.resources.displayMetrics.density).toInt()

        containerList.layoutParams = params
    }

    private fun loadRandomSeries(){
        val randomSeries = mutableListOf<DisplayStream>()

        for (displayStream in displayStreams){
            if(randomSeries.size == 3){
                break
            }

            randomSeries.add(displayStreams[rand(0, displayStreams.size -1)])
        }
        containerList.removeAllViews()

        val adapterRecycler = StreamAdapterRecycler(context, randomSeries)
        containerList.adapter = adapterRecycler
    }

    fun rand(from: Int, to: Int) : Int {
        return Random.nextInt(to - from) + from
    }

    fun start(){
        run = true
        val scheduler = buildSchedule{
            seconds{
                from (0) every 20
            }
        }

        GlobalScope.launch(Dispatchers.Unconfined) {
            withContext(Dispatchers.Main) {
                scheduler.doWhile {
                    if (!run) {
                        Log.d("Home", "Remove Schedule")
                        false
                    } else {

                        Log.d("Home", "Schedule")

                        loadRandomSeries()

                        true
                    }
                }
            }
        }
    }

    fun stop() {
        run = false
    }
}