package com.example.singmetoo.dbHelper.daos

import androidx.room.*
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppConstants

@Dao
interface DaoSongsFromDevice {

    @Query ("Select * from ${AppConstants.TABLE_SONGS_FROM_DEVICE} ORDER BY songTitle ASC")
    fun getAllSongsFromDevice() : List<SongModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: SongModel)

    @Query ("Select COUNT(songId) from ${AppConstants.TABLE_SONGS_FROM_DEVICE}")
    fun countOfSongs() : Int

    @Update
    fun updateSongDetail(song: SongModel)

    @Delete
    fun deleteSong(song: SongModel)
}