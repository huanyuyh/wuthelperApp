package com.huanyu.wuthelper.activity

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityUserGuideBinding
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.file.FileSchemeHandler
import okhttp3.OkHttpClient
import java.io.IOException


class UserGuideActivity : AppCompatActivity() {
    lateinit var _binding: ActivityUserGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityUserGuideBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val markdownText = loadMarkdownFromAssets("user_guide.md")


        // 创建Markwon实例并启用图片支持
        val markwon = Markwon.builder(this)
            .usePlugin(HtmlPlugin.create())
            .usePlugin(ImagesPlugin.create { plugin ->
                plugin.addSchemeHandler(FileSchemeHandler.createWithAssets(this))
            })
            .build()
        if (markdownText != null) {
            markwon.setMarkdown(_binding.guideText, markdownText)
            _binding.guideText.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    // 从assets文件夹加载Markdown文件
    private fun loadMarkdownFromAssets(fileName: String): String? {
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}