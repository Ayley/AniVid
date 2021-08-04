package me.kleidukos.anicloud.room.watchlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.kleidukos.anicloud.models.anicloud.Genre
import java.util.List

@Entity
data class RoomWatchlist(
    @PrimaryKey val uId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cover") val cover: String?
)
