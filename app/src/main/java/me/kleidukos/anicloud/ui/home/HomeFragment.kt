package me.kleidukos.anicloud.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.components.RandomSeries
import me.kleidukos.anicloud.components.SeriesContainer
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.datachannel.IDataChannel
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.models.DisplayStreamContainer
import java.lang.Exception

class HomeFragment : Fragment(), IDataChannel {

    private lateinit var containerLayout: LinearLayout
    private lateinit var viewGroup: ViewGroup

    private var displayStreamContainer: DisplayStreamContainer? = null

    private lateinit var viewModel: HomeViewModel

    private var randomSeries: RandomSeries? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        viewGroup = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup

        containerLayout = viewGroup.findViewById(R.id.container_layout)

        return viewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        try {
            displayStreamContainer = DataChannelManager.dataChannelStorage("Main") as DisplayStreamContainer
        }catch (e: Exception){
            //Nothing
        }

        if(displayStreamContainer != null && containerLayout.size == 0){
            loadRandomSeries()

            loadGenres()
        }
    }

    override fun onPause() {
        super.onPause()
        randomSeries?.stop()
        randomSeries = null
        containerLayout.removeAllViews()
    }

    fun loadRandomSeries() {
        activity?.runOnUiThread {
            if(randomSeries == null){
                randomSeries = RandomSeries(viewGroup.context, displayStreamContainer?.allSeries!!)

                containerLayout.addView(randomSeries)
            }
        }
    }

    fun loadGenres() {
        Thread {
            this.activity?.runOnUiThread() {
                loadGenre(viewGroup.context, Genre.POPULAR, Genre.POPULAR.genreName)

                loadGenre(viewGroup.context, Genre.NEW, Genre.NEW.genreName)

                loadGenre(viewGroup.context, Genre.DRAMA, "Genre: ${Genre.DRAMA.genreName}")
            }
        }.start()
    }

    fun loadGenre(context: Context, genre: Genre, title: String) {
        val streams: List<DisplayStream> = displayStreamContainer?.genreMap?.get(genre)!!

        if (streams.size > 30) {
            addGenre(context, streams.subList(0, 30), title)
            return
        }
        addGenre(context, streams, title)
    }

    private fun addGenre(context: Context, displayStreams: List<DisplayStream>, title: String) {
        val view = SeriesContainer(context, displayStreams, title)

        containerLayout.addView(view)
    }

    override fun <T> onDataReceived(data: T) {
        if (data is DisplayStreamContainer) {
            Log.d("Home", "Loading Animes")

            displayStreamContainer = data

            loadRandomSeries()

            loadGenres()

            Log.d("Home", "Loaded")
        }
    }

}