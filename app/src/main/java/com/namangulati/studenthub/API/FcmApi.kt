package com.namangulati.studenthub.API

import com.namangulati.studenthub.models.SentMessageDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {
    @POST("/send")
    suspend fun sendMessage(
        @Body body:SentMessageDTO
    )

    @POST("/broadcast")
    suspend fun broadcast(
        @Body body:SentMessageDTO
    )
}