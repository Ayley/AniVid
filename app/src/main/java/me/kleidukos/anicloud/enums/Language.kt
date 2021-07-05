package me.kleidukos.anicloud.enums

import java.io.Serializable

enum class Language(var language: String, val id: Int): Serializable {

    GERMAN("Deutsch", 1),
    JAPANESE_ENGLISH("Japanisch mit Englischem Untertitel", 2),
    JAPANESE_GERMAN("Japanisch mit Deutschem Untertitel", 3);

    companion object{
        fun getById(id: Int): Language{
            for (language: Language in values()){
                if(language.id == id){
                    return language
                }
            }
            return null!!
        }

        fun getByName(string: String): Language{
            for (language: Language in values()){
                if(language.language.equals(string, true)){
                    return language
                }
            }
            return null!!
        }
    }

}
