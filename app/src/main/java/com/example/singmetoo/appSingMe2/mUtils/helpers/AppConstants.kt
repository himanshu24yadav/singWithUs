package com.example.singmetoo.appSingMe2.mUtils.helpers

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

        const val ARG_SONG_TITLE = "ARG_SONG_TITLE"
        const val ARG_SONG_ID = "ARG_SONG_ID"
        const val ARG_SONG_PATH = "ARG_SONG_PATH"
        const val ARG_SONG_ALBUM_ID = "ARG_SONG_ALBUM_ID"
        const val ARG_SONG_START_POS = "ARG_SONG_START_POS"
        const val DEFAULT_TITLE = "Unknown"
        const val DEFAULT_ARTIST = "<unknown>"
        const val SONG_TAG_KEY = 24
        const val SONG_TAG_PLAY = "SONG_TAG_PLAY"
        const val SONG_TAG_PAUSE = "SONG_TAG_PAUSE"
    }
}