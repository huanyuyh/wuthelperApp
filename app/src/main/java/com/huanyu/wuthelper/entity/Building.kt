package com.huanyu.wuthelper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buildings")
data class Building(
    @PrimaryKey(autoGenerate = true) var _id:Int = 0,
    var areaParent:String,
    var areaParentId:String,
    var area:String,
    var areaId:String,
) {
}