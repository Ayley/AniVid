package me.kleidukos.anicloud.models

import java.io.Serializable

data class Stream(val name: String, val description: String, val thumbnailUrl: String, val trailerUrl: String, val seasons: Map<String, String>): Serializable
