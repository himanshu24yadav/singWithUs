package com.example.singmetoo.dbHelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.dbHelper.daos.DaoSongsFromDevice
import com.example.singmetoo.dbHelper.daos.DaoTestingUser
import com.example.singmetoo.testing.TestingUserModel

@Database (entities = [TestingUserModel::class, SongModel::class],version = 2)
@TypeConverters(DBTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun daoTestingUser():DaoTestingUser
    abstract fun daoSongsFromDevice(): DaoSongsFromDevice

    companion object {
        private var INSTANCE : AppDatabase? = null

        fun getInstance (context: Context?) : AppDatabase {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context?.applicationContext!!, AppDatabase::class.java, "sing_me_db").fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }
        fun destroyAppDB(){
            INSTANCE = null
        }

    }
}