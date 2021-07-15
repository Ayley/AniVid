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
        val seasonName = streamView.stream.seasons[pos].name
        streamView.container.removeAllViews()

        Log.d("Stream", seasonName)

        streamView.scope = GlobalScope.launch(Dispatchers.Default) {
            if (!streamView.seasonsMap.containsKey(seasonName))
                streamView.loadSeason(pos)


            streamView.runOnUiThread {
                streamView.container.adapter = SeasonAdapterRecycler(
                    streamView.applicationContext,
                    streamView.seasonsMap.get(seasonName)!!,
                    streamView.stream
                )
            }
        }
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {
        streamView.runOnUiThread {
            streamView.container.adapter = SeasonAdapterRecycler(
                streamView.applicationContext,
                streamView.seasonsMap.toList().first().second,
                streamView.stream
            )
        }
    }
}