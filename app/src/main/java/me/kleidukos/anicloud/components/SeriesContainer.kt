package me.kleidukos.anicloud.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.activities.MainActivity
import me.kleidukos.anicloud.adapter.StreamAdapterRecycler
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.room.RoomGenre

class SeriesContainer: LinearLayout {

    private val containerName: TextView
    private val containerList: RecyclerView
    private val removeButton: ImageButton

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)

        containerName = findViewById(R.id.container_name)
        containerList = findViewById(R.id.container_list)
        removeButton = findViewById(R.id.container_remove)
    }

    constructor(
        context: Context,
        linearLayout: LinearLayout,
        genre: Genre?,
        genreSelector: GenreSelector?,
        displayStreams: List<DisplayStream>,
        title: String,
        canRemoved: Boolean
    ) : super(context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)
        containerName = findViewById(R.id.container_name)
        containerList = findViewById(R.id.container_list)
        removeButton = findViewById(R.id.container_remove)

        loadRemoveButton(canRemoved, genre, genreSelector, linearLayout)

        loadTitle(title)

        createContainer(displayStreams)
    }

    //Load components
    private fun loadRemoveButton(canRemoved: Boolean,genre: Genre?, genreSelector: GenreSelector?, linearLayout: LinearLayout) {
        if (!canRemoved) {
            removeButton.visibility = View.GONE
        } else {
            removeButton.setOnClickListener {
                var removeGenre: RoomGenre = getGenreByRoomGenres(genre!!)!!

                MainActivity.database().genresDao().removeGenre(removeGenre)

                linearLayout.removeView(this)

                genreSelector?.loadGenreSpinner()
            }
        }
    }

    private fun loadTitle(title: String) {
        if (title.isBlank()) {
            containerName.visibility = View.GONE

            setTopMargin(context)
        } else {
            containerName.text = title
        }
    }

    private fun createContainer(displayStreams: List<DisplayStream>) {
        containerList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        containerList.adapter = StreamAdapterRecycler(context, displayStreams)
    }

    //Load layout
    private fun setTopMargin(context: Context) {
        val params = containerList.layoutParams as LayoutParams

        params.topMargin = (5 / context.resources.displayMetrics.density).toInt()

        containerList.layoutParams = params
    }

    //functions
    private fun getGenreByRoomGenres(genre: Genre): RoomGenre?{
        val genresDao = MainActivity.database().genresDao().getGenres()

        for (roomGenre in genresDao){
            if(roomGenre.genre == genre){
                return roomGenre
                break
            }
        }
        return null
    }
}