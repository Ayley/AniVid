package me.kleidukos.anicloud.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DisplayContainerDao {

    @Query("SELECT * FROM RoomDisplayStream")
    fun getDisplayStreams(): List<RoomDisplayStream>

    @Insert
    fun insertDisplayStreams(roomDisplayStream: RoomDisplayStream)

    @Delete
    fun removeWatchlist(roomDisplayStream: RoomDisplayStream)

}