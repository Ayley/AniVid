package me.kleidukos.anicloud.components

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.activities.MainActivity
import me.kleidukos.anicloud.enums.Genre
import me.kleidukos.anicloud.room.RoomDisplayStream
import me.kleidukos.anicloud.room.RoomGenre
import me.kleidukos.anicloud.ui.home.HomeFragment

class GenreSelector : LinearLayout {

    private var genres: Spinner
    private var addButton: Button

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.series_container, this)

        genres = findViewById(R.id.genre_selector)
        addButton = findViewById(R.id.genre_add_button)
    }

    constructor(
        context: Context,
        homeFragment: HomeFragment
    ) : super(context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.genre_chooser, this)

        genres = findViewById(R.id.genre_selector)
        addButton = findViewById(R.id.genre_add_button)

        loadGenreSpinner()

        setAddButtonOnClick(homeFragment)
    }

    //Load components
    fun loadGenreSpinner() {
        genres.background.setColorFilter(
            resources.getColor(R.color.head_background),
            PorterDuff.Mode.OVERLAY
        )

        val genreNames: MutableList<String> = mutableListOf()

        val roomGenres = MainActivity.database().genresDao().getGenres()

        for (genre in Genre.values()) {
            if (genre == Genre.ALL || genre == Genre.POPULAR || genre == Genre.MAGICAL_GIRL || roomGenres.any {it.genre.genreName.equals(genre.genreName,true)}) {
                continue
            }
            genreNames.add(genre.genreName)
        }

        genres.adapter = ArrayAdapter(context, R.layout.spinner_item, genreNames)
    }

    private fun setAddButtonOnClick(homeFragment: HomeFragment) {
        addButton.setOnClickListener {
            val genreName = genres.selectedItem as String

            val genre = getGenreByName(genreName)

            val roomGenreDao = MainActivity.database().genresDao().getGenres()

            val id = roomGenreDao.size

            val roomGenre = if (id == 0) {
                RoomGenre(0, genre)
            } else {
                RoomGenre(id, genre)
            }

            MainActivity.database().genresDao().insertGenre(roomGenre)

            if (genre != Genre.ALL) {
                homeFragment.addGenre(genre, true)
            }

            loadGenreSpinner()
        }
    }

    //functions
    private fun getGenreByName(name: String): Genre {
        for (genre in Genre.values()) {
            if (genre.genreName.equals(name, true)) {
                return genre
            }
        }
        return Genre.ALL
    }
}