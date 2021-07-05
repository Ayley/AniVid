package me.kleidukos.anicloud.enums

import java.io.Serializable

enum class Genre(val genreName: String, val url: String): Serializable {

    ADVENTURE("Abenteuer", "genre/abenteuer"),
    ACTION("Action", "genre/action"),
    ACTION_DRAMA("Actiondrama", "genre/actiondrama"),
    ACTION_COMEDY("Action Komödie", "genre/actionkomoedie"),
    EVERYDAY_LIFE("Alltagsleben", "genre/alltagsleben"),
    EVERYDAY_DRAMA("Alltagsdrama", "genre/alltagsdrama"),
    BOYSLOVE("Boys Love", "genre/boys-love"),
    DRAMA("Drama", "genre/drama"),
    ECCHI("Ecchi", "genre/ecchi"),
    ENGSUB("EngSub", "genre/engsub"),
    EROTICISM("Erotik", "genre/erotik"),
    FANTASY("Fantasy", "genre/fantasy"),
    FIGHTING_SHOUNEN("Fighting-Shounen", "genre/fighting-shounen"),
    GANBATTE("Ganbatte", "genre/ganbatte"),
    GHOST_STORIES("Geistergeschichten", "genre/geistergeschichten"),
    GER("Ger", "genre/ger"),
    GERSUB("GerSub", "genre/gersub"),
    HAREM("Harem", "genre/harem"),
    HORROR("Horror", "genre/horror"),
    COMEDY("Komödie", "genre/komoedie"),
    THRILLER("Krimi", "genre/krimi"),
    LOVE_DRAMA("Liebesdrama", "genre/liebesdrama"),
    MAGICAL_GIRL("Magical Girl", "genre/magical-girl"),
    MECHA("Mecha", "genre/mecha"),
    MYSTERY("Mystery", "genre/mystery"),
    NONSENSE_COMEDY("Nonsense Komödie", "genre/nonsense-komoedie"),
    PSYCHODRAMA("Psychodrama", "genre/psychodrama"),
    ROMANTIC_COMEDY("Romantische Komödie", "genre/romantische-komoedie"),
    ROMANCE("Romance", "genre/romanze"),
    SCIFI("SciFi", "genre/scifi"),
    SPORT("Sport", "genre/sport"),
    THRILLER2("Thriller", "genre/thriller"),
    YURI("Yuri", "genre/yuri"),
    EXCESSIVE_DISPLAY_OF_VIOLENCE("Übermäßige Gewaltdarstellung", "genre/uebermaessige-gewaltdarstellung"),


    ALL("Alle Animes", "animes"),
    NEW("Neue Animes", "neu"),
    POPULAR("Beliebte Animes", "beliebte-animes")

}