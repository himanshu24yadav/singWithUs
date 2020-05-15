package com.example.singmetoo.appSingMe2.mBase.util

import androidx.appcompat.app.AppCompatActivity
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo
import com.example.singmetoo.permissionHelper.PermissionsManager

open class BaseActivity : AppCompatActivity() {
    companion object {
        var mUserInfo: AppUserInfo = AppUserInfo(userName = "User")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionsManager.handlePermissionResult(requestCode,permissions,grantResults)
    }
}