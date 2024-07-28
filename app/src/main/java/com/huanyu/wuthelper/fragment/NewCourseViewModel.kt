package com.huanyu.wuthelper.fragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseShowEx
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.yearMonthDaysDifference
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import com.huanyu.wuthelper.utils.SPTools.Companion.getWeekCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewCourseViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val LOG_TAG = "NewCourseViewModel"
    }

    var courseShowEx = listOf<CourseShowEx>()
    private val _termStart = MutableLiveData<String>("2024-02-26")
    val termStart: LiveData<String> get() = _termStart
    var weekCnt: Int = 19
    private val _nowWeek = MutableLiveData<Int>(1)
    val nowWeek: LiveData<Int> get() = _nowWeek
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> get() = _courses
    private val _courseTimes = MutableLiveData<List<CourseTime>>()
    val courseTimes: LiveData<List<CourseTime>> get() = _courseTimes

    fun getTermStart() {
        Log.d("$LOG_TAG getTermStart", "getTermStart")
        _termStart.value = getUnitDate(getApplication())
    }

    fun getWeekCnt() {
        Log.d("$LOG_TAG getWeekCnt", "getWeekCnt")
        weekCnt = getWeekCount(getApplication())
    }

    fun setNowWeek(week: Int) {
        Log.d("$LOG_TAG setNowWeek", "setNowWeek")
        _nowWeek.value = week
    }

    fun getNowWeek() {
        Log.d("$LOG_TAG getNowWeek", "getNowWeek")
        _termStart.value?.let {
            val diffDate = yearMonthDaysDifference(it)
            if (_nowWeek.value != (diffDate / 7).toInt() + 1) {
                _nowWeek.value = (diffDate / 7).toInt() + 1
            }
        }
    }

    fun getCourses() {
        Log.d("$LOG_TAG getCourses", "getCourses")
        viewModelScope.launch(Dispatchers.IO) {
            val courseList = CoursesDatabase.getDatabase(getApplication()).courseDao().getAllCourses()
            courseShowEx = CoursesDatabase.getDatabase(getApplication()).courseShowExDao().getAllCourseExs()
            launch(Dispatchers.Main) {
                _courses.value = courseList
            }
        }
    }

    fun getCourseTimes() {
        Log.d("$LOG_TAG getCourseTimes", "getCourseTimes")
        viewModelScope.launch(Dispatchers.IO) {
            val courseTimeList = CoursesDatabase.getDatabase(getApplication()).courseTimeDao().getAllCourseTimes()
            launch(Dispatchers.Main) {
                _courseTimes.value = courseTimeList
            }
        }
    }
}