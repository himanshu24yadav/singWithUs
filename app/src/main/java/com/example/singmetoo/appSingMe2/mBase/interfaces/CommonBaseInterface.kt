package com.example.singmetoo.appSingMe2.mBase.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.audioPlayerHelper.PlayerStatus
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

interface CommonBaseInterface {
    val playerStatusLiveData: LiveData<PlayerStatus>?
    val playingSongLiveData: LiveData<SongModel>?
    val songListFromDeviceLiveData: LiveData<ArrayList<SongModel>>?
    val exoAudioPlayerView: Player?
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