package com.namangulati.studenthub.models

data class Groups(
    var name: String?=null,
    var photo: String? = null,
    var accessYears: ArrayList<Int> = arrayListOf()
)
