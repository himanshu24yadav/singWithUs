package com.example.singmetoo.dbHelper.daos

import androidx.room.*
import com.example.singmetoo.testing.TestingUserModel

@Dao
interface DaoTestingUser {

    @Query ("Select * from testing_user")
    fun getTestingUsers() : List<TestingUserModel>

    @Query("SELECT * FROM testing_user WHERE user_dob == :dob")
    fun getUsersByDob(dob: String): List<TestingUserModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(testingUserModel: TestingUserModel)

    @Update
    fun updateUser(testingUserModel: TestingUserModel)

    @Delete
    fun deleteUser(testingUserModel: TestingUserModel)

}