package me.kleidukos.anicloud.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.coroutines.runBlocking
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.databinding.ActivityMainBinding
import me.kleidukos.anicloud.datachannel.DataChannelManager
import me.kleidukos.anicloud.models.DisplayStreamContainer
import me.kleidukos.anicloud.room.AppDatabase
import me.kleidukos.anicloud.room.User
import me.kleidukos.anicloud.datachannel.IDataChannel as IDataChannel

class MainActivity : AppCompatActivity(), IDataChannel {

    private lateinit var binding: ActivityMainBinding

    private lateinit var dataChannelManager: DataChannelManager

    private lateinit var database: AppDatabase

    private var displayStreamContainer: DisplayStreamContainer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataChannelManager = DataChannelManager(this)

        buildPolicy()

        loadNavigation()

        loadChannels()

        val intent = Intent(applicationContext, SplashScreenActivity::class.java)

        startActivity(intent)

        loadDatabase()

        if (database.userDao().getUser() != null) {
            DataChannelManager.dataStoreInChannel("StreamView", database.userDao().getUser())
        }
    }

    private fun loadDatabase() {
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "AniCloud")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    private fun loadNavigation() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val navView: BottomNavigationView = binding.navView

        navView.bringToFront()

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun loadChannels() {
        val fragments =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val home = fragments.childFragmentManager.fragments[0]

        dataChannelManager.addChannel("Home", home as IDataChannel)
    }

    private fun buildPolicy() {
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun <T> onDataReceived(data: T) {
        if (data is DisplayStreamContainer) {
            displayStreamContainer = data
            DataChannelManager.sendData("Home", data)
        }
        if(data is String){
            val user = User(0, data)
            database.userDao().insertUser(user)
            DataChannelManager.dataStoreInChannel("StreamView", user)
        }
    }
}