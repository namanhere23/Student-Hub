package com.namangulati.studenthub.models

import java.io.Serializable

data class UserDetailsModel(
    var name: String?=null,
    var email: String?=null,
    var mobile: String?=null,
    var photo: String? = null,
    var uid: String?=null,
    var groups: ArrayList<String> = arrayListOf(),
    var status:String?=null
) : Serializable
