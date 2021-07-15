package me.kleidukos.anicloud.models.anicloud

data class Episode(
    val season: Int,
    val episode: Int,
    val url: String,
    val titleGerman: String?,
    val titleEnglish: String,
    val poster: String?,
    val description: String?,
    val seen: Boolean,
    val providers: List<Redirect>
)
