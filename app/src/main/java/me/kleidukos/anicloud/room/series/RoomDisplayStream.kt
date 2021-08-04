package me.kleidukos.anicloud.room.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.kleidukos.anicloud.models.anicloud.Genre
import java.io.Serializable
import java.util.List

@Entity
data class RoomDisplayStream(
    @PrimaryKey val title: String,
    @ColumnInfo(name = "altName") val altTitle: String?,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "poster") val poster: String,
    @ColumnInfo(name = "genres") val genres: List<Genre>?
): Serializable{}
