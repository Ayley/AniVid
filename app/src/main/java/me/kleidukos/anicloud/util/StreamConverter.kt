package me.kleidukos.anicloud.util

import me.kleidukos.anicloud.models.anicloud.SimpleStream
import me.kleidukos.anicloud.room.series.RoomDisplayStream

class StreamConverter {

    companion object{
        fun convert(rds: RoomDisplayStream): SimpleStream{
            return SimpleStream(rds.title, rds.altTitle, rds.url, rds.poster, rds.description, "", rds.year, emptyList(), emptyList())
        }
    }
}