package me.kleidukos.anicloud.room.series

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.kleidukos.anicloud.room.series.RoomDisplayStream

@Dao
interface DisplayContainerDao {

    @Query("SELECT * FROM RoomDisplayStream")
    fun getDisplayStreams(): List<RoomDisplayStream>

    @Query("DELETE FROM RoomDisplayStream")
    fun clear()

    @Insert
    fun insertDisplayStreams(roomDisplayStream: RoomDisplayStream)

    @Delete
    fun removeDisplayStream(roomDisplayStream: RoomDisplayStream)

}