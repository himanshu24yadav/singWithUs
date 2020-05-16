package com.example.singmetoo.permissionHelper

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mUtils.AppConstants

class PermissionsManager {

    companion object {

        private var permissionCallback: PermissionsResultInterface? = null

        private fun isPermissionGranted (context:Context?,permission:String) : Boolean {
            return context?.let { ContextCompat.checkSelfPermission(it,permission) } == PackageManager.PERMISSION_GRANTED
        }

        fun checkPermissions(context:Context?,permissionList:Array<String>?,requestCode: Int = AppConstants.PERMISSION_REQUEST_CODE,permissionCallback: PermissionsResultInterface?) : Boolean{
            Companion.permissionCallback = permissionCallback
            var requestPermissionFor: Array<String>? = arrayOf()

            permissionList?.let { list ->
                for (permission in list) {
                    if(!isPermissionGranted(context, permission)){
                        requestPermissionFor = requestPermissionFor?.plusElement(permission)
                    }
                }
            }

            if(requestPermissionFor?.size!! > 0){
                ActivityCompat.requestPermissions(context as BaseActivity,requestPermissionFor!!,requestCode)
                return false
            }

            return true
        }

        fun handlePermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
            val arrayList:ArrayList<PermissionModel>? = ArrayList()
            var isAllGranted = true
            for (index in permissions.indices){
                val permissionModel =
                    PermissionModel(permission = permissions[index], permissionGranted = grantResults[index] >= 0)
                if(isAllGranted) isAllGranted = grantResults[index] >= 0
                arrayList?.add(permissionModel)
            }
            permissionCallback?.onPermissionResult(isAllGranted,arrayList,requestCode)
        }
    }
}