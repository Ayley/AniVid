package me.kleidukos.anicloud.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GenreDao {

    @Query("SELECT * FROM roomgenre")
    fun getGenres(): List<RoomGenre>

    @Insert
    fun insertGenre(roomGenre: RoomGenre)

    @Delete
    fun removeGenre(roomGenre: RoomGenre)

}