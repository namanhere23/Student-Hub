package com.namangulati.studenthub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namangulati.studenthub.models.UserDetailsModel

class LiveDataViewModel:ViewModel() {
    private val _user= MutableLiveData<UserDetailsModel>()
    val user: LiveData<UserDetailsModel> get()=_user

    fun setUser(value:UserDetailsModel){
        _user.value=value
    }
}