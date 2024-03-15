package com.example.agencyphase2.utils

import android.content.Context
import android.util.Log
import com.example.agencyphase2.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.delay
import java.net.URISyntaxException

class SocketHelper {
    companion object{
        var mSocket: Socket? = null

        suspend fun initSocket(){
            try {
                mSocket = IO.socket(Constants.NODE_URL)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            mSocket?.connect()

            //mSocket!!.emit("signin", PrefManager.getUserId())
            delay(10L)
            val userid = PrefManager.getUserId().toString()
            mSocket!!.emit("signin", userid)

        }

    }
}