package me.kleidukos.anicloud.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomDisplayStream(
    @PrimaryKey val uId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(name = "url") val url: String
)
