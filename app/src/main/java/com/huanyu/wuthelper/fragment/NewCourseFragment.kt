package com.huanyu.wuthelper.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.gridlayout.widget.GridLayout
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.*
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.databinding.FragmentNewCourseBinding
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.utils.CustomDataUtils.*
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.addDaysToDateReturnDay
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.addDaysToDateReturnMonth
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.addDaysToDateReturnMonthDay
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.addDaysToDateReturnWeekday
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentYear
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.isCurrentTimeInRange
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.monthDaysDifference
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.yearMonthDaysDifference
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myCourseAlertDialog
import kotlin.random.Random

class NewCourseFragment : Fragment() {

    companion object {
        private const val LOG_TAG = "NewCourseFragment"
        fun newInstance() = NewCourseFragment()
    }

    private lateinit var binding: FragmentNewCourseBinding
    private val backcolors = listOf(
        "#c1cbd7", "#e0e5df", "#b5c4b1", "#9ca8b8", "#ececea", "#fffaf4",
        "#96a48b", "#dfd7d7", "#d8caaf", "#e0cdcf", "#965454", "#eee5f8",
        "#c9c0d3", "#7a7281"
    )
    private val viewModel: NewCourseViewModel by viewModels()
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onResume() {
        super.onResume()
        viewModel.getNowWeek()
        viewModel.getTermStart()
        viewModel.getWeekCnt()
        Log.d("$LOG_TAG onResume", viewModel.courses.value.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 初始化 ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra("key")?.let { Log.d("NewCourse", it) }
                viewModel.getCourses()
            }
        }

        // 绑定视图
        binding = FragmentNewCourseBinding.inflate(inflater, container, false)

        // 设置滚动监听
        binding.courseScroll.setOnScrollChangeListener { v, _, _, _, _ ->
            if (!v.canScrollVertically(1)) {
                binding.lastPage.hide()
                binding.nextPage.hide()
            } else {
                binding.lastPage.show()
                binding.nextPage.show()
            }
        }

        // 设置按钮点击事件
        binding.nextPage.setOnClickListener {
            viewModel.nowWeek.value?.let {
                viewModel.setNowWeek(if (it == viewModel.weekCnt) 1 else it + 1)
            }
        }

        binding.lastPage.setOnClickListener {
            viewModel.nowWeek.value?.let {
                viewModel.setNowWeek(if (it == 1) viewModel.weekCnt else it - 1)
            }
        }

        binding.moreBtn.setOnClickListener {
            showPopupMenu(it)
        }

        // 观察 ViewModel 数据变化
        viewModel.getNowWeek()
        viewModel.getTermStart()
        viewModel.getCourses()
        viewModel.getCourseTimes()

        viewModel.courses.observe(viewLifecycleOwner) {
            viewModel.nowWeek.value?.let { setNowWeekCourse(it) }
        }

        viewModel.nowWeek.observe(viewLifecycleOwner) {
            setNowWeekCourse(it)
        }

        viewModel.courseTimes.observe(viewLifecycleOwner) {
            setTimeLine(binding.courseTable, it)
        }

        viewModel.termStart.observe(viewLifecycleOwner) {
            setWeekBtn(it)
            viewModel.getNowWeek()
            viewModel.nowWeek.value?.let { week -> setDateLine(binding.dateLine, it, week) }
        }

        return binding.root
    }

    private fun showPopupMenu(view: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.custom_popup_menu, null)

        val popupWindow = PopupWindow(popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, true)

        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.popup_background))
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // 设置点击事件
        popupView.findViewById<TextView>(R.id.importCourse).setOnClickListener {
            val intent = Intent(context, ToolWebActivity::class.java)
            intent.putExtra("webPlatform", "教务系统（智慧理工大）")
            intent.putExtra("webUrl", "http://sso.jwc.whut.edu.cn/Certification/index2.jsp")
            intent.putExtra("action", "importClass")
            activityResultLauncher?.launch(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.setList).setOnClickListener {
            val intent = Intent(requireContext(), CourseSettingsActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.courseList).setOnClickListener {
            val intent = Intent(requireContext(), CourseListActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.courseEdit).setOnClickListener {
            val intent = Intent(requireContext(), CourseEditActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        // 显示PopupWindow
        popupWindow.showAsDropDown(view)
    }

    private fun setNowWeekCourse(week: Int) {
        binding.buttonShowWeek.text = "第${week} 周"
        viewModel.courses.value?.let { setCourseGrid(binding.courseTable, it, week) }
        viewModel.termStart.value?.let { setDateLine(binding.dateLine, it, week) }
    }

    private fun setWeekBtn(startDate: String) {
        val diffDate = yearMonthDaysDifference(startDate)
        binding.buttonShowWeek.text = "第${diffDate / 7 + 1} 周"
        binding.buttonShowWeek.setOnClickListener {
            WeekPickerFragment().apply {
                onWeekSelected = { week -> viewModel.setNowWeek(week) }
            }.show(parentFragmentManager, WeekPickerFragment.TAG)
        }
    }

    private fun setDateLine(gridLayout: GridLayout, termStart: String, week: Int) {
        gridLayout.removeAllViews()
        val width = resources.displayMetrics.widthPixels

        val dataview = LayoutInflater.from(requireContext()).inflate(R.layout.item_date, null)
        val dateTv = dataview.findViewById<TextView>(R.id.dateTv)
        val weekdayTv = dataview.findViewById<TextView>(R.id.weekdayTv)
        dateTv.text = "${addDaysToDateReturnMonth(termStart, ((week - 1) * 7).toLong()).toInt()}月"
        weekdayTv.text = getCurrentYear().toString()
        weekdayTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
        dateTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)

        val dataparams = GridLayout.LayoutParams()
        dataparams.width = (width / 23 * 2)
        dataparams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dataparams.rowSpec = GridLayout.spec(0, 1)
        dataparams.columnSpec = GridLayout.spec(0, 1)
        dataview.layoutParams = dataparams
        gridLayout.addView(dataview)

        val weekList = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        for (i in 0..6) {
            val tempday = ((week - 1) * 7 + i).toLong()
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_date, null)
            val textView = view.findViewById<TextView>(R.id.dateTv)
            val weekTv = view.findViewById<TextView>(R.id.weekdayTv)
            val llData = view.findViewById<LinearLayout>(R.id.ll_course_data)
            textView.text = addDaysToDateReturnDay(termStart, tempday)
            weekTv.text = weekList[addDaysToDateReturnWeekday(termStart, tempday) - 1]
            if (monthDaysDifference(addDaysToDateReturnMonthDay(termStart, tempday)) == 0L) {
                llData.setBackgroundResource(R.drawable.course_todaybg)
            }

            val params = GridLayout.LayoutParams()
            params.width = (width / 23 * 3)
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            params.rowSpec = GridLayout.spec(0, 1)
            params.columnSpec = GridLayout.spec(i + 1, 1)
            view.layoutParams = params
            gridLayout.addView(view)
        }
    }

    private fun removeAllViewsWithTag(parent: ViewGroup, tag: String) {
        val childrenToRemove = mutableListOf<View>()
        parent.children.forEach { if (it.tag == tag) childrenToRemove.add(it) }
        childrenToRemove.forEach { parent.removeView(it) }
    }

    private fun setCourseGrid(gridLayout: GridLayout, courseList: List<Course>, week: Int) {
        removeAllViewsWithTag(gridLayout, "course")
        val orientation = resources.configuration.orientation
        val density = resources.displayMetrics.density
        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        for (c in 1..7) {
            val courses = courseList.filter { it.day == c && it.startWeek <= week && it.endWeek >= week }
            if (courses.isEmpty()) {
                val emptyView = View(requireContext()).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    minimumHeight = width / 23 * 3
                    minimumWidth = height / 10
                }
                val params = GridLayout.LayoutParams()
                params.width = width / 23 * 3
                params.height = height / 10
                params.rowSpec = GridLayout.spec(0, 1)
                params.columnSpec = GridLayout.spec(c, 1)
                emptyView.layoutParams = params
                gridLayout.addView(emptyView)
            } else {
                courses.forEach { course ->
                    val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_course, null)
                    val nameTv = view.findViewById<TextView>(R.id.courseNameTv)
                    val teacherTv = view.findViewById<TextView>(R.id.courseTeacher)
                    val positionTv = view.findViewById<TextView>(R.id.coursePosition)
                    val timeTv = view.findViewById<TextView>(R.id.courseTime)
                    val linearLayout = view.findViewById<LinearLayout>(R.id.ll_backCard)
                    nameTv.text = course.name
                    teacherTv.text = course.teacher
                    positionTv.text = course.room
                    timeTv.text = "${course.startNode}-${course.endNode}"

                    val params = GridLayout.LayoutParams()
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        params.width = (width * 0.92 * 0.92 / 15 * 2).toInt()
                        params.height = (80 * density * (course.endNode - course.startNode + 1)).toInt()
                    } else {
                        params.width = width / 23 * 3
                        params.height = height / 10 * (course.endNode - course.startNode + 1)
                    }
                    params.rowSpec = GridLayout.spec(course.startNode - 1, course.endNode - course.startNode + 1)
                    params.columnSpec = GridLayout.spec(course.day, 1)
                    view.layoutParams = params

                    val randomColor = backcolors[Random.nextInt(backcolors.size)]
                    val tempCourseEx = viewModel.courseShowEx.filter { it1 -> it1.name == course.name }
                    val backgroundColor = if (tempCourseEx.isNotEmpty()) tempCourseEx[0].color else randomColor
                    val background = linearLayout.background as GradientDrawable
                    val color = Color.parseColor(backgroundColor)
                    background.setColor(color)
                    val textColor = getTextColorForBackground(color)
                    nameTv.setTextColor(textColor)
                    teacherTv.setTextColor(textColor)
                    positionTv.setTextColor(textColor)
                    timeTv.setTextColor(textColor)
                    linearLayout.background = background

                    view.setOnClickListener {
                        Thread {
                            val courseInfo = CoursesDatabase.getDatabase(requireContext()).courseInfoDao().getCourseInfoByName(course.name)
                            requireActivity().runOnUiThread {
                                myCourseAlertDialog(requireContext(),courseInfo.time,course)
                            }
                        }.start()
                    }
                    view.tag = "course"
                    gridLayout.addView(view)
                }
            }
        }
    }

    private fun getTextColorForBackground(backgroundColor: Int): Int {
        val brightness = (Color.red(backgroundColor) * 299 + Color.green(backgroundColor) * 587 + Color.blue(backgroundColor) * 114) / 1000
        return if (brightness >= 128) Color.BLACK else Color.WHITE
    }

    private fun setTimeLine(gridLayout: GridLayout, timeList: List<CourseTime>) {
        removeAllViewsWithTag(gridLayout, "time")
        val orientation = resources.configuration.orientation
        val density = resources.displayMetrics.density
        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        var lastTime = "08:00"
        for ((index, timeDetail) in timeList.withIndex()) {
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_course_time, null)
            val nodeTv = view.findViewById<TextView>(R.id.nodeTv)
            val startTv = view.findViewById<TextView>(R.id.startTimeTv)
            val endTv = view.findViewById<TextView>(R.id.endTimeTv)
            val llData = view.findViewById<LinearLayout>(R.id.ll_course_time)
            nodeTv.text = timeDetail.node.toString()
            startTv.text = timeDetail.startTime
            endTv.text = timeDetail.endTime
            val inTime = isCurrentTimeInRange(lastTime, timeDetail.endTime)
            lastTime = timeDetail.endTime
            if (inTime) {
                llData.setBackgroundResource(R.drawable.timegridbg_today)
            }

            val params = GridLayout.LayoutParams()
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.width = (width * 0.92 * 0.92 / 15).toInt()
                params.height = (80 * density).toInt()
                startTv.textSize = 10F
                endTv.textSize = 10F
            } else {
                params.width = (width / 23 * 2)
                params.height = height / 10
                startTv.textSize = 10F
                endTv.textSize = 10F
            }

            params.rowSpec = GridLayout.spec(index, 1)
            params.columnSpec = GridLayout.spec(0, 1)
            view.layoutParams = params
            view.tag = "time"
            gridLayout.addView(view)
        }
    }
}
