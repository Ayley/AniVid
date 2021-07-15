package me.kleidukos.anicloud.models.anicloud

import java.io.Serializable

data class Season(val name: String, val season: Int, val episodes: List<Episode>) : Serializable