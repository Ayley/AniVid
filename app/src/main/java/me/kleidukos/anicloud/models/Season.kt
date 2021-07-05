package me.kleidukos.anicloud.models

import me.kleidukos.anicloud.models.Episode
import java.io.Serializable

data class Season(val name: String,val seasonId: Int, val episodes: List<Episode>): Serializable
