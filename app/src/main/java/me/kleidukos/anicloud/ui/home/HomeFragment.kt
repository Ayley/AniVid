package me.kleidukos.anicloud.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.activities.MainActivity
import me.kleidukos.anicloud.components.GenreSelector
import me.kleidukos.anicloud.components.RandomSeries
import me.kleidukos.anicloud.components.SeriesContainer
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.datachannel.IDataChannel
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.models.DisplayStreamContainer
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var containerLayout: LinearLayout
    private lateinit var viewGroup: ViewGroup

    private lateinit var viewModel: HomeViewModel

    private lateinit var randomSeries: RandomSeries
    private lateinit var genreSelector: GenreSelector

    private var watchlist: MutableList<DisplayStream> = mutableListOf()
    private var genres: MutableList<Genre> = mutableListOf()

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
        Log.d("Fragment_Home", "onResume")

        if(!MainActivity.isStreamContainerInit()){
            Log.d("Fragment_Home", "Skip onResume")
            super.onResume()
            return
        }

        loadWatchlist()

        loadGenres()

        addRandomSeries()

        addWatchlist()

        addGenreChooser()

        for (genre in genres){
            addGenre(genre, true)
        }

        super.onResume()
    }

    override fun onPause() {
        Log.d("Fragment_Home", "onPause")

        if(containerLayout.isEmpty()){
            Log.d("Fragment_Home", "Skip onPause")
            super.onPause()
            return
        }

        randomSeries.stop()

        containerLayout.removeAllViews()

        super.onPause()
    }

    //Manage variables
    private fun loadWatchlist(){
        Log.d("Fragment_Home", "Load Watchlist")

        val dbWatchlist = MainActivity.database().watchlistDao()

        watchlist.clear()

        for (stream in dbWatchlist.getWatchlist()){
            val displayStream = DisplayStream(stream.name, stream.cover, stream.url)
            watchlist.add(displayStream)
        }
    }

    private fun loadGenres(){
        Log.d("Fragment_Home", "Load Genres")

        val dbGenres = MainActivity.database().genresDao()

        genres.clear()

        for (genre in dbGenres.getGenres()){
            genres.add(genre.genre)
        }
    }

    //Load views
    private fun addRandomSeries(){
        Log.d("Fragment_Home", "Add random series")

        val allStreams = MainActivity.streamContainer().allSeries

        randomSeries = RandomSeries(containerLayout.context, allStreams)

        containerLayout.addView(randomSeries)

        randomSeries.start()
    }

    private fun addWatchlist(){
        Log.d("Fragment_Home", "Add watchlist")

        if(watchlist.isEmpty()){
            return
        }

        val view = SeriesContainer(containerLayout.context,containerLayout, null, null, watchlist.asReversed(), "Watchlist", false)

        containerLayout.addView(view)
    }

    fun addGenre(genre: Genre, beforeLast: Boolean){
        Log.d("Fragment_Home", "Add genre: ${genre.genreName}")

        val genreStreams = if(MainActivity.streamContainer().genreMap?.get(genre)?.size!! > 30){
            MainActivity.streamContainer().genreMap?.get(genre)?.subList(0, 30)
        }else{
            MainActivity.streamContainer().genreMap?.get(genre)
        }

        val view = SeriesContainer(containerLayout.context, containerLayout, genre, genreSelector, genreStreams!!, "Genre: ${genre.genreName}", true)

        val size = containerLayout.size

        if(beforeLast){
            containerLayout.addView(view, size -1)
        }else{
            containerLayout.addView(view)
        }
    }

    private fun addGenreChooser(){
        genreSelector = GenreSelector(containerLayout.context, this)

        containerLayout.addView(genreSelector)
    }
}