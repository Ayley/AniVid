package me.kleidukos.anicloud.util

import android.content.Context
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.kleidukos.anicloud.models.github.GithubRelease

class GitHub {

    companion object{
        private const val updateUrl = "https://api.github.com/repos/Ayley/AniVid/releases"
        private const val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36 OPR/77.0.4054.172"

        fun getUpdate(context: Context): Pair<Boolean, String>{
            val httpGet = Fuel.get(updateUrl).header("UserAgent", userAgent)

            val result = httpGet.responseString()

            when(result.third){
                is Result.Failure -> {
                    return Pair(false, "")
                }
                is Result.Success -> {
                    val release = Json { ignoreUnknownKeys = true; coerceInputValues = true}.decodeFromString<List<GithubRelease>>(result.third.get())[0]

                    val githubReleaseVersion = release.tag_name.replace(".","").toInt()

                    val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName.replace(".", "").toInt()

                    Log.d("GitHub_Update", "Github Version: $githubReleaseVersion | current version: $currentVersion")

                    if(githubReleaseVersion > currentVersion){
                        return Pair(true, release.assets.first().browser_download_url!!)
                    }
                }
            }
            return Pair(false, "")
        }
    }
}