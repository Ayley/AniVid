package me.kleidukos.anicloud.room.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.kleidukos.anicloud.models.anicloud.Genre
import java.io.Serializable

@Entity
data class RoomDisplayStream(
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "cover") val poster: String?,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "genres") val genres: List<Genre>
): Serializable{
    @PrimaryKey(autoGenerate = true) var uId: Int = 0
}
