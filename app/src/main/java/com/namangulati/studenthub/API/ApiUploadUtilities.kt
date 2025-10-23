package com.namangulati.studenthub.API

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUploadUtilities {
    fun getApiInterface():ApiUploadInterface{
        Log.d("Hello3",Constants.BASE_URL)
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiUploadInterface::class.java)
    }
}