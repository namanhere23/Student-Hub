package com.namangulati.studenthub.models

import java.io.Serializable

data class PapersModel(
    var subject: String? = null,
    var semester: Int? = null,
    var year: Int? = null,
    var exam: String? = null,
    var link: String? = null
) : Serializable
