package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courseshowex")
data class CourseShowEx(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    var name:String,
    var color:String
) {
}