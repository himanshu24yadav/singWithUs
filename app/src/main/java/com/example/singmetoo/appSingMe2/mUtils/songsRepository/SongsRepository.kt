package com.example.singmetoo.appSingMe2.mUtils.songsRepository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.dbHelper.AppDatabase
import kotlinx.coroutines.*

object SongsRepository {

    private val mContext:Context? = CustomApplicationClass.applicationContext()
    private val appDatabase: AppDatabase? = AppDatabase.getInstance(mContext)
    var mSongLiveData: MutableLiveData<ArrayList<SongModel>>? = MutableLiveData()

    private val coroutineExceptionHandler:CoroutineExceptionHandler = CoroutineExceptionHandler { _,throwable->
        AppUtil.showToast(mContext,"message = ${throwable.message}")
    }

    fun fetchAllSongsFromDevice(toRefreshList:Boolean) {
        CoroutineScope(Dispatchers.Main + Job() + coroutineExceptionHandler).launch {
            when {
                !toRefreshList && isDataAvailableInCache() -> {
                    fromDB()
                }
                else -> {
                    fromDevice()
                    storeSongsToDB()
                }
            }
        }
    }

    fun updateCurrentlyPlayingSongFromDevice(oldPlayingSongId:Long?, newPlayingSongId:Long?) {
        val updateSongList: ArrayList<SongModel>? = mSongLiveData?.value
        var oldPlayingSongModel:SongModel? = null
        var newPlayingSongModel:SongModel? = null

        updateSongList?.let {
            for (item in it) {
                item.songCurrentlyPlaying = item.songId == newPlayingSongId
                when(item.songId) {
                    oldPlayingSongId -> oldPlayingSongModel = item
                    newPlayingSongId -> newPlayingSongModel = item
                }
            }
        }
        mSongLiveData?.value = updateSongList

        CoroutineScope(Dispatchers.Main + Job() + coroutineExceptionHandler).launch {
            if(oldPlayingSongModel!=null && newPlayingSongModel!=null) {
                updateSongDetailsFromDevice(oldPlayingSongModel)
                updateSongDetailsFromDevice(newPlayingSongModel)
            }
        }
    }

    private suspend fun fromDevice() {
        withContext(Dispatchers.IO) {
            val musicList:ArrayList<SongModel>? = ArrayList()
            val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
            val cursor: Cursor? = mContext?.contentResolver?.query(uri, null, selection, null, sortOrder)

            if (cursor!= null && cursor.moveToFirst()){
                val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val title = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val composer = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
                val size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                val dateAdded = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val dateModified = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
                val duration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) cursor.getColumnIndex(MediaStore.Audio.Media.DURATION) else -1

                do {
                    val songModel = SongModel()
                    songModel.songId = cursor.getLong(id)
                    songModel.songTitle =  cursor.getString(title)
                    songModel.songAlbumId =  cursor.getLong(albumId)
                    songModel.songPath = cursor.getString(data)
                    songModel.songAlbum = cursor.getString(album)
                    songModel.songArtist = cursor.getString(artist)
                    songModel.songComposer = cursor.getString(composer)
                    songModel.songSize = cursor.getString(size).toLongOrNull()
                    songModel.songDateAdded = cursor.getString(dateAdded).toLongOrNull()
                    songModel.songDateModified = cursor.getString(dateModified).toLongOrNull()
                    songModel.songDuration = if(duration != -1) cursor.getString(duration).toLongOrNull() else null

                    if(isValidSongDetails(songModel)){
                        musicList?.add(songModel)
                    }

                } while (cursor.moveToNext())
            }

            if(musicList!!.size > 0) {
                musicList[0].songCurrentlyPlaying = true
            }
            mSongLiveData?.postValue(musicList)

            cursor?.close()
        }
    }

    private suspend fun storeSongsToDB() {
        withContext(Dispatchers.Default + SupervisorJob()) {
            mSongLiveData?.value?.let { songList ->
                for (song in songList) {
                    appDatabase?.daoSongsFromDevice()?.insertSong(song)
                }
            }
        }
    }

    private suspend fun isDataAvailableInCache():Boolean {
        return withContext(Dispatchers.Default) {
            appDatabase?.daoSongsFromDevice()?.countOfSongs()!! > 0
        }
    }

    private suspend fun fromDB() {
        withContext(Dispatchers.Default) {
            val songsList: ArrayList<SongModel>? = appDatabase?.daoSongsFromDevice()?.getAllSongsFromDevice() as? ArrayList<SongModel>?
            mSongLiveData?.postValue(songsList)
        }
    }

    private suspend fun updateSongDetailsFromDevice(songModelToUpdate:SongModel?) {
        withContext(Dispatchers.Default) {
            songModelToUpdate?.let {
                appDatabase?.daoSongsFromDevice()?.updateSongDetail(songModelToUpdate)
            }
        }
    }

    private fun isValidSongDetails(songDetail: SongModel) : Boolean{
        return songDetail.songId > 0 && AppUtil.checkIsNotNull(songDetail.songTitle) && AppUtil.checkIsNotNull(songDetail.songPath)
    }

}