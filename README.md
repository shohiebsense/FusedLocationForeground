# FusedLocationForeground
Running desperately FusedLocation on Foreground.

...

Usage

Of course you have to add ACCESS_FINE_LOCATION permission

```
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 ```
  
 also if you want it to run on background. Add this in AndroidManifest.xml inside application.
 
 ```
    <receiver android:name="com.shohiebsense.loclib.service.LocationUpdateBroadcastReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="com.shohiebsense.loclib.action.ACTION_PROCESS_UPDATES" />
            </intent-filter>
        </receiver>
 ```
In your activity file, implement LocationService.LocationServiceListener :

```
    lateinit var locationServiceHelper : LocationServiceHelper

   override fun onCreate(savedInstanceState: Bundle?) {
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
        //this is called when the fused location service updates according to its interval..
     }


 ```
 
 
 
