package com.huanyu.wuthelper.appwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.huanyu.wuthelper.MainActivity
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.BuildingDatabase
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.yearMonthDaysDifference
import com.huanyu.wuthelper.utils.CustomHttps.Companion.wutDianFeeGetAndTryByLogin
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.Executors

class CourseAppWidgetProvider : AppWidgetProvider() {
    companion object {
        const val ACTION_PREV = "com.huanyu.wuthelper.PREV"
        const val ACTION_NEXT = "com.huanyu.wuthelper.NEXT"
        const val ACTION_UPDATE = "com.huanyu.wuthelper.UPDATE"
        const val ACTION_DAILY_UPDATE = "com.huanyu.wuthelper.DAILY_UPDATE"
        var currentDate: Int = LocalDate.now().dayOfWeek.value
        var nowDate: Int = LocalDate.now().dayOfWeek.value
        private val executorService = Executors.newSingleThreadExecutor()
        val weekList = mutableListOf("周一","周二","周三","周四","周五","周六","周日")

    }
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // 设置每日更新的闹钟
        setDailyUpdateAlarm(context)
        // 迭代所有的widget实例
        for (appWidgetId in appWidgetIds) {
            // 在后台线程中获取课程信息
            executorService.execute {
                val views = RemoteViews(context.packageName, R.layout.course_widget_layout)
                val startDate = getUnitDate(context)
                val diffDate = yearMonthDaysDifference(startDate)
                val week = ((diffDate)/7+1).toInt()
                var courseCnt = CoursesDatabase.getDatabase(context).courseDao().getCourseCounttoday(week,
                    currentDate)
                if(courseCnt==0){
                    views.setViewVisibility(R.id.no_courses_text, View.VISIBLE)
                    views.setViewVisibility(R.id.courseList, View.GONE)
                }else{
                    views.setViewVisibility(R.id.no_courses_text, View.GONE)
                    views.setViewVisibility(R.id.courseList, View.VISIBLE)
                }
                if(currentDate!= nowDate){
                    views.setTextViewText(R.id.todayCourse,weekList[currentDate-1])
                }else{
                    views.setTextViewText(R.id.todayCourse,"今日课程")
                }

                val prevIntent = Intent(context,CourseAppWidgetProvider::class.java).apply {
                    action = ACTION_PREV
                }
                val prevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.courseLast, prevPendingIntent)

                val nextIntent = Intent(context, CourseAppWidgetProvider::class.java).apply {
                    action = ACTION_NEXT
                }
                val nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.courseNext, nextPendingIntent)

                val updateIntent = Intent(context, CourseAppWidgetProvider::class.java).apply {
                    action = ACTION_UPDATE
                }
                val updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.courseRefresh, updatePendingIntent)

                val serviceIntent = Intent(context, CourseWidgetService::class.java)
                views.setRemoteAdapter(R.id.courseList, serviceIntent)

                // 设置点击事件启动Activity
                val appIntent = Intent(context, MainActivity::class.java)
                val appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setPendingIntentTemplate(R.id.courseList, appPendingIntent)
                // 为整个widget设置点击事件，以便在没有课程时启动应用程序
                val defaultIntent = Intent(context, MainActivity::class.java)
                defaultIntent.putExtra(MainActivity.SHOW_PAGE, 0)
                val defaultPendingIntent = PendingIntent.getActivity(context, 0, defaultIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.course_widget, defaultPendingIntent)
                fetchAndUpdateWidget(context)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.courseList)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
    private fun fetchAndUpdateWidget(context: Context) {
        Log.d("mywidget","getdianfee")
        // 使用协程进行网络请求
        CoroutineScope(Dispatchers.IO).launch {
            wutDianFeeGetAndTryByLogin(context, onSuccess = { remindPower, fee ->
                Log.d("mywidget","onSuccess")
                CoroutineScope(Dispatchers.Main).launch {
                    updateWidgetUI(context, "电费剩余${remindPower}度")
                }
            }, onError = {
                Log.d("mywidget","onError")
                val due = BuildingDatabase.getDatabase(context).dianFeeDao().getLatestDue()
                CoroutineScope(Dispatchers.Main).launch {
                    updateWidgetUI(context, "上次查询:${due}度")
                }
            })
        }
    }
    private fun updateWidgetUI(context: Context, result: String) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, CourseAppWidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        for (widgetId in allWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.course_widget_layout)
            views.setTextViewText(R.id.dianfeeTv, result)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PREV -> {
                Log.d("mywidget",ACTION_PREV)
                if(currentDate==1){
                    currentDate = 7
                }else{
                    currentDate-=1
                }

            }
            ACTION_NEXT -> {
                Log.d("mywidget",ACTION_NEXT)
                if(currentDate==7){
                    currentDate = 1
                }else{
                    currentDate+=1
                }

            }
            ACTION_UPDATE -> {
                Log.d("mywidget", ACTION_UPDATE)
                nowDate = LocalDate.now().dayOfWeek.value
                currentDate = nowDate
            }
            ACTION_DAILY_UPDATE -> {
                Log.d("mywidget", ACTION_DAILY_UPDATE)
                // 每天更新日期
                nowDate = LocalDate.now().dayOfWeek.value
                currentDate = nowDate
            }
        }
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            nowDate = LocalDate.now().dayOfWeek.value
            currentDate = nowDate
            // 启动网络请求服务
//            val workRequest = OneTimeWorkRequest.Builder(DianFeeNetworkWorker::class.java).build()
//            WorkManager.getInstance(context).enqueue(workRequest)

        }
        // 手动更新
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context.packageName,CourseAppWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.courseList)
        onUpdate(context, appWidgetManager, appWidgetIds)
    }
    private fun setDailyUpdateAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CourseAppWidgetProvider::class.java).apply {
            action = ACTION_DAILY_UPDATE
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // 设置每天的定时更新
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // 如果时间已经过了今天的更新时间，设置为明天
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

}