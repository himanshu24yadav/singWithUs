package com.example.singmetoo.appSingMe2.mBase.util

import androidx.appcompat.app.AppCompatActivity
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo

open class BaseActivity : AppCompatActivity() {
    companion object {
        var mUserInfo: AppUserInfo = AppUserInfo(userName = "User")
    }
}