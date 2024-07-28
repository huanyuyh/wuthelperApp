package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var platform:String,
    var name:String,
    var pass:String
) {
}