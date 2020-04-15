package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context
import android.widget.Toast

class AppUtil {

    companion object {
        fun showToast(ctx: Context,msg:String) {
            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()
        }

        fun getFirstName(name:String?) : String?{
           var firstName: String? = ""
           firstName = name?.let { name.substring(0,name.lastIndexOf(' ')) }
           return firstName
        }
    }

}