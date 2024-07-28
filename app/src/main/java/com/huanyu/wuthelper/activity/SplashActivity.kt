package com.huanyu.wuthelper.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock.sleep
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.wuthelper.MainActivity
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        val versionName = packageInfo.versionName
        binding.versionBy.text = "version:${versionName} by.huanyu"
        Thread(){
            run {
                sleep(300)
                try {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }.start()
//        Handler().postDelayed({
//            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 300) // 3秒后跳转到主界面
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



    }
}