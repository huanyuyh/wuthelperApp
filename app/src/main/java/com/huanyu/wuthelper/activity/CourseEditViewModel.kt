package com.huanyu.wuthelper.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseEditViewModel(application: Application):AndroidViewModel (application){
    companion object{
        private val LOG = "CourseEditViewModel:"
    }
    var _index = MutableLiveData<Int>(0)
    val index: LiveData<Int> get() = _index
    var courses = mutableListOf<Course>()
    var maxIndex = 0
    var temCourse = Course(0,"",0,"","",0,0,0,0,0,0F,"","","")
    fun getAllCourses(){
        viewModelScope.launch (Dispatchers.IO){
            courses = CoursesDatabase.getDatabase(getApplication()).courseDao().getAllCourses().toMutableList()
            if(courses.isNotEmpty()){
                maxIndex = courses.size
                launch (Dispatchers.Main){ _index.value = 0  }
            }
        }
    }
    fun addIndex(){
        if(_index.value!! <maxIndex-1){
            _index.value = _index.value!! + 1
        }else{
            _index.value = 0
        }
    }
    fun decIndex(){
        if(_index.value!! >0){
            _index.value = _index.value!! -1
        }else{
            _index.value = maxIndex-1
        }
    }

    fun getNowCourse() {
        if(courses.isNotEmpty()){
            _index.value?.let { temCourse = courses.get(it) }
        }
    }
    fun saveNow(){
        viewModelScope.launch (Dispatchers.IO){
            Log.d(LOG,temCourse.toString())
            if(temCourse._id==0){
                CoursesDatabase.getDatabase(getApplication()).courseDao().insert(temCourse)
            }else{
                CoursesDatabase.getDatabase(getApplication()).courseDao().update(temCourse)
            }

            getAllCourses()
        }
    }

    fun delCourse() {
        viewModelScope.launch (Dispatchers.IO) {
            CoursesDatabase.getDatabase(getApplication()).courseDao().delete(temCourse)
            getAllCourses()
        }
    }

}