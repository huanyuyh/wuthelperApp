package com.huanyu.wuthelper.activity

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.database.BuildingDatabase
import com.huanyu.wuthelper.entity.DianFee
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeSaveRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

class DianFeeViewModel(application: Application):AndroidViewModel(application) {
    companion object{
        private val LOG_DianFeeViewModel = "DianFeeViewModel:"
    }
    private var _dianfeeList: MutableLiveData<List<DianFee>> = MutableLiveData(listOf())
    val dianfeeList: LiveData<List<DianFee>> get() = _dianfeeList
    private var _dianfeeSpeed: MutableLiveData<UsageSpeed> = MutableLiveData()
    val dianfeeSpeed: LiveData<UsageSpeed> get() = _dianfeeSpeed
    //从数据库获取课程表
    fun getDianfeeList(){
        Log.d(LOG_DianFeeViewModel+"getDianfeeList","getDianfeeList()")
        viewModelScope.launch(Dispatchers.IO){
            val saveRoom = getDianFeeSaveRoom(getApplication())
            Log.d(LOG_DianFeeViewModel+"dianFeeDao",saveRoom)
            var dianFees:List<DianFee>
            val dianFeeDao = BuildingDatabase.getDatabase(getApplication()).dianFeeDao()
            if(saveRoom.contains("null")){
                dianFees = dianFeeDao.getAll()
            }else{
                dianFees = dianFeeDao.queryDianFeesByAreaName(saveRoom)
            }
            Log.d(LOG_DianFeeViewModel+"dianFeeDao",dianFees.toString())
            launch(Dispatchers.Main) {
                _dianfeeList.value = dianFees
                calculateUsageSpeed()
            }

        }

    }

    data class UsageSpeed(
        val electricFeeSpeed: Float,
        val electricityUsageSpeed: Float
    )

    fun calculateUsageSpeed() {
        Log.d(LOG_DianFeeViewModel+"calculateUsageSpeed","calculateUsageSpeed")
        _dianfeeList.value?.let {
            Log.d(LOG_DianFeeViewModel+"calculateUsageSpeed",it.size.toString())
            if(it.size>=2){
                val dianFee1 = it[0]
                val dianFee2 = it[1]
                if(dianFee2.due>dianFee1.due){
                    // 日期格式化
                    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

                    // 解析时间
                    val time1 = dateFormat.parse(dianFee1.time)
                    val time2 = dateFormat.parse(dianFee2.time)

                    // 确保时间解析成功
                    if (time1 == null || time2 == null) {
                        throw IllegalArgumentException("Invalid date format")
                    }

                    // 计算时间差，单位为天
                    val diffInMillis = abs(time2.time - time1.time)
                    val daysDiff = diffInMillis / (1000 * 60 * 60 * 24).toFloat()
                    Log.d(LOG_DianFeeViewModel+"calculateUsageSpeeddaysDiff",daysDiff.toString())
                    // 获取电费和电量
                    val dianFeeValue1 = dianFee1.dianfee.toFloatOrNull() ?: 0.0f
                    val dianFeeValue2 = dianFee2.dianfee.toFloatOrNull() ?: 0.0f
                    val dueValue1 = dianFee1.due.toFloatOrNull() ?: 0.0f
                    val dueValue2 = dianFee2.due.toFloatOrNull() ?: 0.0f

                    // 计算电费差和电量差
                    val electricFeeDiff = abs(dianFeeValue2 - dianFeeValue1)
                    val electricityUsageDiff = abs(dueValue2 - dueValue1)

                    // 计算使用速度
                    val electricFeeSpeed = if (daysDiff > 0) electricFeeDiff / daysDiff else 0.0f
                    val electricityUsageSpeed = if (daysDiff > 0) electricityUsageDiff / daysDiff else 0.0f
                    _dianfeeSpeed.value = UsageSpeed(electricFeeSpeed, electricityUsageSpeed)
                }

            }
        }
    }

}