package com.huanyu.wuthelper.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.entity.LocationEntity
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import com.huanyu.wuthelper.utils.SPTools.Companion.putUnitDate

class CourseSettingsViewModel(application: Application):AndroidViewModel(application) {
    companion object{
        private val LOG_CourseSettingsViewModel = "CourseSettingsViewModel:"
    }
    private val _termStart = MutableLiveData<String>()
    val termStart: LiveData<String> get() = _termStart
    //获取开学日期
    fun getTermStartTime(){
        Log.d(LOG_CourseSettingsViewModel +"getTermStartTime" ,"getTermStartTime")
        _termStart.value = getUnitDate(getApplication())
    }
    //保存开学日期
    fun saveStartTermTime(dataStr:String){
        Log.d(LOG_CourseSettingsViewModel +"saveStartTermTime" ,"saveStartTermTime")
        _termStart.value = dataStr
        putUnitDate(getApplication(),dataStr)
    }
}