package me.kleidukos.anicloud.models

import java.io.Serializable

data class DisplayStream(val name: String, val cover: String, val url: String): Serializable{

    override fun equals(other: Any?): Boolean {
        if(other !is DisplayStream){
            return false
        }

        if(!name.equals(other.name, true)){
            return false
        }

        if(!cover.equals(other.cover, true)){
            return false
        }

        if(!url.equals(other.url, true)){
            return false
        }

        return true
    }

}
