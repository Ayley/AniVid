package me.kleidukos.anicloud.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getUser(): User

    @Insert
    fun insertUser(user: User)

    @Delete
    fun removeUser(user: User)
}