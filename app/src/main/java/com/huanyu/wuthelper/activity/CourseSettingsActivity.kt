package com.huanyu.wuthelper.activity

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityCourseSettingsBinding
import java.util.Locale

class CourseSettingsActivity : AppCompatActivity() {
    companion object{
        private val LOG_CourseSettingsActivity = "CourseSettingsActivity:"
    }
    lateinit var _binding:ActivityCourseSettingsBinding
    lateinit var viewModel: CourseSettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_CourseSettingsActivity+"onCreate" ,"onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityCourseSettingsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CourseSettingsViewModel::class.java]

        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //开学日期选择文本更新
        viewModel.termStart.observe(this){
            Log.d(LOG_CourseSettingsActivity+"termStart" ,"termStart.observe")
            _binding.pickerBtn.text = it
        }
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val selectedDateStr = dateFormat.format(calendar.time)
            viewModel.saveStartTermTime(selectedDateStr)
            Log.d(LOG_CourseSettingsActivity+"selectedDateStr" ,selectedDateStr)
        }
        //开学日期选择器
        _binding.pickerBtn.setOnClickListener {
            Log.d(LOG_CourseSettingsActivity+"pickerBtn" ,"pickerBtn.OnClick")
            DatePickerDialog(
                this@CourseSettingsActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onResume() {
        Log.d(LOG_CourseSettingsActivity+"onResume" ,"onResume")
        viewModel.getTermStartTime()
        super.onResume()
    }
}