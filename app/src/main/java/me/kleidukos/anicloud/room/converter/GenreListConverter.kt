package me.kleidukos.anicloud.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.kleidukos.anicloud.models.anicloud.Genre
import java.lang.reflect.Type
import java.util.*


class GenreListConverter {

    var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<Genre>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object : TypeToken<List<Genre>?>() {}.type
        return gson.fromJson<List<Genre>>(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Genre>?): String? {
        return gson.toJson(someObjects)
    }
}