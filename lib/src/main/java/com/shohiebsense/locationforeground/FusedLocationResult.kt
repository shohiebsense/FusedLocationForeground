package com.shohiebsense.locationforeground

import android.content.Context
import com.shohiebsense.locationforeground.PreferenceManager

class FusedLocationResult(val context : Context) {
    private val preferenceManager = PreferenceManager(context)





    fun getLatitude(): String{
        return preferenceManager.getLatitude()
    }

    fun getLongitude(): String{
        return preferenceManager.getLongitude()
    }

    fun getTime() : String{
        return preferenceManager.getTime()
    }

    fun getDate() : String{
        return preferenceManager.getDate()
    }

}