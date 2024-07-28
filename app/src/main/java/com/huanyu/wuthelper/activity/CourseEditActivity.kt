package com.huanyu.wuthelper.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityCourseEditBinding
import com.huanyu.wuthelper.databinding.ActivityCourseListBinding
import com.huanyu.wuthelper.entity.Course

class CourseEditActivity : AppCompatActivity() {
    companion object{
        private val LOG = "CourseEditActivity:"
    }
    lateinit var _binding: ActivityCourseEditBinding
    lateinit var viewModel: CourseEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[CourseEditViewModel::class.java]
        _binding = ActivityCourseEditBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _binding.buttonNext.setOnClickListener {
            viewModel.addIndex()
        }
        _binding.buttonLast.setOnClickListener {
            viewModel.decIndex()
        }
        _binding.buttonSave.setOnClickListener {
            viewModel.saveNow()
        }
        _binding.buttonNew.setOnClickListener {
            viewModel.temCourse = Course(0,"",0,"","",0,0,0,0,0,0F,"","","")
            setCourseEdit(viewModel.temCourse)
        }
        _binding.buttonDel.setOnClickListener {
            viewModel.delCourse()
        }
        viewModel.index.observe(this){
           viewModel.getNowCourse()
            setCourseEdit(viewModel.temCourse)
        }
        viewModel.getAllCourses()
        setEdit()
    }
    fun setEdit(){
        _binding.editTextName.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextDay.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.day = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextRoom.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.room = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextTeacher.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.teacher = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextStartNode.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.startNode = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextEndNode.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.endNode = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextStartWeek.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.startWeek = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextEndWeek.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.endWeek = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextCredit.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.credit = s.toString().toFloat()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        _binding.editTextNote.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(LOG,viewModel.temCourse.toString())
                viewModel.temCourse.note = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
    fun setCourseEdit(course: Course){
        _binding.editTextName.setText(course.name)
        _binding.editTextDay.setText(course.day.toString())
        _binding.editTextRoom.setText(course.room)
        _binding.editTextTeacher.setText(course.teacher)
        _binding.editTextStartNode.setText(course.startNode.toString())
        _binding.editTextEndNode.setText(course.endNode.toString())
        _binding.editTextStartWeek.setText(course.startWeek.toString())
        _binding.editTextEndWeek.setText(course.endWeek.toString())
        _binding.editTextCredit.setText(course.credit.toString())
        _binding.editTextNote.setText(course.note)
    }

}