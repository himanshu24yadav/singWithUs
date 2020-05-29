package com.example.singmetoo.appSingMe2.mBase.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.audioPlayerHelper.PlayerStatus

interface CommonBaseInterface {
    val playerStatusLiveData: LiveData<PlayerStatus>?
    val playingSongLiveData: LiveData<SongModel>?
    val songListFromDeviceLiveData: LiveData<ArrayList<SongModel>>?
    fun playAudio(songModel: SongModel?,toShowBottomAudioPlayer: Boolean)
    fun pauseAudio()
    fun stopAudio()
    fun showBottomAudioPlayer(songToPlay:SongModel?)
    fun hideBottomAudioPlayer()
    fun closeDrawer()
    fun openDrawer()
    fun lockDrawer()
    fun unLockDrawer()
    fun isDrawerOpen() : Boolean
    fun updatePlayingSong(playingSong:SongModel?)
}