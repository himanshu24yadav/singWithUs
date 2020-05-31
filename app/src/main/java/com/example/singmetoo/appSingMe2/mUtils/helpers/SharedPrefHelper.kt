package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.content.Context
import com.example.singmetoo.CustomApplicationClass

class SharedPrefHelper {
    companion object {

        const val SF_KEY_SKIP_LOGIN = "SF_KEY_SKIP_LOGIN"

        private val sharedPref = CustomApplicationClass.applicationContext()?.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)

        fun storeSharedPref (keyString:String,value:Any?) {
            sharedPref?.edit()?.let {
                when(value) {
                    is Int -> {
                        it.putInt(keyString,value)
                    }
                    is Boolean -> {
                        it.putBoolean(keyString,value)
                    }
                    is Float -> {
                        it.putFloat(keyString,value)
                    }
                    else -> {
                        it.putString(keyString,value.toString())
                    }
                }
                it.apply()
            } ?: return
        }

        fun getSharedPref(keyString:String,defaultValue: Any) : Any? {
            return sharedPref?.let {
                when(defaultValue) {
                    is Int -> {
                        it.getInt(keyString,defaultValue)
                    }
                    is Boolean -> {
                        it.getBoolean(keyString,defaultValue)
                    }
                    is Float -> {
                        it.getFloat(keyString,defaultValue)
                    }
                    else -> {
                        it.getString(keyString,defaultValue.toString())
                    }
                }
            } ?: defaultValue
        }

    }
}