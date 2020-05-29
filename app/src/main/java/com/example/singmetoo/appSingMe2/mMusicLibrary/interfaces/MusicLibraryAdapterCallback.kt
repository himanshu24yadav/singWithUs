package com.example.singmetoo.appSingMe2.mMusicLibrary.interfaces

import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel

interface MusicLibraryAdapterCallback {

    fun toggleAudioPlayer(newSelectedSongForPlaying: SongModel?, toPauseSong:Boolean)

}