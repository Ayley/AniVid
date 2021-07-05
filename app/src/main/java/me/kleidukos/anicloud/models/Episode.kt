package me.kleidukos.anicloud.models

import me.kleidukos.anicloud.models.Hoster
import java.io.Serializable

data class Episode(val titleGerman: String?, val titleEnglish: String?, val description: String?, val seen: Boolean, val episodeId: Int, val link: String, val hoster: List<Hoster>): Serializable
