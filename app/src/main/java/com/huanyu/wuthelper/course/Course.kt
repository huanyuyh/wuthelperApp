package com.huanyu.wuthelper.course

import androidx.room.Entity
import kotlin.random.Random

data class Course(
    val name: String,            // 课程名
    val day: Int,                // 该课程的是星期几（7代表星期天）参数范围：1 - 7
    val room: String = "",       // 教室
    val teacher: String = "",    // 老师
    var startNode: Int,          // 开始为第几节课
    var endNode: Int,            // 结束时为第几节课
    var startWeek: Int,          // 开始周
    var endWeek: Int,            // 结束周
    var type: Int,               // 单双周，每周为0，单周为1，双周为2
    var credit: Float = 0f,      // 学分
    var note: String = "",       // 备注
    var startTime: String = "",  // 不规则的开始时间，长度必须为5，如"08:08"
    var endTime: String = ""     // 不规则的结束时间，长度必须为5，如"08:08"
){
    fun courseToRoomClass(id: Int = 0): com.huanyu.wuthelper.entity.Course {
        return com.huanyu.wuthelper.entity.Course(
            _id = id,
            name = this.name,
            day = this.day,
            room = this.room,
            teacher = this.teacher,
            startNode = this.startNode,
            endNode = this.endNode,
            startWeek = this.startWeek,
            endWeek = this.endWeek,
            type = this.type,
            credit = this.credit,
            note = this.note,
            startTime = this.startTime,
            endTime = this.endTime,
            )
    }

}