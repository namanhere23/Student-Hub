package com.namangulati.studenthub.models

import java.io.Serializable

data class ContactsModel(
    val name: String,
    val email: String,
    val uid:String,
    val mobile:String,
    val url:String,
    var time: Long=0L
) :Serializable
