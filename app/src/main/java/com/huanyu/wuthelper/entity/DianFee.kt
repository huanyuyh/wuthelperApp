package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dianfees")
data class DianFee(
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var areaName:String,
    var dianfee:String,
    var time:String,
    var due:String,
) {
}