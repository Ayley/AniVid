package me.kleidukos.anicloud.room.watchlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.kleidukos.anicloud.room.watchlist.RoomWatchlist

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM RoomWatchlist")
    fun getWatchlist(): List<RoomWatchlist>

    @Insert
    fun insertWatchlist(roomWatchlist: RoomWatchlist)

    @Delete
    fun removeWatchlist(roomWatchlist: RoomWatchlist)
}