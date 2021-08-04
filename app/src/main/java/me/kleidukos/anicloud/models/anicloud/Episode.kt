package me.kleidukos.anicloud.models.anicloud

import java.io.Serializable

data class Episode(
    val season: Int,
    val episode: Int,
    val title_english: String,
    val title_german: String?,
    val episode_url: String,
    val description: String?,
    val poster: String?,
    val languages: List<Language>?,
    val seen: Boolean
) : Serializable {

}
