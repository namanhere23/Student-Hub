package com.namangulati.studenthub.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionsUtils {
    private fun hasExternalStorage(activity: Activity) =
        ActivityCompat.checkSelfPermission(activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED

    fun requestExternalStoragePermission(activity: Activity) {
        if (!hasExternalStorage(activity)) {
            ActivityCompat.requestPermissions(activity,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
        }
    }


    private fun hasLocationPermission(activity: Activity) =
        ActivityCompat.checkSelfPermission(activity,
            android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    fun requestLocationPermission(activity: Activity) {
        if (!hasLocationPermission(activity)) {
            ActivityCompat.requestPermissions(activity,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
        }
    }
}