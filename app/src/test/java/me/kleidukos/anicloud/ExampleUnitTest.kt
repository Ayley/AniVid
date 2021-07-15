package me.kleidukos.anicloud

import com.github.kittinunf.fuel.Fuel
import me.kleidukos.anicloud.scraping.parsing.TMDB
import org.junit.Test

import org.junit.Assert.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        println(TMDB.loadEpisodesThumbnailDescription("Fairy Tail", 3))
    }

    suspend  fun getJson(url: String) : String = suspendCoroutine {

            cont-> geturl(url)   { cont.resume(it) }
    }

    fun geturl(url: String, callback: (String) -> Unit){

        Thread {
            val (_, _, result) = Fuel.get(url)
                .responseString()
            when (result) {
                is com.github.kittinunf.result.Result.Failure -> {
                    callback("")

                }
                is com.github.kittinunf.result.Result.Success -> {
                    val data = result.get()
                    //
                    callback(data)
                }
            }
        }.start()
    }
}