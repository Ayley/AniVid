package me.kleidukos.anicloud.room.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.kleidukos.anicloud.room.user.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getUser(): User

    @Query("DELETE FROM user")
    fun clear()

    @Query("SELECT EXISTS(SELECT * FROM user)")
    fun isExists(): Boolean

    @Insert
    fun insertUser(user: User)

    @Delete
    fun removeUser(user: User)
}