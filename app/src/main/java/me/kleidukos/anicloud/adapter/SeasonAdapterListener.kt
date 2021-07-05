package me.kleidukos.anicloud.adapter

import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.kleidukos.anicloud.scraping.SeasonFetcher
import me.kleidukos.anicloud.ui.stream.StreamView
import kotlin.streams.toList

class SeasonAdapterListener(private val streamView: StreamView) :
    AdapterView.OnItemSelectedListener {


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val seasonName = streamView.stream.seasons.keys.stream().toList()[pos]
        streamView.container.removeAllViews()

        Log.d("Stream", seasonName)

        GlobalScope.launch(Dispatchers.Unconfined) {
            withContext(Dispatchers.Main) {
                if (!streamView.seasonsMap.containsKey(seasonName)) {
                    if (streamView.user != null) {
                        Log.d("Season", "Load Season with user")
                        streamView.seasonsMap.put(
                            seasonName,
                            SeasonFetcher.loadSeasonWithAccount(
                                seasonName,
                                streamView.stream.seasons?.get(seasonName)!!,
                                streamView.user!!.id,
                                streamView.stream.seasons.keys.indexOf(seasonName)
                            )
                        )
                    } else {
                        streamView.seasonsMap.put(
                            seasonName,
                            SeasonFetcher.loadSeason(
                                seasonName,
                                streamView.stream.seasons?.get(seasonName)!!,
                                streamView.stream.seasons.keys.indexOf(seasonName)
                            )
                        )
                    }
                }

                streamView.runOnUiThread {
                    streamView.container.adapter = SeasonAdapterRecycler(
                        streamView.applicationContext,
                        streamView.seasonsMap[seasonName]!!,
                        streamView.stream
                    )
                }
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