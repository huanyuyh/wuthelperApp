package com.huanyu.wuthelper.activity

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityCheckUpdateBinding
import com.huanyu.wuthelper.utils.CustomHttps.Companion.getUpdate
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckUpdateActivity : AppCompatActivity() {
    lateinit var _binding:ActivityCheckUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityCheckUpdateBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var context = this
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val stringBuilder = StringBuilder()
        stringBuilder.append("## 当前版本 \n" +
                "$versionName  \n" +
                "## 未获取到新版  \n" +
                "## 下载链接 \n" +
                "[https://www.212314.xyz/download/app-release.apk](https://www.212314.xyz/download/app-release.apk) \n \n" +
                "[https://www.wuthelper.top/download/app-release.apk](https://www.wuthelper.top/download/app-release.apk) \n \n")
        val markdownText =stringBuilder.toString()


        val markwon = Markwon.builder(context)
            .usePlugin(ImagesPlugin.create())
            .usePlugin(ImagesPlugin.create()) // 使用OkHttpImagesPlugin
            .build()
        if (markdownText != null) {
            markwon.setMarkdown(_binding.updateInfo, markdownText)
            _binding.updateInfo.movementMethod = LinkMovementMethod.getInstance()
        }
        CoroutineScope(Dispatchers.IO).launch {
            getUpdate(context, onUpdate = {release_notes,release_url,version->
                launch (Dispatchers.Main){

                    val releaseUrl = release_url.split(",")
                    val releaseNote = release_notes.split(" ")
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("## 当前版本 \n" +
                            "$versionName \n" +
                            "## 最新版本 \n" +
                            "$version \n" +
                            "## 下载链接 \n")
                    releaseUrl.forEach {
                        stringBuilder.append("[$it]($it) \n \n")
                    }
                    stringBuilder.append("## 版本日志 \n"
                            )
                    releaseNote.forEach {
                        stringBuilder.append("$it  \n")
                    }
                    val markdownText =stringBuilder.toString()


                    val markwon = Markwon.builder(context)
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(ImagesPlugin.create()) // 使用OkHttpImagesPlugin
                        .build()
                    if (markdownText != null) {
                        markwon.setMarkdown(_binding.updateInfo, markdownText)
                    }
                }



            }, onLatest = {msg,release_notes,release_url,version->
                launch (Dispatchers.Main){
                    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val versionName = packageInfo.versionName
                    val releaseUrl = release_url.split(",")
                    val releaseNote = release_notes.split(" ")
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("## 当前版本 \n" +
                            "$versionName  \n" +
                            "## $msg  \n" +
                            "## 下载链接 \n")
                    releaseUrl.forEach {
                        stringBuilder.append("[$it]($it) \n \n")
                    }
                    stringBuilder.append("## 版本日志 \n"
                    )
                    releaseNote.forEach {
                        stringBuilder.append("$it  \n")
                    }
                    val markdownText =stringBuilder.toString()


                    val markwon = Markwon.builder(context)
                        .usePlugin(ImagesPlugin.create())
                        .usePlugin(ImagesPlugin.create()) // 使用OkHttpImagesPlugin
                        .build()
                    if (markdownText != null) {
                        markwon.setMarkdown(_binding.updateInfo, markdownText)
                    }
                }
            })
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