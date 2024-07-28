package com.huanyu.wuthelper.appwidget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.huanyu.wuthelper.MainActivity
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.yearMonthDaysDifference
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import kotlinx.coroutines.runBlocking

class CourseWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var courses: List<Course> = emptyList()
    private var currentDate: Int = CourseAppWidgetProvider.currentDate
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        currentDate = CourseAppWidgetProvider.currentDate
        var courseDao = CoursesDatabase.getDatabase(context).courseDao()
        val startDate = getUnitDate(context)
        val diffDate = yearMonthDaysDifference(startDate)
        val week = ((diffDate)/7+1).toInt()
        Log.d("WidgetCurrentWeek",week.toString())
        Log.d("WidgetCurrentDay",currentDate.toString())
        runBlocking {
            courses = courseDao.getCoursesForWeekAndDay(week,currentDate)
            Log.d("WidgetCurrentcourses",courses.toString())
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = courses.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.item_widget_course)
        if(courses.isNotEmpty()&&position<courses.size){
            val course = courses[position]
            views.setTextViewText(R.id.courseName, course.name)
            views.setTextViewText(R.id.courseDesc, "第${course.startNode}-${course.endNode}节 ${course.room}")
            // 设置点击事件，启动Activity
            val fillInIntent = Intent()
            fillInIntent.putExtra(MainActivity.SHOW_PAGE, 0)
            views.setOnClickFillInIntent(R.id.item_course, fillInIntent)
        }


        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long {
        return if(courses.isNotEmpty()&&position<courses.size){
            courses[position]._id.toLong()
        }else{
            0L
        }

    }

    override fun hasStableIds(): Boolean = true
}