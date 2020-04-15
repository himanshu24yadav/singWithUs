package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context

fun Context.fetchString(stringId:Int) : String {
    return this.resources.getString(stringId)
}