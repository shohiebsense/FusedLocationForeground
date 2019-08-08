package com.shohiebsense.locationforeground

import android.content.Context

class PreferenceManager(val context: Context) {
    val sharedPreference = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()


    fun save(KEY_NAME: String, value: Int) {
        editor.putInt(KEY_NAME, value)
        commit()
    }

    fun save(KEY_NAME: String, value: String){
        editor.putString(KEY_NAME, value)
        commit()
    }

    fun save(KEY_NAME: String, value: Boolean){
        editor.putBoolean(KEY_NAME, value)
        commit()
    }

    fun saveIsRequesting(isRequesting : Boolean){
        save(Constants.KEY_IS_REQUESTING, isRequesting)
    }

    fun saveLatitude(latitude : String){
        save(Constants.KEY_LATITUDE_, latitude)
    }

    fun saveLongitude(longitude : String){
        save(Constants.KEY_LONGITUDE, longitude)
    }

    fun saveDate(time : String){
        save(Constants.KEY_DATE, time)
    }

    fun saveTime(time : String){
        save(Constants.KEY_TIME, time)
    }

    fun commit(){
        editor.commit()
    }

    fun getLatitude() : String {
        return "${sharedPreference.getString(Constants.KEY_LATITUDE_, "")}"
    }

    fun getLongitude() : String {
        return "${sharedPreference.getString(Constants.KEY_LONGITUDE, "")}"
    }

    fun getTime() : String {
        return "${sharedPreference.getString(Constants.KEY_TIME, "")}"
    }

    fun getDate() : String {
        return "${sharedPreference.getString(Constants.KEY_DATE, "")}"
    }

    fun getIsRequesting() : Boolean {
        return sharedPreference.getBoolean(Constants.KEY_IS_REQUESTING, false)
    }
}