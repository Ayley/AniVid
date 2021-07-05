package me.kleidukos.anicloud.models

import me.kleidukos.anicloud.enums.Genre

data class DisplayStreamContainer(val genreMap: Map<Genre, List<DisplayStream>>, val allSeries: List<DisplayStream>)