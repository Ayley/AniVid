package me.kleidukos.anicloud.datachannel

import me.kleidukos.anicloud.models.DisplayStreamContainer
import java.util.HashMap

class DataChannelManager(main: IDataChannel) {

    fun addChannel(channelName: String, iDataChannel: IDataChannel) {
        channelMap[channelName] = iDataChannel
    }

    companion object {
        private val channelMap: MutableMap<String, IDataChannel> = HashMap()
        private val storageMap: MutableMap<String, Any> = HashMap()
        fun sendData(channelName: String, data: Any) {
            channelMap[channelName]!!.onDataReceived(data)
        }

        fun sendMainChannelData(data: Any) {
            channelMap["Main"]!!.onDataReceived(data)
        }

        fun dataChannelStorage(channelName: String): Any{
            return storageMap[channelName]!!
        }

        fun dataStoreInChannel(channelName: String, data: Any){
            storageMap.put(channelName, data)
        }
    }

    init {
        channelMap["Main"] = main
    }
}