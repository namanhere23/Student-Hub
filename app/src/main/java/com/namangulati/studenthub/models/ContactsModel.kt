package com.namangulati.studenthub.models

import java.io.Serializable

data class ContactsModel(
    val name: String,
    val email: String,
    val uid:String
) :Serializable
