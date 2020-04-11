package com.example.singmetoo.testing

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "testing_user")
class TestingUserModel (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    var userId:Int? = null,

    @ColumnInfo(name = "user_name")
    var userName:String? = null,

    @ColumnInfo(name = "user_address")
    var userAddress:String? = null,

    @ColumnInfo(name = "user_dob")
    var dob: Date? = null,

    @Ignore
    var info: String? = null
)