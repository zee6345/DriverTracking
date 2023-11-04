package com.app.drivertracking.data.cache

import android.content.Context
import android.content.ContextWrapper
import com.app.drivertracking.data.models.BusModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs

object AppPreference {

    fun Preference(context: Context) {
        Prefs.Builder()
            .setContext(context)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(context.packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    fun putString(key: String, value: String) {
        Prefs.putString(key, value)
    }

    fun getString(key: String): String {
        return Prefs.getString(key) ?: ""
    }

    fun putInt(key: String, value: Int) {
        Prefs.putInt(key, value)
    }

    fun getInt(key: String): Int {
        return Prefs.getInt(key, 0)
    }

    fun putBoolean(key: String, value: Boolean) {
        Prefs.putBoolean(key, value)
    }

    fun getBoolean(key: String): Boolean {
        return Prefs.getBoolean(key, false)
    }

    fun setToken(token: String) {
        Prefs.putString("token", token)
    }

    fun getToken(): String {
        return Prefs.getString("token", "")
    }

    fun clear(){
        Prefs.clear()
    }

    fun getBusDetails():BusModel?{
        val dataModel = Prefs.getString("BusDetails","")
        return if (dataModel.isNotEmpty()){
            val gson = Gson()
            val savedUserProfile = gson.fromJson(dataModel, BusModel::class.java)
            savedUserProfile
        }else{
            null
        }
    }

    fun setBusDetails(valueBody: BusModel){
        val gson = Gson()
        val userProfileJson = gson.toJson(valueBody)
        Prefs.putString("BusDetails",userProfileJson)
    }
}