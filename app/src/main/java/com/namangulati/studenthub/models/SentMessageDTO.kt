package com.namangulati.studenthub.models

import androidx.compose.ui.window.Notification

data class SentMessageDTO(
    val to:String?,
    val notification:NotificationBody
)

data class NotificationBody(
    val title:String,
    val body:String
)