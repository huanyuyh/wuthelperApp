package com.huanyu.wuthelper.activity

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.huanyu.wuthelper.MyApplication
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.adapter.CourseListAdapter
import com.huanyu.wuthelper.databinding.ActivityCourseListBinding

class CourseListActivity : AppCompatActivity() {
    companion object{
        private val LOG_CourseListActivity = "CourseListActivity:"
    }

    lateinit var _binding:ActivityCourseListBinding
    private lateinit var courseListViewModel: CourseListViewModel
    override fun onResume() {

        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_CourseListActivity+"onCreate","onCreate")
        super.onCreate(savedInstanceState)
        _binding = ActivityCourseListBinding.inflate(layoutInflater)
        courseListViewModel = ViewModelProvider(this)[CourseListViewModel::class.java]
        enableEdgeToEdge()
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //配置adpater
        _binding.courseList.layoutManager = LinearLayoutManager(this)
        var adapter = CourseListAdapter(this,null)
        _binding.courseList.adapter = adapter
        courseListViewModel.getCourseList()
        //课程列表
        courseListViewModel.courseList.observe(this){
            Log.d(LOG_CourseListActivity+"courseList","courseList.observe")
            //列表更新数据
            adapter.updateData(it)
        }
    }
}