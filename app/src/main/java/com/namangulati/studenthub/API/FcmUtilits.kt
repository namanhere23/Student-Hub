package com.namangulati.studenthub.API

import android.util.Log
import com.namangulati.studenthub.models.NotificationBody
import com.namangulati.studenthub.models.SentMessageDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FcmUtilits {

    private val api: FcmApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.FCM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FcmApi::class.java)
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun sendMessageWithToken(token: String, messageText: String) {
        coroutineScope.launch {
            val messageDTO = SentMessageDTO(
                to = token,
                notification = NotificationBody(
                    title = "New Message!",
                    body = messageText
                )
            )

            try {
                api.sendMessage(messageDTO)
            } catch (e: Exception) {
                Log.e("Error", "Error sending message: ${e.message}", e)
            }
        }
    }

    fun sendBroadcastMessage(messageText: String) {
        coroutineScope.launch {
            val messageDTO = SentMessageDTO(
                to = "/topics/all",
                notification = NotificationBody(
                    title = "New Broadcast!",
                    body = messageText
                )
            )
            try {
                api.broadcast(messageDTO)
            } catch (e: Exception) {
                Log.e("FcmUtilits", "Error sending broadcast: ${e.message}", e)
            }
        }
    }
}
