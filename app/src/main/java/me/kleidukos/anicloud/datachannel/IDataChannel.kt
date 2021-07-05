package me.kleidukos.anicloud.datachannel

import java.io.Serializable

interface IDataChannel: Serializable {

    fun <T>onDataReceived(data: T)
}