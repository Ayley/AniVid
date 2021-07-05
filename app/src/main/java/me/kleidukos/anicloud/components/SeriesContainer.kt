package me.kleidukos.anicloud.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler

class SeriesContainer: LinearLayout {

    private val containerName: TextView
    private val containerList: RecyclerView

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)

        containerName = findViewById(R.id.container_name)
        containerList = findViewById(R.id.container_list)
    }

    constructor(context: Context, displayStreams: List<DisplayStream>, title: String): super(context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)
        containerName = findViewById(R.id.container_name)
        containerList = findViewById(R.id.container_list)

        if(title.isBlank()){
            containerName.visibility = View.GONE

            setTopMargin(context)
        }else{
            containerName.text = title
        }

        containerList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        containerList.adapter = StreamAdapterRecycler(context, displayStreams, 18)
    }

    private fun setTopMargin(context: Context){
        val params = containerList.layoutParams as LinearLayout.LayoutParams

        params.topMargin = (5 / context.resources.displayMetrics.density).toInt()

        containerList.layoutParams = params
    }
}