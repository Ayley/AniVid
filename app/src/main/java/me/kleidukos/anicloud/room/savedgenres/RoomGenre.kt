package me.kleidukos.anicloud.room.savedgenres

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.kleidukos.anicloud.models.anicloud.Genre

@Entity
data class RoomGenre(@PrimaryKey val uId: Int, @ColumnInfo(name = "genre") val genre: Genre)