package com.example.singmetoo.appSingMe2.mBase.interfaces

import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.audioPlayerHelper.PlayerStatus

interface CommonBaseInterface {
    val playerStatusLiveData: MutableLiveData<PlayerStatus>?
    fun playAudio(songModel: SongModel?)
    fun pauseAudio()
    fun stopAudio()
    fun showBottomAudioPlayer(songToPlay:SongModel?)
    fun hideBottomAudioPlayer()
    fun closeDrawer()
    fun openDrawer()
    fun lockDrawer()
    fun unLockDrawer()
    fun isDrawerOpen() : Boolean
}