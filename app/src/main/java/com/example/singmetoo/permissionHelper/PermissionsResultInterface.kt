package com.example.singmetoo.permissionHelper

interface PermissionsResultInterface {
    fun onPermissionResult(isAllGranted:Boolean, permissionResults:ArrayList<PermissionModel>?, requestCode:Int)
}