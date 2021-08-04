package me.kleidukos.anicloud.room.series

import android.icu.text.CaseMap
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.kleidukos.anicloud.room.series.RoomDisplayStream

@Dao
interface DisplayContainerDao {

    @Query("SELECT * FROM RoomDisplayStream")
    fun getDisplayStreams(): List<RoomDisplayStream>

    @Query("SELECT * FROM RoomDisplayStream WHERE title= :title")
    fun getDisplayStream(title: String): RoomDisplayStream

    @Query("DELETE FROM RoomDisplayStream")
    fun clear()

    @Query("SELECT EXISTS(SELECT * FROM RoomDisplayStream WHERE title = :title)")
    fun contains(title: String): Boolean

    @Insert
    fun insertDisplayStreams(roomDisplayStream: RoomDisplayStream)

    @Delete
    fun removeDisplayStream(roomDisplayStream: RoomDisplayStream)
}