package com.huanyu.wuthelper.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.adapter.LocationListAdapter
import com.huanyu.wuthelper.adapter.TaskListAdapter
import com.huanyu.wuthelper.databinding.ActivityTaskBinding
import com.huanyu.wuthelper.databinding.ActivityWebBinding

class TaskActivity : AppCompatActivity() {
    lateinit var _binding:ActivityTaskBinding
    lateinit var viewModel: TaskViewModel
    override fun onResume() {
        viewModel.getCourseTasks()
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityTaskBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        var adapter = TaskListAdapter(this,null)
        _binding.taskRecyclerView.adapter = adapter
        viewModel.courseTask.observe(this){
            adapter.updateData(it)
        }
        viewModel.isEndShow.observe(this) {
            viewModel.getCourseTasks()
        }
        viewModel.isTimeOrder.observe(this) {
            viewModel.getCourseTasks()
        }
        _binding.switchTask.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setIsEndTask(isChecked)
        }
        _binding.switchTime.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setIsTimeOrderTask(isChecked)
            if(isChecked){
                _binding.switchTime.text = "时间排序"
            }else{
                _binding.switchTime.text = "课程排序"
            }
        }
    }
}