package me.kleidukos.anicloud.models.anicloud

import java.io.Serializable

data class Redirect(
    val season: Int,
    val episode: Int,
    val name: String,
    val language: Language,
    val redirectId: Int
) : Serializable
