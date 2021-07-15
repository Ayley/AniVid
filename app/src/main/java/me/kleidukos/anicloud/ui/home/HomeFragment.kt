package me.kleidukos.anicloud.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.components.GenreSelector
import me.kleidukos.anicloud.components.RandomSeries
import me.kleidukos.anicloud.components.SeriesContainer
import me.kleidukos.anicloud.models.anicloud.Genre
import me.kleidukos.anicloud.room.series.RoomDisplayStream

class HomeFragment : Fragment() {

    private lateinit var containerLayout: LinearLayout
    private lateinit var viewGroup: ViewGroup

    private lateinit var randomSeries: RandomSeries
    private lateinit var genreSelector: GenreSelector

    private var watchlist: MutableList<RoomDisplayStream> = mutableListOf()
    private var genres: MutableList<Genre> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        viewGroup = inflater.inflate(R.layout.fragment_home, container, false) as ViewGroup

        containerLayout = viewGroup.findViewById(R.id.container_layout)

        return viewGroup
    }

    override fun onResume() {
        Log.d("Fragment_Home", "onResume")

        loadHeader()

        if(MainActivity.getAllAnime().isEmpty()){
            Log.d("Fragment_Home", "Skip onResume")
            super.onResume()
            return
        }

        loadWatchlist()

        loadGenres()

        addRandomSeries()

        addWatchlist()

        addGenreChooser()

        addGenre(Genre.NEW)
        addGenre(Genre.POPULAR)
        for (genre in genres){
            addGenre(genre)
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
            val displayStream = RoomDisplayStream(stream.name, stream.cover, stream.url, stream.genre)
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

        val allStreams = MainActivity.getAllAnime()

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

    fun addGenre(genre: Genre){
        Log.d("Fragment_Home", "Add genre: ${genre.genreName}")

        GlobalScope.launch(Dispatchers.Default){
            var removable = true

            val streams: List<RoomDisplayStream> = if(genre == Genre.NEW){
                removable = false
                MainActivity.getNewAnime()
            }else if(genre == Genre.POPULAR){
                removable = false
                MainActivity.getPupularAnime()
            }else{
                MainActivity.getAllAnime().filter { it.genres?.contains(genre)!! }
            }

            val genreStreams = if (streams.size!! > 30) {
                streams.subList(0, 30)
            } else {
                streams
            }

            val view = SeriesContainer(
                containerLayout.context,
                containerLayout,
                genre,
                genreSelector,
                genreStreams!!,
                "Genre: ${genre.genreName}",
                removable
            )

            activity?.runOnUiThread{
                containerLayout.addView(view, containerLayout.size - 1)
            }
        }
    }

    private fun addGenreChooser(){
        genreSelector = GenreSelector(containerLayout.context, this)

        containerLayout.addView(genreSelector)
    }

    private fun loadHeader(){
        activity?.findViewById<TextView>(R.id.app_title)?.visibility = View.VISIBLE
        activity?.findViewById<EditText>(R.id.searchbox)?.visibility = View.GONE
        activity?.findViewById<ImageButton>(R.id.menu_button)?.visibility = View.VISIBLE
    }
}