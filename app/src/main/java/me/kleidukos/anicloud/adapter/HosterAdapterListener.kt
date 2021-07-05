package me.kleidukos.anicloud.adapter

import android.view.View
import android.widget.AdapterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer

class HosterAdapterListener(private val streamPlayer: StreamPlayer): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val hoster = streamPlayer.season.episodes[streamPlayer.episode].hoster[pos]

        val id = if(hoster.streams.containsKey(streamPlayer.language)){
            hoster.streams.get(streamPlayer.language)
        }else{
            streamPlayer.finish()
        }

        val link = "https://anicloud.io/redirect/$id"

        GlobalScope.launch (Dispatchers.Main){
            streamPlayer.loadVideo(link, hoster.name)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}