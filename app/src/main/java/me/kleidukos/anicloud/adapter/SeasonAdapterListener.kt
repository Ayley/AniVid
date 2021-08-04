package me.kleidukos.anicloud.adapter

import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.ui.stream.StreamView

class SeasonAdapterListener(private val streamView: StreamView) :
    AdapterView.OnItemSelectedListener {


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val season = streamView.seasonList[pos]
        streamView.container.removeAllViews()

        Log.d("Stream", season.title)

        streamView.scope = GlobalScope.launch(Dispatchers.Default) {
            if (!streamView.seasonsMap.containsKey(season.season))
                streamView.loadSeason(pos)


            streamView.runOnUiThread {
                streamView.container.adapter = SeasonAdapterRecycler(
                    streamView.applicationContext,
                    streamView.seasonsMap[season.season]!!,
                    streamView.displayStream
                )
            }
        }
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {
        streamView.runOnUiThread {
            streamView.container.adapter = SeasonAdapterRecycler(
                streamView.applicationContext,
                streamView.seasonsMap[streamView.seasonList[streamView.seasons.selectedItemPosition].season]!!,
                streamView.displayStream
            )
        }
    }
}