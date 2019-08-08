package com.shohiebsense.locationforeground.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationResult
import com.shohiebsense.locationforeground.LocationResultObservable
import com.shohiebsense.locationforeground.LocationResultHelper

class LocationUpdateBroadcastReceiver : BroadcastReceiver(){

    companion object {
        val TAG = LocationUpdateBroadcastReceiver::class.java.simpleName
        val ACTION_PROCESS_UPDATES = "${LocationService.PACKAGE_NAME}.action.PROCESS_UPDATES"
        val INTENT_LOCATION =  "${LocationService.PACKAGE_NAME}.INTENT_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent == null){
            return
        }
        val action = intent.action

        Log.e("receiver ","intent action "+action.toString())
        Log.e("receiver ","extract result "+LocationResult.extractResult(intent))
        if(context == null){
            return
        }
        //#OPT 0
        var locationList = arrayListOf<Location>()
        var location : Location?  =   intent.getParcelableExtra(INTENT_LOCATION)
        if(location == null){
            Log.e("shohiebsense ","location is null")
            return
        }

        locationList.add(location)

        val locationResultHelper = LocationResultHelper(context, locationList)
        locationResultHelper.saveResult()
        Log.e("receiver ",location.latitude.toString()+"   "+location.longitude)
        LocationResultObservable.instance.updateValue(intent)
    }


}