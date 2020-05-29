package com.example.singmetoo.appSingMe2.mUtils.songsRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SongsViewModel : ViewModel() {

    fun fetchAllSongsFromDevice(toRefreshList:Boolean = false) {
        SongsRepository.fetchAllSongsFromDevice(toRefreshList)
    }

    fun updateCurrentlyPlayingSongFromDevice(newPlayingSongId:Long?) {
        SongsRepository.updateCurrentlyPlayingSongFromDevice(newPlayingSongId)
    }

    fun getSongsLiveData() : LiveData<ArrayList<SongModel>>? {
        return SongsRepository.mSongLiveData
    }
}