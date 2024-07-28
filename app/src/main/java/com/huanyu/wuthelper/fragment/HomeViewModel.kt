package com.huanyu.wuthelper.fragment

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson2.JSONObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.database.CourseTaskDatabase
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.database.OneWordDatabase
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.entity.OneWord
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.getCurrentDateTime
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.yearMonthDaysDifference
import com.huanyu.wuthelper.utils.CustomHttps.Companion.getXiaoYaTasks
import com.huanyu.wuthelper.utils.CustomHttps.Companion.oKHttpGet
import com.huanyu.wuthelper.utils.CustomHttps.Companion.oKHttpPost
import com.huanyu.wuthelper.utils.CustomHttps.Companion.wutDianFeeGetAndTryByLogin
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOG = "HomeViewModel:"
    }

    lateinit var courseTime: List<CourseTime>
    private var _currentWeek: MutableLiveData<Int> = MutableLiveData(1)
    val currentWeek: LiveData<Int> get() = _currentWeek
    private var _oneWord: MutableLiveData<String> = MutableLiveData("永远相信美好的事情将会发生")
    val oneWord: LiveData<String> get() = _oneWord
    private var _dianFei: MutableLiveData<String> = MutableLiveData("")
    val dianFei: LiveData<String> get() = _dianFei
    private var _weather: MutableLiveData<String> = MutableLiveData("")
    val weather: LiveData<String> get() = _weather
    private var _courseTask: MutableLiveData<List<CourseTask>> = MutableLiveData()
    val courseTask: LiveData<List<CourseTask>> get() = _courseTask

    private var _morningCourses: MutableLiveData<List<Course>> = MutableLiveData()
    val morningCourses: LiveData<List<Course>> get() = _morningCourses
    private var _afternoonCourse: MutableLiveData<List<Course>> = MutableLiveData()
    val afternoonCourse: LiveData<List<Course>> get() = _afternoonCourse
    private var _nightCourse: MutableLiveData<List<Course>> = MutableLiveData()
    val nightCourse: LiveData<List<Course>> get() = _nightCourse
    private var _isHaveCourse: MutableLiveData<Boolean> = MutableLiveData(true)
    val isHaveCourse: LiveData<Boolean> get() = _isHaveCourse

    private val client = OkHttpClient()

    init {
        Log.d(LOG, "ViewModel 初始化")
    }

    fun setCurrentWeek() {
        Log.d(LOG + "setCurrentWeek", "setCurrentWeek()")
        val startDate = getUnitDate(getApplication())
        Log.d(LOG + "setCurrentWeek", startDate)
        val diffDate = yearMonthDaysDifference(startDate)
        Log.d(LOG + "setCurrentWeek", diffDate.toString())
        _currentWeek.value = ((diffDate) / 7 + 1).toInt()
    }

    private var oneWordStr = ""
    private var oneWordFrom = ""
    private var oneWordFromWho = ""
    private var oneWordType = ""
    private var oneWordId = ""
    private var oneWordUuid = ""

    fun getOneWord() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mediaType = "application/x-www-form-urlencoded".toMediaType()
                val body = "c=d".toRequestBody(mediaType)
                val response = oKHttpPost("https://v1.hitokoto.cn/",body)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    launch(Dispatchers.Main) {
                        Log.d(LOG + "oneWord", responseData)
                        val gson = Gson()
                        val type = object : TypeToken<Map<String, String>>() {}.type
                        val respose: Map<String, String> = gson.fromJson(responseData, type)
                        _oneWord.value = respose["hitokoto"] + "--" + respose["from_who"]
                        launch(Dispatchers.IO) {
                            respose["hitokoto"]?.let {
                                oneWordStr = it
                            }
                            respose["from"]?.let {
                                oneWordFrom = it
                            }
                            respose["from_who"]?.let {
                                oneWordFromWho = it
                            }
                            respose["type"]?.let {
                                oneWordType = it
                            }
                            respose["id"]?.let {
                                oneWordId = it
                            }
                            respose["uuid"]?.let {
                                oneWordUuid = it
                            }
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Log.d(LOG + "oneWord", Exception("Error fetching data").toString())
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Log.d(LOG + "oneWord", e.toString())
                }
            }
        }
    }

    fun insertOneWord() {
        viewModelScope.launch(Dispatchers.IO) {
            OneWordDatabase.getDatabase(getApplication()).oneWordDao().insert(
                OneWord(0, oneWordStr, oneWordFrom, oneWordFromWho, oneWordUuid, oneWordId, oneWordType)
            )
        }
    }

    fun deleteOneWord() {
        viewModelScope.launch(Dispatchers.IO) {
            OneWordDatabase.getDatabase(getApplication()).oneWordDao().deleteByID(oneWordId)
        }
    }

    fun getCourseTimes() {
        viewModelScope.launch(Dispatchers.IO) {
            val courseTimeDao = CoursesDatabase.getDatabase(getApplication()).courseTimeDao()
            courseTime = courseTimeDao.getAllCourseTimes()
        }
    }

    fun getWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = oKHttpGet("https://restapi.amap.com/v3/weather/weatherInfo?key=e046aecabbffce51c9211fb60115e1fa&city=420111","","")
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    launch(Dispatchers.Main) {
                        Log.d(LOG + "Weather", responseData)
                        val jsonObject = JSONObject.parseObject(responseData)
                        Log.d(LOG + "Weather", jsonObject.getString("lives"))
                        val livesWeather = jsonObject.getString("lives")
                        val weaJson = JSONObject.parseObject(livesWeather.replace("[", "").replace("]", ""))
                        Log.d(LOG + "Weather", weaJson.getString("weather"))
                        _weather.value = "${weaJson.getFloat("temperature_float")}°C ${weaJson.getString("weather")} ${weaJson.getString("winddirection")}风${weaJson.getString("windpower")}级 洪山区"
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Log.d(LOG + "weather", Exception("Error fetching data").toString())
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Log.d(LOG + "weather", e.toString())
                }
            }
        }
    }

    fun setTaskList() {
        Log.d(LOG + "setTaskList", "setTaskList")
        viewModelScope.launch(Dispatchers.IO) {
            val courseTaskDao = CourseTaskDatabase.getDatabase(getApplication()).courseTaskDao()
            val currentTime = getCurrentDateTime()
            val courseTaskList = courseTaskDao.getFirstFiveTasks(currentTime, 5)
            launch(Dispatchers.Main) {
                _courseTask.value = courseTaskList
                Log.d(LOG + "courseTask", _courseTask.value.toString())
            }
        }
    }

    fun setTodayCourse(onFinish: () -> Unit) {
        Log.d(LOG + "setTodayCourse", "setTodayCourse")
        viewModelScope.launch(Dispatchers.IO) {
            val courseDao = CoursesDatabase.getDatabase(getApplication()).courseDao()
            val courses = currentWeek.value?.let { courseDao.getCoursesForWeek(it) }
            onFinish()
            if (courses != null) {
                val todayCourses = courses.filter {
                    it.day == LocalDate.now().dayOfWeek.value
                }

                if (todayCourses.isEmpty()) {
                    launch(Dispatchers.Main) {
                        _isHaveCourse.value = false
                    }
                } else {
                    launch(Dispatchers.Main) {
                        _isHaveCourse.value = true
                        _morningCourses.value = todayCourses.filter {
                            it.endNode <= 5
                        }
                        _afternoonCourse.value = todayCourses.filter {
                            it.startNode >= 6 && it.endNode <= 10
                        }
                        _nightCourse.value = todayCourses.filter {
                            it.startNode >= 11
                        }
                    }
                }
            }
        }
    }

    fun getDianfei(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            wutDianFeeGetAndTryByLogin(context, onSuccess =  { remainPower, _ ->
                launch(Dispatchers.Main) {
                    if (remainPower.toDouble() < 15) {
                        showCustomDialog(
                            context,
                            "当前电费$remainPower 度 小于15度注意缴费23:20-00:10不能缴费"
                        )
                    }
                    _dianFei.value = "电费:$remainPower 度"
                }
            }, onError = {})
            getXiaoYaTasks(context, onSuccess = {})
            setTaskList()
        }
    }

    private fun showCustomDialog(context: Context, msg: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("提示")
        builder.setMessage(msg)
        builder.setPositiveButton("确认") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("关闭") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}


