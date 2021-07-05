package me.kleidukos.anicloud.models

import me.kleidukos.anicloud.enums.Language
import java.io.Serializable

data class Hoster(val name: String, val streams: HashMap<Language, Int>): Serializable
