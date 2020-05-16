package com.example.singmetoo.appSingMe2.mBase.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.singmetoo.appSingMe2.mBase.pojo.SongModel
import com.example.singmetoo.appSingMe2.mBase.util.SongsRepository

class SongsViewModel : ViewModel() {

    var songsLiveData: LiveData<ArrayList<SongModel>>? = SongsRepository.mSongLiveData

    fun fetchAllSongsFromDevice(toRefreshList:Boolean = false) {
        SongsRepository.fetchAllSongsFromDevice(toRefreshList)
    }
}