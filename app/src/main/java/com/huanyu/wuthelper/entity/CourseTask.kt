package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.huanyu.wuthelper.entity.Platform

@Entity(tableName = "coursetasks")
data class CourseTask (
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var name:String,
    var platform: String,
    var group_name:String,
    var start_time:String,
    var end_time:String,
    var is_course_task:String,
    var is_allow_after_submitted:Boolean,
    var task_type:Int,
    var note:String,
    var isFinish:Boolean,
)
