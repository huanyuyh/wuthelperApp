package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MoocCourse")
data class MoocCourse (
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var moocName:String,
    var tid:String
){
}