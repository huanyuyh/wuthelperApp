package com.huanyu.wuthelper.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.course.WUTParser
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.CourseShowEx
import com.huanyu.wuthelper.utils.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class ToolWebViewModel(application: Application):AndroidViewModel(application) {
    companion object{
        private val LOG = "ToolWebViewModel:"
    }
    var path:String = getApplication<Application>().externalCacheDir!!.path+"/jwc.html"
    val backcolors = listOf(
        "#c1cbd7",
        "#e0e5df",
        "#b5c4b1",
        "#9ca8b8",
        "#ececea",
        "#fffaf4",
        "#96a48b",
        "#dfd7d7",
        "#d8caaf",
        "#e0cdcf",
        "#965454",
        "#eee5f8",
        "#c9c0d3",
        "#7a7281"

    )
    fun importClass(activity: Activity) {
        Log.d(LOG +"importClass" ,"importClass")
        viewModelScope.launch (Dispatchers.IO){
            val html = FileUtil.readText(path)
            html?.let {
                Log.d(LOG +"importClass" ,"html?.let")
                val wutParser = WUTParser(it)
                val courseList = wutParser.generateCourseList()
                Log.d(LOG +"importClassCourse" ,courseList.toString())
                val courses = mutableListOf<Course>()
                var courseShowExs = mutableListOf<CourseShowEx>()
                courseList.forEach {
                    courses.add(it.courseToRoomClass())
                    if(courseShowExs.filter { it1-> it1.name==it.name }.isEmpty()){
                        courseShowExs.add(CourseShowEx(0,it.name,backcolors[Random.nextInt(backcolors.size)]))
                    }

                }
                var courseDao = CoursesDatabase.getDatabase(getApplication()).courseDao()
                var courseShowExDao = CoursesDatabase.getDatabase(getApplication()).courseShowExDao()
                courseDao.deleteAll()
                courseDao.inserts(courses)
                courseShowExDao.deleteAll()
                Log.d(LOG +"importClassCourseShowExs" ,courseShowExs.toList().toString())
                courseShowExDao.inserts(courseShowExs.toList())

                var courseDetailList = mutableListOf<CourseInfo>()
                var i = 0
                courseList.forEach { course ->
                    var name = course.name
                    var tempCourseDetailList = courseDetailList.filter { it.name == name }
                    if(tempCourseDetailList.isEmpty()){
                        courseDetailList.add(
                            CourseInfo(i++,course.name,course.room,course.teacher,
                                "${course.startWeek}-${course.endWeek}周 ${course.startNode}-${course.endNode}节",course.credit,course.note)
                        )
                    }else{
                        courseDetailList[tempCourseDetailList[0]._id].time += "\n${course.startWeek}-${course.endWeek}周 ${course.startNode}-${course.endNode}节"
                    }
                }
                Log.d(LOG +"importClassCourseDetail" ,courseDetailList.toString())
                var courseInfoDao = CoursesDatabase.getDatabase(getApplication()).courseInfoDao()
                courseInfoDao.deleteAll()
                courseInfoDao.inserts(courseDetailList)
                val returnIntent = Intent()
                returnIntent.putExtra("key", "finish")
                activity.setResult(Activity.RESULT_OK, returnIntent)
                activity.finish()
                Log.d(LOG +"importClass" ,"importClassActivity.finish")
            }
        }

    }
}