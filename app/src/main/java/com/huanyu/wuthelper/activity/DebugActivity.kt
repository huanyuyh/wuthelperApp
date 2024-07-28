package com.huanyu.wuthelper.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityDebugBinding
import com.huanyu.wuthelper.utils.SPTools.Companion.removeDianFeeInfo

class DebugActivity : AppCompatActivity() {
    lateinit var binding: ActivityDebugBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.deleteAllDianFee.setOnClickListener {
            removeDianFeeInfo(this)
        }
        binding.deleteAllCourse.setOnClickListener {

        }
    }
}