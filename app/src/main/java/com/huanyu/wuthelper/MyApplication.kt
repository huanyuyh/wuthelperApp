package com.huanyu.wuthelper

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.util.Log
import com.huanyu.wuthelper.activity.CourseListActivity
import com.huanyu.wuthelper.activity.TaskActivity
import com.huanyu.wuthelper.activity.ToolWebActivity
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User
import com.huanyu.wuthelper.utils.CustomHttps.Companion.oKHttpGet
import com.huanyu.wuthelper.utils.SPTools.Companion.getUnitDate
import com.huanyu.wuthelper.utils.SPTools.Companion.getUpdateUrl
import com.huanyu.wuthelper.utils.SPTools.Companion.getWeekCount
import com.huanyu.wuthelper.utils.SPTools.Companion.putUnitDate
import com.huanyu.wuthelper.utils.SPTools.Companion.putUpdateUrl
import com.huanyu.wuthelper.utils.SPTools.Companion.putWeekCount


class MyApplication : Application() {
    // Companion object 方便全局访问
    companion object {
        private const val LOG = "MyApplication:"
        fun newInstance() = Application()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("$LOG onCreate", "应用创建")
        // 启动新线程执行初始化任务
        Thread {
            Log.d("$LOG Thread", "初始化应用")
            initUnitDate()
            initCourseTimeTable()
            initUserTable()
            initPlatformTable()
            setShortCut()

        }.start()

    }

    // 设置应用快捷方式
    private fun setShortCut() {
        Log.d("$LOG init", "设置快捷方式")
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        val courseShortcut = ShortcutInfo.Builder(this, "course_list_id")
            .setShortLabel("全部课程")
            .setLongLabel("查看全部课程")
            .setIcon(Icon.createWithResource(this, R.drawable.item_course))
            .setIntent(Intent(this, CourseListActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            })
            .build()
        val taskShortcut = ShortcutInfo.Builder(this, "task_list_id")
            .setShortLabel("全部任务")
            .setLongLabel("查看全部任务")
            .setIcon(Icon.createWithResource(this, R.drawable.item_task))
            .setIntent(Intent(this, TaskActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            })
            .build()
        val wifiShortcut = ShortcutInfo.Builder(this, "wifi_login_id")
            .setShortLabel("校园网(网页)")
            .setLongLabel("校园网认证(网页)")
            .setIcon(Icon.createWithResource(this, R.drawable.wifi))
            .setIntent(Intent(this, ToolWebActivity::class.java).apply {
                putExtra("webUrl","http://1.1.1.1")
                putExtra("action","wifiLogin")
                action = Intent.ACTION_VIEW
            })
            .build()
        shortcutManager.dynamicShortcuts = listOf(courseShortcut, taskShortcut, wifiShortcut)
    }

    override fun onTerminate() {
        Log.d("$LOG onTerminate", "应用终止")
        super.onTerminate()
    }

    // 初始化平台表
    private fun initPlatformTable() {
        val platformDao = UserDatabase.getDatabase(this).PlatformDao()
        if (platformDao.getPlatformsCount() < 1) {
            Log.d("$LOG init", "初始化平台表")
            platformDao.deleteAllPlatforms()
            val platList = mutableListOf(
                Platform(0, "智慧理工大", "http://zhlgd.whut.edu.cn/", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click();\n", "#1e78b3"),
                Platform(0, "教务系统（智慧理工大）", "http://sso.jwc.whut.edu.cn/Certification/index2.jsp", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#447ba7"),
                Platform(0, "教务系统", "http://sso.jwc.whut.edu.cn/Certification/toIndex.do", "教务系统", "document.getElementById('username').value ='';\n" +
                        "document.getElementById('password').value ='';\n" +
                        "setTimeout(function() {\n" +
                        "document.getElementById('submit_id').click()\n" +
                        "}, 1000);\n", "#447ba7"),
                Platform(0, "教务系统(新)", "http://jwxt.whut.edu.cn", "智慧理工大", "document.getElementById('tyrzBtn').click();\n"+
                        "setTimeout(function() {\n" +
                        "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n"+
                        "}, 1000);\n", "#2d6dff"),
                Platform(0, "网络教学平台", "https://jxpt.whut.edu.cn/meol/homepage/common/sso_login.jsp", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#2d6dff"),
                Platform(0, "缴费平台", "http://cwsf.whut.edu.cn/casLogin", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click();\n", "#f9a63b"),
                Platform(0, "智慧学工", "https://talent.whut.edu.cn/", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#678cd8"),
                Platform(0, "校园地图（智慧理工大版）", "http://gis.whut.edu.cn/index.shtml", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#059490"),
                Platform(0, "校园地图（微校园版）", "http://gis.whut.edu.cn/mobile/index.html#/", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#059490"),
                Platform(0, "WebVPN", "https://webvpn.whut.edu.cn/", "校园网认证(WLAN)", "document.getElementsByName('username')[0].value ='';\n" +
                        "document.getElementsByName('password')[0].value ='';\n" +
                        "document.getElementsByName('remember_cookie')[0].click()\n" +
                        "document.getElementById('login').click()\n", "#2881fb"),
                Platform(0, "学校邮箱", "https://mail.whut.edu.cn/", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#9cd4fb"),
                Platform(0, "学校邮箱(163)", "https://qy.163.com/login/", "学校邮箱", "document.getElementById('accname').value ='';\n" +
                        "document.getElementById('accpwd').value ='';\n" +
                        "document.getElementById('accautologin').checked\n" +
                        "document.getElementsByClassName('u-logincheck logincheck js-logincheck js-loginPrivate loginPrivate')[0].click()\n" +
                        "document.getElementsByClassName('w-button w-button-account js-loginbtn')[0].click()\n", "#9cd4fb"),
                Platform(0, "成绩查询", "http://zhlgd.whut.edu.cn/tp_up/view?m=up#act=up/sysintegration/queryGrade", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click()\n", "#1e78b3"),
                Platform(0, "校园主页", "http://i.whut.edu.cn/", "null", "null", "#1e78b3"),
                Platform(0, "理工智课", "http://zhlgd.whut.edu.cn/tpass/login?service=https%3A%2F%2Fwhut.ai-augmented.com%2Fapi%2Fjw-starcmooc%2Fuser%2Fcas%2Flogin%3FschoolCertify%3D10497%26rememberme%3Dfalse", "智慧理工大", "document.getElementById('un').value ='';\n" +
                        "document.getElementById('pd').value ='';\n" +
                        "document.getElementById('index_login_btn').click();\n", "#fcb43f"),
            )
            platformDao.inserts(platList)
        }
    }

    // 初始化配置数据
    private fun initUnitDate() {
        Log.d("$LOG init", "初始化 config.xml")
        if (getUnitDate(this) == "null") {
            Log.d("$LOG init", "unitDate")
            putUnitDate(this, "2024-02-26")
        }
        if (getWeekCount(this) == 0) {
            Log.d("$LOG init", "weekCount")
            putWeekCount(this, 19)
        }
        if(getUpdateUrl(this).isEmpty()){
            putUpdateUrl(this, listOf(
                "https://www.212314.xyz",
                "https://www.wuthelper.top"
            ))
        }
    }

    // 初始化课程时间表
    private fun initCourseTimeTable() {
        val courseTimeDao = CoursesDatabase.getDatabase(this).courseTimeDao()
        if (courseTimeDao.getCourseTimeCount() < 1) {
            Log.d("$LOG init", "初始化课程时间表")
            val courseTimes: List<CourseTime> = mutableListOf(
                CourseTime(0, 1, "08:00", "08:45"),
                CourseTime(0, 2, "08:50", "09:35"),
                CourseTime(0, 3, "09:55", "10:40"),
                CourseTime(0, 4, "10:45", "11:30"),
                CourseTime(0, 5, "11:35", "12:20"),
                CourseTime(0, 6, "14:00", "14:45"),
                CourseTime(0, 7, "14:50", "15:35"),
                CourseTime(0, 8, "15:40", "16:25"),
                CourseTime(0, 9, "16:45", "17:30"),
                CourseTime(0, 10, "17:35", "18:20"),
                CourseTime(0, 11, "19:00", "19:45"),
                CourseTime(0, 12, "19:50", "20:35"),
                CourseTime(0, 13, "20:40", "21:25"),
            )
            courseTimeDao.inserts(courseTimes)
        }
    }

    // 初始化用户表，保留多用户扩展
    private fun initUserTable() {
        val userDao = UserDatabase.getDatabase(this).UserDao()
        if (userDao.getUsersCount() < 1) {
            Log.d("$LOG init", "初始化用户表")
            val userList = mutableListOf(
                User(0, "智慧理工大", "", ""),
                User(0, "教务系统", "", ""),
                User(0, "校园网认证(WLAN)", "", ""),
                User(0, "学校邮箱", "", ""),
            )
            userDao.inserts(userList)
        }
    }
}