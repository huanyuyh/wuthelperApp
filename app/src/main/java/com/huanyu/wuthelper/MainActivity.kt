package com.huanyu.wuthelper

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.huanyu.wuthelper.activity.AboutUsActivity
import com.huanyu.wuthelper.activity.CheckUpdateActivity
import com.huanyu.wuthelper.adapter.MyFragmentPageAdapter
import com.huanyu.wuthelper.databinding.ActivityMainBinding
import com.huanyu.wuthelper.fragment.HomeFragment
import com.huanyu.wuthelper.fragment.MyFragment
import com.huanyu.wuthelper.fragment.NewCourseFragment
import com.huanyu.wuthelper.fragment.NewMyFragment
import com.huanyu.wuthelper.fragment.ServiceFragment
import com.huanyu.wuthelper.utils.CustomHttps.Companion.getMsg
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myAlertDialog
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myNotification
import com.huanyu.wuthelper.utils.CustomUIs.Companion.showNotificationWithActivity
import com.huanyu.wuthelper.utils.SPTools.Companion.getFirstUseApp
import com.huanyu.wuthelper.utils.SPTools.Companion.putFirstUseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        private const val LOG = "MainActivity:"
        const val SHOW_PAGE = "showPage"
        const val RC_NOTIFICATION_PERMISSION = 123
    }

    // 使用 lateinit 延迟初始化变量
    lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG + "onCreate", "onCreate")
        super.onCreate(savedInstanceState)

        // 启用边缘到边缘的界面布局
        enableEdgeToEdge()

        // 使用 ViewBinding 加载布局
        binding = ActivityMainBinding.inflate(layoutInflater)

        // 初始化 ViewModel
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // 设置内容视图为绑定的根视图
        setContentView(binding.root)

        // 设置窗口插入监听器，以适配系统栏（如状态栏和导航栏）
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(getFirstUseApp(this)){
            putFirstUseApp(this,false)
            val intent = Intent(this,AboutUsActivity::class.java)
            startActivity(intent)
        }
        // 请求通知权限
        requestNotificationPermission()

        // 创建 Fragment 列表
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(NewCourseFragment.newInstance())
        fragmentList.add(ServiceFragment.newInstance())
        fragmentList.add(HomeFragment.newInstance())
        fragmentList.add(MyFragment.newInstance())
        fragmentList.add(NewMyFragment.newInstance())
        Log.d(LOG + "fragment list", fragmentList.toString())

        // 设置 ViewPager2 的适配器
        binding.mainViewPage.adapter = MyFragmentPageAdapter(fragmentList, this)
        binding.mainViewPage.isUserInputEnabled = false // 禁用用户手动滑动
        binding.mainViewPage.offscreenPageLimit = 4 // 预加载页面数量

        // 设置初始显示的页面
        val pageId = intent.getIntExtra(SHOW_PAGE, 2)
        binding.mainViewPage.setCurrentItem(pageId, false)

        // 初始化当前选中的底部导航项
        var currentNav = binding.bottomNav3
        currentNav.isSelected = true

        // 设置底部导航按钮点击事件
        binding.bottomNav1.setOnClickListener {
            Log.d(LOG + "bottomNav1", "OnClick")
            binding.mainViewPage.setCurrentItem(0, false)
            currentNav.isSelected = false
            currentNav = binding.bottomNav1
            currentNav.isSelected = true
        }
        binding.bottomNav2.setOnClickListener {
            Log.d(LOG + "bottomNav2", "OnClick")
            binding.mainViewPage.setCurrentItem(1, false)
            currentNav.isSelected = false
            currentNav = binding.bottomNav2
            currentNav.isSelected = true
        }
        binding.bottomNav3.setOnClickListener {
            Log.d(LOG + "bottomNav3", "OnClick")
            binding.mainViewPage.setCurrentItem(2, false)
            currentNav.isSelected = false
            currentNav = binding.bottomNav3
            currentNav.isSelected = true
        }
        binding.bottomNav4.setOnClickListener {
            Log.d(LOG + "bottomNav4", "OnClick")
            binding.mainViewPage.setCurrentItem(3, false)
            currentNav.isSelected = false
            currentNav = binding.bottomNav4
            currentNav.isSelected = true
        }
        binding.bottomNav5.setOnClickListener {
            Log.d(LOG + "bottomNav5", "OnClick")
            binding.mainViewPage.setCurrentItem(4, false)
            currentNav.isSelected = false
            currentNav = binding.bottomNav5
            currentNav.isSelected = true
        }

        // 设置 ViewPager2 页面切换回调
        binding.mainViewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.d(LOG + "onPageSelected", position.toString())
                when (position) {
                    0 -> {
                        currentNav.isSelected = false
                        currentNav = binding.bottomNav1
                        currentNav.isSelected = true
                    }
                    1 -> {
                        currentNav.isSelected = false
                        currentNav = binding.bottomNav2
                        currentNav.isSelected = true
                    }
                    2 -> {
                        currentNav.isSelected = false
                        currentNav = binding.bottomNav3
                        currentNav.isSelected = true
                    }
                    3 -> {
                        currentNav.isSelected = false
                        currentNav = binding.bottomNav4
                        currentNav.isSelected = true
                    }
                    4 -> {
                        currentNav.isSelected = false
                        currentNav = binding.bottomNav5
                        currentNav.isSelected = true
                    }
                }
                super.onPageSelected(position)
            }
        })
    }
    @AfterPermissionGranted(RC_NOTIFICATION_PERMISSION)
    private fun requestNotificationPermission() {
        val perms = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // 已经获得权限，进行网络请求获取公告
            fetchAnnouncement()
        } else {
            // 请求权限
            EasyPermissions.requestPermissions(
                this,
                "需要通知权限来显示公告",
                RC_NOTIFICATION_PERMISSION,
                *perms
            )
        }
    }
    private fun fetchAnnouncement() {
        var context = this
        CoroutineScope(Dispatchers.IO).launch {
            getMsg(context, onUpdate = {title,msg,time->

                CoroutineScope(Dispatchers.Main).launch{
                    myAlertDialog(context,title,"${msg}\n${time}", onClick = {})
//                    myNotification(context,title,msg)
//                    showNotificationWithActivity(context,title,msg,CheckUpdateActivity::class.java)
//                    val customLayout = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
//                    val customTitle = customLayout.findViewById<TextView>(R.id.custom_alert_title)
//                    val customMessage = customLayout.findViewById<TextView>(R.id.custom_alert_message)
//                    val customButton = customLayout.findViewById<Button>(R.id.custom_alert_button)
//                    customTitle.text = title
//                    customMessage.text = msg
//                    val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
//                        .setView(customLayout)
//                        .create()
//                    customButton.setOnClickListener {
//                        dialog.dismiss()
//                    }
//
//                    dialog.show()
                }

//                Snackbar.make(findViewById(android.R.id.content), title+msg, Snackbar.LENGTH_LONG)
//                    .setAction("确定") {
//                        // 用户点击操作
//                    }.show()
//                val builder: Notification.Builder? = Notification.Builder(context, "wuthelper")
//                    .setSmallIcon(R.drawable.appicon)
//                    .setContentTitle(title)
//                    .setContentText(msg)
//                    .setPriority(Notification.PRIORITY_DEFAULT)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val channel = NotificationChannel(
//                        "wuthelper",
//                        "wuthelper",
//                        NotificationManager.IMPORTANCE_DEFAULT
//                    )
//                    notificationManager.createNotificationChannel(channel)
//                }
//
//                if (builder != null) {
//                    notificationManager.notify(1, builder.build())
//                }
            })
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 将结果传递给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_NOTIFICATION_PERMISSION) {
            // 权限被授予，进行网络请求获取公告
            fetchAnnouncement()
            Log.d(LOG,"权限授予")
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_NOTIFICATION_PERMISSION) {
            // 权限被拒绝，处理拒绝逻辑
        }
    }
}
