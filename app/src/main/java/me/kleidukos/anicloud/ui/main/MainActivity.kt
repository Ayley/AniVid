package me.kleidukos.anicloud.ui.main

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuInflater
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.room.AppDatabase
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.util.EndPoint

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: ImageButton
    private lateinit var menuButton: ImageButton
    private var isHomeAtFront = true

    companion object {
        private lateinit var database: AppDatabase

        private var newList: List<RoomDisplayStream>? = mutableListOf()
        private var popularList: List<RoomDisplayStream>? = mutableListOf()

        fun database(): AppDatabase {
            return database
        }

        fun getAllAnime(): List<RoomDisplayStream> {
            return database.seriesDao().getDisplayStreams()
        }

        fun getNewAnime(): List<RoomDisplayStream>? {
            if (newList?.isEmpty() == true) {
                newList = EndPoint.getNew()
            }
            return newList
        }

        fun getPupularAnime(): List<RoomDisplayStream>? {
            if (popularList?.isEmpty() == true) {
                popularList = EndPoint.getPopular()
            }
            return popularList
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildPolicy()

        searchButton = findViewById(R.id.search_button)
        menuButton = findViewById(R.id.menu_button)

        showPopup()

        setupButtons()

        loadDatabase()
    }

    /*private var started = false
    override fun onResume() {
        super.onResume()
        if(started){
            GlobalScope.launch(Dispatchers.Default) {
                val type = object : TypeToken<List<RoomDisplayStream>>() {}.type
                val streams = Gson().fromJson<List<RoomDisplayStream>>(EndPoint.getAll(), type)

                for (stream in streams){
                    if(!database.seriesDao().contains(stream.sid)){
                        database.seriesDao().insertDisplayStreams(stream)
                    }
                }
            }
        }
        started = true
    }*/

    private fun setupButtons() {
        searchButton.setOnClickListener {
            if (isHomeAtFront) {
                findNavController(R.id.fragment_container).navigate(R.id.navigation_search)
                isHomeAtFront = false
            }
        }
    }

    private fun loadDatabase() {
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AniCloud")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    private fun buildPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    fun showPopup() {
        menuButton.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.setOnMenuItemClickListener {
                if (isHomeAtFront) {
                    findNavController(R.id.fragment_container).navigate(R.id.navigation_settings)
                    isHomeAtFront = false
                }
                true
            }
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.bottom_nav_menu, popup.menu)
            popup.show()
        }
    }

    override fun onBackPressed() {
        if (isHomeAtFront) {
            return
        } else {
            isHomeAtFront = true
        }
        super.onBackPressed()
    }
}