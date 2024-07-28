package com.huanyu.wuthelper.database

import android.content.Context
import com.huanyu.wuthelper.Dao.DianFeeDao
import com.huanyu.wuthelper.entity.DianFee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BuildingDatabaseUtil {
    companion object{
        fun insertUnionDue(dao: DianFeeDao, dianFee: DianFee){
                val latestDue = dao.getLatestDue()
                if (latestDue != dianFee.due) {
                    dao.insert(dianFee)
                }
        }
        fun insertUnionDianFeeDue(dao: DianFeeDao,saveRoom: String,meterOverdue:String ,remainPower: String){
            val latestDue = dao.getLatestDue()

            if (latestDue != remainPower) {
                // 获取当前时间
                val currentDateTime = LocalDateTime.now()
                // 定义日期时间格式
                val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
                // 格式化当前时间为字符串
                val formattedDateTime = currentDateTime.format(formatter)
                DianFee(0, saveRoom, meterOverdue, formattedDateTime, remainPower)
                dao.insert(DianFee(0, saveRoom, meterOverdue, formattedDateTime, remainPower))
            }
        }
    }

}