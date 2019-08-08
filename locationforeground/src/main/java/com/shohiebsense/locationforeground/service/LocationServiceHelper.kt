package com.shohiebsense.locationforeground.service

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shohiebsense.locationforeground.LocationResultObservable
import com.shohiebsense.locationforeground.FusedLocationResult
import java.util.*
import android.content.Intent
import android.os.Handler


class LocationServiceHelper(
    var listener: LocationServiceListener,
    val activity: AppCompatActivity) : Observer {

    private val fusedLocationResult = FusedLocationResult(activity)
    var isBound = false
    var service : LocationService? = null
    var receiver : LocationUpdateBroadcastReceiver = LocationUpdateBroadcastReceiver()

    companion object {
        val REQUEST_CODE_PERMISSION = 33
    }

    val isLocationPermissionGranted: Boolean
        get() {
            val permissionState = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            return permissionState == PackageManager.PERMISSION_GRANTED
        }


    init {
        val permissionList = arrayListOf<String>()
        permissionList.add(getLocationPermissionList())
        if (permissionList.isNotEmpty() && permissionList.get(0).isNotEmpty()) {
            doRequestLocationPermission(permissionList)
        }
        LocationResultObservable.instance.addObserver(this)
    }


    fun doRequestLocationPermission(permissions: List<String>) {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(), REQUEST_CODE_PERMISSION
        )
    }

    fun getLocationPermissionList(): String {
        return if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            Manifest.permission.ACCESS_FINE_LOCATION
        } else ""
    }


    val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, serviceBinder: IBinder) {
            val binder = serviceBinder as LocationService.LocalBinder
            service = binder.service
            isBound = true
            if(isLocationPermissionGranted){
                Handler().postDelayed({ service!!.requestLocationUpdates() }, 2000)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
            isBound = false
        }
    }

    fun onStart(){
        activity.bindService(Intent(activity, LocationService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)

    }

    fun onResume(){
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, IntentFilter(
            LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES))
    }

    fun onPause(){
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver)
    }

    fun onStop(){
        if(isBound){
            activity.unbindService(mServiceConnection)
            isBound = false
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
        listener.onGettingLocation(false, "${fusedLocationResult.getDate()} ${fusedLocationResult.getTime()}", fusedLocationResult.getLatitude(), fusedLocationResult.getLongitude())
    }


    interface LocationServiceListener {
        fun onGettingLocation(isImmediateExit : Boolean, time: String, latitude: String, longitude: String)
    }


}