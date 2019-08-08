package com.shohiebsense.locationforeground

import android.content.Context
import android.location.Location
import android.util.Log
import java.sql.Date
import java.text.SimpleDateFormat

class LocationResultHelper(val context : Context, val locationList : List<Location>) {
    val preferenceManager = PreferenceManager(context)
    val CURRENT_TIMESTAMP_FORMAT = "HH:mm:ss"
    val CURRENT_DATE_FORMAT = "yyyy-MM-dd"

    fun saveResult(){
        if(locationList.isEmpty()){
            return
        }
        val location = locationList.first()
        Log.e("shohiebsense ","accuraccy "+location.hasAccuracy()+"  "+location.accuracy)


        preferenceManager.saveLatitude(location.latitude.toString())
        preferenceManager.saveLongitude(location.longitude.toString())
        preferenceManager.saveTime(longToTimeStamp(location.time, CURRENT_TIMESTAMP_FORMAT))
        preferenceManager.saveDate(longToTimeStamp(location.time, CURRENT_DATE_FORMAT))
    }

    fun isUpdatedAlready(newTime : String): Boolean{
        val oldTime = preferenceManager.getTime()
        if(oldTime.isNullOrEmpty()){
            preferenceManager.saveTime(newTime)
            return false
        }
        try{
            var oldHourTime = oldTime.split(":")[2].toInt()
            var newHourTime = newTime.split(":")[2].toInt()
            if(newHourTime - oldHourTime < 15) return true
        }catch (e : java.lang.Exception){

        }
        preferenceManager.saveTime(newTime)
        return false
    }

    fun isSimillar(newLatitude : Double, newLongitude : Double) : Boolean{
        val newLatitudeStr = newLatitude.toString()
        val oldLatitudeStr = preferenceManager.getLatitude()

        val newLongitudeStr = newLongitude.toString()
        val oldLongitudeStr = preferenceManager.getLongitude()


        if(oldLatitudeStr.isEmpty()) return false
        if(oldLongitudeStr.isEmpty()) return false

        if(newLatitudeStr.length < 8 && oldLatitudeStr.length < 8) return true
        if(newLongitudeStr.length < 8 && oldLongitudeStr.length < 8) return true


        var isLatitudeEqual = newLatitudeStr.substring(0, 7).equals(oldLatitudeStr.substring(0, 7))
        var isLongitudeEqual = newLongitudeStr.substring(0, 7).equals(oldLongitudeStr.substring(0, 7))
        if(isLatitudeEqual || isLongitudeEqual) return false

        return false
    }


    fun longToTimeStamp(timeStamp : Long, format: String) : String{
        try {
            val sdf = SimpleDateFormat(format)
            val netDate = Date(timeStamp)
            return sdf.format(netDate)
        } catch (ex: Exception) {
            Log.e("shohiebsense","exception "+ex.toString())
            return android.text.format.DateFormat.format(CURRENT_TIMESTAMP_FORMAT, java.util.Date().time).toString()
        }
    }

}