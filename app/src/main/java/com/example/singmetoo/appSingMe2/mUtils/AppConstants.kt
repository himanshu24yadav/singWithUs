package com.example.singmetoo.appSingMe2.mUtils

import android.Manifest

class AppConstants {

    companion object {

        const val RC_SIGN_IN = 100
        const val PERMISSION_REQUEST_CODE = 101

        const val DEFAULT_FRAGMENT_TAG = "DEFAULT_FRAG"
        const val HOME_FRAGMENT_TAG = "HOME_FRAG"
        const val MUSIC_LIBRARY_FRAGMENT_TAG = "MUSIC_LIBRARY_FRAG"

        //permissions
        const val PERMISSION_WRITE_STORAGE: String = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val PERMISSION_CAMERA: String = Manifest.permission.CAMERA

        //table_names
        const val TABLE_SONGS_FROM_DEVICE = "songs_from_device"
    }
}