package me.kleidukos.anicloud.adapter

import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer
import java.lang.Exception

class HosterAdapterListener(private val streamPlayer: StreamPlayer): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val hosterName = streamPlayer.hoster_selector.selectedItem as String

        val providers = streamPlayer.stream.seasons[streamPlayer.id].episodes[streamPlayer.episode].providers.filter { it.language == streamPlayer.language }

        val provider = providers.first { it.name.equals(hosterName, true) }

        try {
            val link = "https://anicloud.io/redirect/${provider.redirectId}"

            GlobalScope.launch (Dispatchers.Main){
                streamPlayer.loadVideo(link, provider.name)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Nothing
    }
}