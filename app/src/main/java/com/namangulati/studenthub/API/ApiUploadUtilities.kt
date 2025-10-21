package com.namangulati.studenthub.API

import android.content.res.AssetManager
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileInputStream
import java.util.Properties

object ApiUploadUtilities {

    private val props = Properties()
    fun initProperties(assetManager: AssetManager) {
        val inputStream = assetManager.open("local.properties")
        props.load(inputStream)
    }

    fun getApiInterface():ApiUploadInterface{
        Log.d("Hello3","Hello3")
        return Retrofit.Builder()
            .baseUrl("\"${props["BASE_URL"]}\"")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiUploadInterface::class.java)
    }
}