package com.shohiebsense.loclib

interface LocationResultCallback {
    fun onGettingLocation(latitude : String, longitude : String, time : String)
}