package com.huanyu.wuthelper.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.CourseTaskDatabase
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application):AndroidViewModel(application) {
    private val _isEndShow = MutableLiveData<Boolean>(false)
    val isEndShow: LiveData<Boolean> get() = _isEndShow
    private val _isTimeOrder = MutableLiveData<Boolean>(false)
    val isTimeOrder: LiveData<Boolean> get() = _isTimeOrder
    private val _courseTask = MutableLiveData<List<CourseTask>>()
    val courseTask: LiveData<List<CourseTask>> get() = _courseTask
    fun getCourseTasks(){
        viewModelScope.launch(Dispatchers.IO) {
            val courseTaskDao = CourseTaskDatabase.getDatabase(getApplication()).courseTaskDao()
            val currentTime = getCurrentDateTime()
            var courseTaskList:List<CourseTask> = listOf()
            if(isEndShow.value == true){
                if(isTimeOrder.value == true){
                    courseTaskList = courseTaskDao.getAllCourseTasksOrderByEndTime()
                }else{
                    courseTaskList = courseTaskDao.getAllCourseTasksOrderByGroup()
                }
            }else{
                if(isTimeOrder.value == true){
                    courseTaskList = courseTaskDao.getAllTasksNoLimitOrderByEndTime(currentTime)
                }else{
                    courseTaskList = courseTaskDao.getAllTasksNoLimitOrderByGroup(currentTime)
                }

            }
            launch(Dispatchers.Main){
                _courseTask.value = courseTaskList
            }
        }
    }
    fun setIsEndTask(isEnd:Boolean){
        _isEndShow.value = isEnd
    }
    fun setIsTimeOrderTask(isTime:Boolean){
        _isTimeOrder.value = isTime
    }
}