package com.huanyu.wuthelper.appwidget

import android.content.Intent
import android.widget.RemoteViewsService

class CourseWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CourseWidgetFactory(this.applicationContext)
    }
}