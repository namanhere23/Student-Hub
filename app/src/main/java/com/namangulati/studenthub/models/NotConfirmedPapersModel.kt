package com.namangulati.studenthub.models

import java.io.Serializable

data class NotConfirmedPapersModel(
    var subject: String? = null,
    var semester: Int? = null,
    var year: Int? = null,
    var exam: String? = null,
    var link: String? = null,
    var email:String?= null,
    var key:String?=null
) : Serializable
