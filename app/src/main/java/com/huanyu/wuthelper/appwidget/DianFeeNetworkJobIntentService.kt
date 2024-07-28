package com.huanyu.wuthelper.appwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.JobIntentService
import com.huanyu.wuthelper.R

class DianFeeNetworkJobIntentService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        Log.d("onHandleWork","onHandleWork")

    }


    private fun updateWidget(context: Context, data: String) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(context.packageName, CourseAppWidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.course_widget_layout)
            views.setTextViewText(R.id.dianfeeTv, "电费剩余${data}度")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        private const val JOB_ID = 1000

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, DianFeeNetworkJobIntentService::class.java, JOB_ID, intent)
        }
    }
}