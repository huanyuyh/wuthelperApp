package com.huanyu.wuthelper.fragment

import android.content.Intent
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.CourseListActivity
import com.huanyu.wuthelper.activity.TaskActivity
import com.huanyu.wuthelper.databinding.FragmentHomeBinding
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.calculateMinutesDifference
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.calculateTimeDifference
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentDay
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentMonth
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentWeekDay
import com.huanyu.wuthelper.utils.SPTools.Companion.getXiaoYaUpdateTime

class HomeFragment : Fragment() {

    companion object {
        private const val LOG = "HomeFragment:"
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var _binding: FragmentHomeBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG + "onCreate", "onCreate")
    }

    override fun onResume() {
        Log.d(LOG + "onResume", "onResume")
        // 设置今天的课程
        viewModel.setTodayCourse {
            // 完成后设置今天的任务
            viewModel.setTaskList()
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG + "onPause", "onPause")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG + "onStart", "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG + "onStop", "onStop")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG + "onDetach", "onDetach")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LOG + "onCreateView", "onCreateView")
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // 设置字体
        val customFont = Typeface.createFromAsset(requireContext().assets, "fonts/FangZhengFangSong-GBK-1.ttf")
        _binding.oneWord.setPadding(16, 16, 16, 16)
        _binding.oneWord.setTypeface(customFont)

        initData()
        setObserver()

        // 一言收藏按钮
        _binding.ivHeart.setOnClickListener {
            Log.d(LOG + "ivHeart", "OnClick")
            isFavorite = !isFavorite
            Log.d(LOG + "isFavorite", isFavorite.toString())
            if (isFavorite) {
                _binding.ivHeart.setImageResource(R.drawable.heart_red)
                viewModel.insertOneWord()
            } else {
                _binding.ivHeart.setImageResource(R.drawable.heart_empty)
                viewModel.deleteOneWord()
            }
        }

        return _binding.root
    }

    // 设置各个 LiveData 的观察者
    private fun setObserver() {
        // 电费观察者
        viewModel.dianFei.observe(viewLifecycleOwner) {
            Log.d(LOG + "DianFei", "DianFei.observe")
            _binding.dianfeiTv.text = it
        }
        // 周次观察者
        viewModel.currentWeek.observe(viewLifecycleOwner) {
            Log.d(LOG + "currentWeek", "currentWeek.observe")
            val dateStr = "${getCurrentMonth()} 月${getCurrentDay()}日 " +
                    "${getCurrentWeekDay()} 第${it}周"
            _binding.homeDate.text = dateStr
        }
        // 一言观察者
        viewModel.oneWord.observe(viewLifecycleOwner) {
            Log.d(LOG + "oneWord", "oneWord.observe")
            _binding.oneWord.text = it
        }
        // 天气观察者
        viewModel.weather.observe(viewLifecycleOwner) {
            Log.d(LOG + "weather", "weather.observe")
            _binding.Weather.text = it
        }
        // 早上课程观察者
        viewModel.morningCourses.observe(viewLifecycleOwner) {
            Log.d(LOG + "morningCourses", "morningCourses.observe")
            setCourses(_binding.homecourselinear, it, MyCourseT.Morning)
        }
        // 下午课程观察者
        viewModel.afternoonCourse.observe(viewLifecycleOwner) {
            Log.d(LOG + "afternoonCourse", "afternoonCourse.observe")
            setCourses(_binding.homecourselinear, it, MyCourseT.Afternoon)
        }
        // 晚上课程观察者
        viewModel.nightCourse.observe(viewLifecycleOwner) {
            Log.d(LOG + "nightCourse", "nightCourse.observe")
            setCourses(_binding.homecourselinear, it, MyCourseT.Night)
        }
        // 是否有课观察者
        viewModel.isHaveCourse.observe(viewLifecycleOwner) {
            Log.d(LOG + "isHaveCourse", "isHaveCourse.observe")
            setEmptyCourse(_binding.homecourselinear, it)
        }
        // 任务观察者
        viewModel.courseTask.observe(viewLifecycleOwner) {
            Log.d(LOG + "courseTask", "courseTask.observe")
            setTaskList(_binding.homecourselinear, it)
        }
    }

    // 初始化数据
    private fun initData() {
        viewModel.getDianfei(requireContext())
        viewModel.setCurrentWeek()
        viewModel.getCourseTimes()
        viewModel.getWeather()
        viewModel.getOneWord()
    }

    // 设置课程为空时显示的视图
    private fun setEmptyCourse(homecourselinear: LinearLayout, isNo: Boolean) {
        Log.d(LOG + "setEmptyCourse", "setEmptyCourse")
        if (!isNo) {
            Log.d(LOG + "setEmptyCourse", "isNo == false")
            removeAllViewsWithTag(homecourselinear, "EmptyCourse")
            val view = layoutInflater.inflate(R.layout.coursenotext, homecourselinear, false)
            val text: TextView = view.findViewById(R.id.coursenotext)
            val coursetext: TextView = view.findViewById(R.id.allTaskTv)
            text.text = "今日无课^_^"
            coursetext.text = "查看所有课程"
            coursetext.setOnClickListener {
                Log.d(LOG + "setEmptyCourse", "coursetext.setOnClick")
                val intent = Intent(requireContext(), CourseListActivity::class.java)
                startActivity(intent)
            }
            view.tag = "EmptyCourse"
            homecourselinear.addView(view)
        } else {
            Log.d(LOG + "EmptyCourse", "removeAllViews")
            removeAllViewsWithTag(homecourselinear, "EmptyCourse")
        }
    }

    // 课程时间枚举
    enum class MyCourseT {
        Morning, Afternoon, Night
    }

    // 设置课程卡片列表
    private fun setCourses(homecourselinear: LinearLayout, courses: List<Course>, myCourseT: MyCourseT) {
        Log.d(LOG + "setCourses", "setCourses")
        if (courses.isNotEmpty()) {
            Log.d(LOG + "setCourses", "courses.isNotEmpty()")
            val view = layoutInflater.inflate(R.layout.coursetimetext, homecourselinear, false)
            val text: TextView = view.findViewById(R.id.coursenotext)
            val coursetext: TextView = view.findViewById(R.id.allTaskTv)
            when (myCourseT) {
                MyCourseT.Morning -> {
                    Log.d(LOG + "setMorningCourses", "setMorningCourses")
                    removeAllViewsWithTag(homecourselinear, "morning")
                    text.text = "上午课程"
                    view.tag = "morning"
                    coursetext.text = "查看所有课程"
                    coursetext.setBackgroundResource(R.drawable.btn_lightblue_bg)
                    coursetext.setOnClickListener {
                        Log.d(LOG + "setMorningCourses", "coursetext.setOnClick")
                        val intent = Intent(requireContext(), CourseListActivity::class.java)
                        startActivity(intent)
                    }
                }
                MyCourseT.Afternoon -> {
                    Log.d(LOG + "setAfternoonCourses", "setAfternoonCourses")
                    removeAllViewsWithTag(homecourselinear, "afternoon")
                    text.text = "下午课程"
                    view.tag = "afternoon"
                    if (viewModel.morningCourses.value.isNullOrEmpty()) {
                        Log.d(LOG + "coursetext", "morningCoursesEmpty")
                        coursetext.text = "查看所有课程"
                        coursetext.setBackgroundResource(R.drawable.btn_lightblue_bg)
                        coursetext.setOnClickListener {
                            val intent = Intent(requireContext(), CourseListActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        coursetext.visibility = View.GONE
                    }
                }
                MyCourseT.Night -> {
                    Log.d(LOG + "setNightCourses", "setNightCourses")
                    removeAllViewsWithTag(homecourselinear, "night")
                    text.text = "晚上课程"
                    view.tag = "night"
                    if (viewModel.afternoonCourse.value.isNullOrEmpty() && viewModel.morningCourses.value.isNullOrEmpty()) {
                        Log.d(LOG + "coursetext", "morningafternoonEmpty")
                        coursetext.text = "查看所有课程"
                        coursetext.setBackgroundResource(R.drawable.btn_lightblue_bg)
                        coursetext.setOnClickListener {
                            val intent = Intent(requireContext(), CourseListActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        coursetext.visibility = View.GONE
                    }
                }
            }
            text.setBackgroundResource(R.drawable.btn_blue_bg)
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            homecourselinear.addView(view)
            setCourseCard(courses, viewModel.courseTime, homecourselinear, view.tag as String)
        } else {
            Log.d(LOG + "setCourses", "removeAllViews")
            removeAllViewsWithTag(homecourselinear, myCourseT.name.toLowerCase())
        }
    }

    // 设置任务卡片列表
    private fun setTaskList(homecourselinear: LinearLayout, courseTasks: List<CourseTask>) {
        Log.d(LOG + "setTaskList", "setTaskList")
        removeAllViewsWithTag(homecourselinear, "TaskView")
        if (courseTasks.isNotEmpty()) {
            Log.d(LOG + "setTaskList", "courseTasks.isNotEmpty()")
            val view = layoutInflater.inflate(R.layout.coursetasktext, homecourselinear, false)
            val text: TextView = view.findViewById(R.id.coursenotext)
            val timetext: TextView = view.findViewById(R.id.timeText)
            val tasktext: TextView = view.findViewById(R.id.allTaskTv)
            val updateTime = getXiaoYaUpdateTime(requireContext())
            if (!updateTime.contains("null")) {
                Log.d(LOG + "setTaskListupdateTime", updateTime)
                timetext.text = "更新于${updateTime}"
            } else {
                Log.d(LOG + "setTaskListupdateTime", "updateTime.contains")
                tasktext.visibility = View.GONE
            }
            text.text = "近期任务"
            text.setBackgroundResource(R.drawable.btn_blue_bg)
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            tasktext.text = "查看所有任务"
            tasktext.setBackgroundResource(R.drawable.btn_lightblue_bg)
            tasktext.setOnClickListener {
                Log.d(LOG + "tasktext", "tasktext.setOnClick")
                val intent = Intent(requireContext(), TaskActivity::class.java)
                startActivity(intent)
            }
            view.tag = "TaskView"
            homecourselinear.addView(view)
            // 获取屏幕宽度
            val windowManager = requireActivity().windowManager
            val displayMetrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = windowManager.currentWindowMetrics
                windowMetrics.bounds
            } else {
                val metrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(metrics)
                Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
            }
            val screenWidth = displayMetrics.width()
            // 计算 maxWidth，例如屏幕宽度的 80%
            val maxWidth = (screenWidth * 0.60).toInt()
            courseTasks.forEach {
                val cardView = layoutInflater.inflate(R.layout.course_taskcard, homecourselinear, false)
                val courseName = cardView.findViewById<TextView>(R.id.courseName)
                val coursePlat = cardView.findViewById<TextView>(R.id.coursePlat)
                val courseDesc = cardView.findViewById<TextView>(R.id.courseDesc)
                val courseIng = cardView.findViewById<TextView>(R.id.courseIng)
                // 设置 maxWidth
                courseName.maxWidth = maxWidth
                courseName.text = it.name
                coursePlat.text = it.platform
                if (it.platform.contains("mooc")) {
                    coursePlat.setBackgroundResource(R.drawable.taskcardbg_green)
                }
                courseDesc.text = it.group_name + " " + it.end_time
                val duration = calculateTimeDifference(it.end_time)
                val hours = duration.toHours()
                when {
                    hours > 72 -> {
                        courseIng.text = "大于三天"
                        courseIng.setBackgroundResource(R.drawable.taskcardbg_lightgreen)
                    }
                    hours > 24 -> {
                        courseIng.text = "小于三天"
                        courseIng.setBackgroundResource(R.drawable.taskcardbg_lightyellow)
                    }
                    hours < 1 -> {
                        courseIng.text = duration.toMinutes().toString() + "分钟"
                        courseIng.setBackgroundResource(R.drawable.taskcardbg_lightred)
                    }
                    else -> {
                        courseIng.text = hours.toString() + "小时"
                        courseIng.setBackgroundResource(R.drawable.taskcardbg_lightred)
                    }
                }
                cardView.tag = "TaskView"
                homecourselinear.addView(cardView)
            }
        }
    }

    // 从视图中删除指定 tag 的视图
    private fun removeAllViewsWithTag(parent: ViewGroup, tag: String) {
        Log.d(LOG + "removeAllViewsWithTag", parent.childCount.toString())
        val childrenToRemove = mutableListOf<View>()
        parent.children.forEach {
            if (it.tag == tag) {
                childrenToRemove.add(it)
            }
        }
        childrenToRemove.forEach { parent.removeView(it) }
    }

    // 设置课程卡片
    private fun setCourseCard(courses: List<Course>, courseTimeList: List<CourseTime>, homecourselinear: LinearLayout, tag: String) {
        Log.d(LOG + "setCourseCard", "setCourseCard")
        courses.forEach { course ->
            val cardView = layoutInflater.inflate(R.layout.coursecard, homecourselinear, false)
            val courseName = cardView.findViewById<TextView>(R.id.courseName)
            val courseDesc = cardView.findViewById<TextView>(R.id.courseDesc)
            val courseIng = cardView.findViewById<TextView>(R.id.courseIng)
            val startTime = cardView.findViewById<TextView>(R.id.startTime)
            val endTime = cardView.findViewById<TextView>(R.id.endTime)
            val divide = cardView.findViewById<View>(R.id.courseDiv)

            val startTimeN = courseTimeList.filter { it.node == course.startNode }
            val endTimeN = courseTimeList.filter { it.node == course.endNode }
            val startTimeStr = startTimeN[0].startTime
            val endTimeStr = endTimeN[0].endTime

            startTime.text = startTimeStr
            endTime.text = endTimeStr
            val minutediff = calculateMinutesDifference(startTimeStr)
            Log.d(LOG + "setCourseCardminutesDiff", minutediff.toString())
            when {
                minutediff < 0 -> {
                    val minuteEnd = calculateMinutesDifference(endTimeStr)
                    if (minuteEnd > 0) {
                        courseIng.text = "进行中"
                        courseIng.background = requireContext().getDrawable(R.drawable.red_line)
                        divide.background = requireContext().getDrawable(R.drawable.red_line)
                    } else {
                        courseIng.text = "已结束"
                        courseIng.background = requireContext().getDrawable(R.drawable.gray_line)
                        startTime.setTextColor(requireContext().getColor(R.color.courseGray))
                        endTime.setTextColor(requireContext().getColor(R.color.courseGray))
                        courseName.setTextColor(requireContext().getColor(R.color.courseGray))
                        courseDesc.setTextColor(requireContext().getColor(R.color.courseGray))
                        divide.background = requireContext().getDrawable(R.drawable.gray_line)
                        courseName.setTypeface(null, Typeface.NORMAL)
                        courseIng.setTypeface(null, Typeface.NORMAL)
                    }
                }
                minutediff > 60 -> {
                    courseIng.text = "未开始"
                    courseIng.background = requireContext().getDrawable(R.drawable.green_line)
                    divide.background = requireContext().getDrawable(R.drawable.green_line)
                }
                else -> {
                    courseIng.text = "${minutediff}分后"
                    courseIng.background = requireContext().getDrawable(R.drawable.blue_line)
                    startTime.setTextColor(requireContext().getColor(R.color.courseWillGray))
                    endTime.setTextColor(requireContext().getColor(R.color.courseWillGray))
                    courseName.setTextColor(requireContext().getColor(R.color.courseWillGray))
                    courseDesc.setTextColor(requireContext().getColor(R.color.courseWillGray))
                    courseIng.setTextColor(requireContext().getColor(R.color.courseWillGray))
                    divide.background = requireContext().getDrawable(R.drawable.blue_line)
                }
            }
            courseName.text = course.name
            courseDesc.text = "${course.teacher} ${course.room} - 第${course.startNode}-${course.endNode}节"
            cardView.tag = tag
            homecourselinear.addView(cardView)
        }
    }
}