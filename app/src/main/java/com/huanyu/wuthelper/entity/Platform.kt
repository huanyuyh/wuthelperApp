package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platforms")
data class Platform(
    @PrimaryKey(autoGenerate = true)  var _id:Int = 0,
    var platName:String,
    var platUrl:String,
    var userPLat:String,
    var webJs:String,
    var color:String
    ) {
}