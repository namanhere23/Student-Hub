package com.namangulati.studenthub.userPages

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namangulati.studenthub.models.PapersModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils

class DashboardViewModel  : ViewModel() {
    private val _papers = MutableLiveData<List<PapersModel>>()
    val papers: LiveData<List<PapersModel>> get() = _papers

    fun loadPapers(context: Context) {
        if (_papers.value != null) return
        FirebasePapersDatabaseUtils.loadAllPapers(context) { papers ->
            _papers.value = papers
        }
    }
}