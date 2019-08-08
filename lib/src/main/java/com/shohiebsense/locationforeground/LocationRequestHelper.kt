package com.shohiebsense.locationforeground

import android.content.Context

class LocationRequestHelper {

   companion object{
       fun setRequesting(context: Context, isRequesting: Boolean){
           PreferenceManager(context).saveIsRequesting(isRequesting)
       }

       fun getIsRequesting(context: Context): Boolean{
           return PreferenceManager(context).getIsRequesting()
       }
   }

}