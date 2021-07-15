package me.kleidukos.anicloud.models.anicloud

import java.io.Serializable

enum class Language(var language: String, val id: Int): Serializable {

    GERMAN("Deutsch", 1),
    JAPANESE_ENGLISH("EngSub", 2),
    JAPANESE_GERMAN("GerSub", 3),
    NONE("Kein Sprache", 4);

    companion object {
        fun getById(id: Int): Language {
            for (language: Language in values()) {
                if (language.id == id) {
                    return language
                }
            }
            return null!!
        }

        fun getByName(string: String): Language {
            for (language: Language in values()) {
                if (language.language.equals(string, true)) {
                    return language
                }
            }
            return null!!
        }

        fun getBySrc(string: String): Language {
            return when (string) {
                "/public/img/german.svg" -> GERMAN
                "/public/img/japanese-german.svg" -> JAPANESE_GERMAN
                "/public/img/japanese-english.svg" -> JAPANESE_ENGLISH
                else -> NONE
            }
        }
    }
}