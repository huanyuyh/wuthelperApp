package com.huanyu.wuthelper.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseListViewModel(application: Application):AndroidViewModel(application) {
    companion object{
        private val LOG_CourseListViewModel = "CourseListViewModel:"
    }
    private var _courseList: MutableLiveData<List<CourseInfo>> = MutableLiveData()
    val courseList: LiveData<List<CourseInfo>> get() = _courseList
    //从数据库获取课程表
    fun getCourseList(){
        Log.d(LOG_CourseListViewModel+"getCourseList","getCourseList()")
        viewModelScope.launch(Dispatchers.IO){
            var courseInfos = CoursesDatabase.getDatabase(getApplication()).courseInfoDao().getAllCourseInfos()
            Log.d(LOG_CourseListViewModel+"courseInfos",courseInfos.toString())
            launch(Dispatchers.Main) {
                _courseList.value = courseInfos
            }
        }
    }
}