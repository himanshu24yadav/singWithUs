package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment


fun Context.fetchString(stringId:Int) : String {
    return resources.getString(stringId)
}

fun Context.fetchDimen(dimenId:Int) : Int {
    return resources.getDimensionPixelSize(dimenId)
}

fun FragmentManager.addFragment(fragment:Fragment,container:Int,fragmentTag:String = AppConstants.DEFAULT_FRAGMENT_TAG,addToBackStack:Boolean = true) {
    if(!addToBackStack){
        beginTransaction().add(container,fragment,fragmentTag).commit()
    }
    else {
        beginTransaction().add(container,fragment,fragmentTag).addToBackStack(fragmentTag).commit()
    }
}

fun FragmentManager.activeFragment() : BaseFragment? {
    if (backStackEntryCount == 0) {
        return null
    }
    val tag: String? = getBackStackEntryAt(backStackEntryCount- 1).name
    return (findFragmentByTag(tag) as BaseFragment)
}

fun FragmentManager.clearBackStack(){
    for (count in 0 until backStackEntryCount){
        popBackStack()
    }
}

fun TextView.setProfileName(username:String?) {
    text = "Hi ${AppUtil.getFirstName(username)}"
}