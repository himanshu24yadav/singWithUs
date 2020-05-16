package com.example.singmetoo.appSingMe2.mUtils.songsRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsRepository

class SongsViewModel : ViewModel() {

    fun fetchAllSongsFromDevice(toRefreshList:Boolean = false) {
        SongsRepository.fetchAllSongsFromDevice(toRefreshList)
    }

    fun getSongsLiveData() : LiveData<ArrayList<SongModel>>? {
        return SongsRepository.mSongLiveData
    }
}