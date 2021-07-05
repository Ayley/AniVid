package me.kleidukos.anicloud.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM RoomDisplayStream")
    fun getWatchlist(): List<RoomDisplayStream>

    @Insert
    fun insertWatchlist(roomDisplayStream: RoomDisplayStream)

    @Delete
    fun removeWatchlist(roomDisplayStream: RoomDisplayStream)
}