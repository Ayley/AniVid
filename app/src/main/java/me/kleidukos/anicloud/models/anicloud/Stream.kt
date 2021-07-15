package me.kleidukos.anicloud.models.anicloud

import java.io.Serializable

data class Stream(
    val title: String,
    val url: String,
    val poster: String,
    val description: String,
    val trailer: String?,
    val genres: List<Genre>,
    val seasons: List<Season>
) : Serializable
