package com.namangulati.studenthub.models

import java.io.Serializable

data class Message(
    val message: String? = null,
    val senderId: String? = null
) : Serializable
