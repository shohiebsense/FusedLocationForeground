package com.shohiebsense.locationforegroundsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shohiebsense.locationforeground.service.LocationServiceHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),LocationServiceHelper.LocationServiceListener {

    lateinit var locationServiceHelper : LocationServiceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationServiceHelper = LocationServiceHelper(this, this)
    }

    override fun onStart() {
        super.onStart()
        locationServiceHelper.onStart()
    }

    override fun onPause() {
        locationServiceHelper.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        locationServiceHelper.onResume()
    }

    override fun onStop() {
        locationServiceHelper.onStop()
        super.onStop()
    }

    override fun onGettingLocation(isImmediateExit: Boolean, time: String, latitude: String, longitude: String) {
        Log.e("shohiebsense ","lat "+latitude+" long "+longitude)
        text_location.text = "Time: $time\nlat: $latitude\nlongitude: $longitude"
    }

}
