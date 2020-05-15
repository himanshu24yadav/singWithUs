package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.appSingMe2.mBase.pojo.SongModel

object SongsRepository {

    private val mContext:Context? = CustomApplicationClass.applicationContext()

    fun getAllSongsFromDevice() {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor: Cursor? = mContext?.contentResolver?.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        if (cursor!= null && cursor.moveToFirst()){
            val id:Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title:Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            do {
                val audioId:Long = cursor.getLong(id)
                val audioTitle:String = cursor.getString(title)
            } while (cursor.moveToNext())
        }
    }

}