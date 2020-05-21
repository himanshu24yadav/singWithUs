package com.example.singmetoo.appSingMe2.mUtils.songsRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SongsViewModel : ViewModel() {

    fun fetchAllSongsFromDevice(toRefreshList:Boolean = false) {
        SongsRepository.fetchAllSongsFromDevice(toRefreshList)
    }

    fun updateCurrentlyPlayingSongFromDevice(oldPlayingSongId: Long?, newPlayingSongId:Long?) {
        SongsRepository.updateCurrentlyPlayingSongFromDevice(oldPlayingSongId,newPlayingSongId)
    }

    fun getSongsLiveData() : LiveData<ArrayList<SongModel>>? {
        return SongsRepository.mSongLiveData
    }
}