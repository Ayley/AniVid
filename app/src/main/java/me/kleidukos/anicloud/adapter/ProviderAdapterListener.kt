package me.kleidukos.anicloud.adapter

import android.view.View
import android.widget.AdapterView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.ui.videoplayer.StreamVideoPlayer
import java.lang.Exception

class ProviderAdapterListener(private val streamVideoPlayer: StreamVideoPlayer): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val provider = streamVideoPlayer.currentProviders[pos]

        try {
            val link = "https://anicloud.io/redirect/${provider.id}"

            GlobalScope.launch (Dispatchers.Main){
                streamVideoPlayer.loadVideoUrl(link, provider.name)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Nothing
    }
}