package com.namangulati.studenthub.modelsRoom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PapersModelRoom(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subject: String?,
    val semester: Int?,
    val year: Int?,
    val exam: String?,
    val link: String?
)
