package com.huanyu.wuthelper

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huanyu.wuthelper.entity.CourseInfo

class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOG = "MainViewModel:"
    }

    init {
        Log.d(LOG, "ViewModel 初始化")
    }
}