package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "onewords")
data class OneWord (
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var OneWord:String,
    var from:String,
    var fromWho:String,
    var uuid:String,
    var id:String,
    var type:String,
){
}