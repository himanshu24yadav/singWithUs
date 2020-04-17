package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Context.fetchString(stringId:Int) : String {
    return this.resources.getString(stringId)
}

fun FragmentManager.addFragment(fragment:Fragment,container:Int,backStackName:String? = null) {
    if(backStackName == null){
        this.beginTransaction().add(container,fragment).commit()
    }
    else {
        this.beginTransaction().add(container,fragment).addToBackStack(backStackName).commit()
    }
}