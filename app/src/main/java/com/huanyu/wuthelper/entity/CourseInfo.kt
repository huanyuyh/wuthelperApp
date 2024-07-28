package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courseinfos")
data class CourseInfo(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var name:String,
    var room:String,
    var teacher:String,
    var time:String,
    var credit:Float,
    var note:String,
    ) {
}