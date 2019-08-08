package com.shohiebsense.locationforeground

import android.util.Log
import java.util.*

class LocationResultObservable : Observable() {

    companion object{
        var instance = LocationResultObservable()
    }


    fun updateValue(data : Any){
        synchronized(this){
            setChanged()
            notifyObservers(data)
        }
    }

    fun updateValue(){
        synchronized(this){
            setChanged()
            notifyObservers()
        }
    }

}