package com.shohiebsense.locationforeground.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.shohiebsense.locationforeground.R
import com.shohiebsense.locationforeground.LocationRequestHelper
import java.lang.Exception
import android.content.pm.ApplicationInfo



class LocationService : Service() {

    companion object {
        val PACKAGE_NAME = "com.shohiebsense.locationforegrounde"
        val TAG = LocationService::class.java.simpleName
        val UPDATE_INTERVAL_IN_MS = 10000L
        val FASTEST_UPDATE_INTERVAL_IN_MS = UPDATE_INTERVAL_IN_MS / 2
        val KEY_NOTIFICATION_ID = 12345678
        var APP_NAME = ""
        val INTENT_NOTIFICATION_CHANNEL_ID = "channel_01"
        val INTENT_KEY_IS_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.IS_STARTED_FROM_NOTIFICATION"
        val INTENT_NOTIFICATION_REQUEST_CODE = 0

    }

    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private lateinit var mServiceHandler: Handler
    private var mLocation: Location? = null
    private val binder = LocalBinder()
    private var isConfigurationChanged = false
    lateinit var notificationManager : NotificationManager

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onGettingLocation(locationResult!!.lastLocation)
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(INTENT_NOTIFICATION_CHANNEL_ID, APP_NAME, NotificationManager.IMPORTANCE_LOW)
            mChannel.enableVibration(false)
            notificationManager.createNotificationChannel(mChannel)
        }

    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.getLastLocation()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.e(TAG, "Failed to get location.")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Lost location permission.$e")
        }

    }



    fun createLocationRequest(){
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(UPDATE_INTERVAL_IN_MS)
        mLocationRequest!!.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MS)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(){

        LocationRequestHelper.setRequesting(this, true)
        startService(Intent(applicationContext, LocationService::class.java))

        try {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: Exception) {
            LocationRequestHelper.setRequesting(this, false)
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    fun removeLocationUpdates(){
        try{
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
            LocationRequestHelper.setRequesting(this, false)
            stopSelf()
        }catch (e: Exception){
            LocationRequestHelper.setRequesting(this, true)
            Log.e("shohiebsense ","failed to remove")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Tells the system to not try to recreate the service after it has been killed.
        var isStartedFromNotification = intent!!.getBooleanExtra(INTENT_KEY_IS_STARTED_FROM_NOTIFICATION, false)

        if(isStartedFromNotification){
            removeLocationUpdates()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun onGettingLocation(location: Location) {
        Log.e(TAG, "New location: $location")

        mLocation = location
        val intent = Intent(LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES)
        intent.putExtra(LocationUpdateBroadcastReceiver.INTENT_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        Log.e("shohiebsense ",isServiceRunningInForeground().toString() + " ")
        if(isServiceRunningInForeground()){
            notificationManager.notify(KEY_NOTIFICATION_ID, buildNotification())
        }
    }

    fun getAppName() : String{
        val applicationInfo = getApplicationInfo()
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
    }

    fun buildNotification() : Notification {
        val intent = Intent(this, LocationService::class.java)
        val text = getAppName()
        intent.putExtra(INTENT_KEY_IS_STARTED_FROM_NOTIFICATION, true)
        /*val servicePendingIntent = PendingIntent.getService(this,
            INTENT_NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)*/
        val builder = NotificationCompat.Builder(this)
            .setContentTitle(String.format(getString(R.string.notif_desc), getAppName()))
            .setOngoing(true)
            .setSmallIcon(R.drawable.round_info_white_18)
            .setTicker(text)
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.DEFAULT_VIBRATE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId(INTENT_NOTIFICATION_CHANNEL_ID)
        }
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        stopForeground(true)
        isConfigurationChanged = false
        return binder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        isConfigurationChanged = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(!isConfigurationChanged && LocationRequestHelper.getIsRequesting(this)){
            startForeground(KEY_NOTIFICATION_ID, buildNotification())
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isConfigurationChanged = true
    }

    override fun onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null)
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    //in case you wanna update notif
    fun isServiceRunningInForeground() : Boolean{
        val manager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)){
            Log.e("shohiebsenes ","javaclass name "+javaClass.name + "  "+service.service.className+"  "+(javaClass.name == service.service.className)+ "  "+(service.foreground))
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }
}