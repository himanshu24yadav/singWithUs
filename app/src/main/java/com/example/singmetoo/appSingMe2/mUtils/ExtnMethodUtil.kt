package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Context.fetchString(stringId:Int) : String {
    return this.resources.getString(stringId)
}

fun Context.fetchDimen(dimenId:Int) : Int {
    return this.resources.getDimensionPixelSize(dimenId)
}

fun FragmentManager.addFragment(fragment:Fragment,container:Int,backStackName:String? = null) {
    if(backStackName == null){
        this.beginTransaction().add(container,fragment).commit()
    }
    else {
        this.beginTransaction().add(container,fragment).addToBackStack(backStackName).commit()
    }
}

fun TextView.setProfileName(username:String?) {
    this.text = "Hi ${AppUtil.getFirstName(username)}"
}