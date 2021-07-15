package me.kleidukos.anicloud.room.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uId: Int,
    @ColumnInfo(name = "session_id") val id: String
)