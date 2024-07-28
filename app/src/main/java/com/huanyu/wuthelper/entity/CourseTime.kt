package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coursetimes")
data class CourseTime(
    @PrimaryKey(autoGenerate = true) var _id: Int = 0,
    val node: Int,
    val startTime: String, // 时间字符串，格式必须为"HH:mm"，24小时制，如"01:30"
    val endTime: String
)