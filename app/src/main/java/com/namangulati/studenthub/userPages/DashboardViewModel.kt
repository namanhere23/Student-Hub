package com.namangulati.studenthub.userPages

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.namangulati.studenthub.Database.PapersRepository
import com.namangulati.studenthub.modelsRoom.PapersModelRoom
import kotlinx.coroutines.launch

class DashboardViewModel(private val repo: PapersRepository)  : ViewModel() {
    private val _papers = MutableLiveData<List<PapersModelRoom>>()
    val papers: LiveData<List<PapersModelRoom>> = repo.getLocalPapers().asLiveData()

    fun loadPapers() {
        viewModelScope.launch {
            repo.syncPapersFromFirebase()
        }
    }

    companion object {
        class Factory(private val context: Context) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    val repo = PapersRepository(context.applicationContext)
                    return DashboardViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}