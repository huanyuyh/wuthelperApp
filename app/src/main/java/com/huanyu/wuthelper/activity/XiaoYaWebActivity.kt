package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.CourseTaskDatabase
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.databinding.ActivityXiaoYaWebBinding
import com.huanyu.wuthelper.utils.CustomHttps.Companion.getXiaoYaTasks

class XiaoYaWebActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var _binding:ActivityXiaoYaWebBinding
    private var cookies:String = ""
    private var authorization:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityXiaoYaWebBinding.inflate(layoutInflater)
        webView = _binding.dianfeiWeb
        webView.loadUrl("http://zhlgd.whut.edu.cn/tpass/login?service=https%3A%2F%2Fwhut.ai-augmented.com%2Fapi%2Fjw-starcmooc%2Fuser%2Fcas%2Flogin%3FschoolCertify%3D10497%26rememberme%3Dfalse")
        initWebView(webView,this)
        enableEdgeToEdge()
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding.floatingActionButtonimport.setOnClickListener {
            var js = "document.getElementsByClassName(\"circle\")[1].click();" +
                    "setTimeout(function(){\n" +
                    "document.getElementsByClassName(\"ant-radio-inner\")[1].click();" +
                    "}, 1000);"
            webView.evaluateJavascript(js, ValueCallback {
                Log.d("xiaoya",it)
            } )

            Thread(kotlinx.coroutines.Runnable {
                val courseTaskDao = CourseTaskDatabase.getDatabase(this).courseTaskDao()
                courseTaskDao.deleteAllByPlatForm("小雅")
                getXiaoYaTasks(this, onSuccess = {
                    runOnUiThread {
                        finish()
                    }
                })
            }).start()

        }
    }
    fun updateScript(baseScript: String, firstValue: String, secondValue: String): String {
        // 用于替换.value=''的正则表达式
        val regex = """\.value\s*=\s*''""".toRegex()

        // 将第一个.value=''替换为.firstValue
        val updatedScript = regex.replaceFirst(baseScript, ".value='$firstValue'")

        // 将第二个.value=''替换为.secondValue
        return regex.replaceFirst(updatedScript, ".value='$secondValue'")
    }
    var isJsDo = 0
    @SuppressLint("JavascriptInterface")
    fun initWebView(webView: WebView, activity: Activity){
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if(isJsDo<5){
                    Thread(Runnable {
                        var user = UserDatabase.getDatabase(activity).UserDao().getUserByPlatform("智慧理工大")
                        var platform = UserDatabase.getDatabase(activity).PlatformDao().getPlatformByName("理工智课")

                        var myJs = updateScript(platform.webJs,user.name,user.pass)
                        Log.d("dianfeiweb",platform.webJs)
                        runOnUiThread {
                            view?.evaluateJavascript(myJs, ValueCallback {
                            } )
                        }
                    }).start()
                    isJsDo ++
                }
//                var userList = MyApplication.userDBHelper.queryAllUserInfo()
//                var user = userList.filter { it.platform == "智慧理工大" }
//                var platform = MyApplication.userDBHelper.queryPlatformInfoByplatName("理工智课")
//                var myJs = updateScript(platform.webJs,user[0].name,user[0].pass)
//                Log.d("dianfei",myJs)
//                view?.evaluateJavascript(myJs, ValueCallback {
//
//                } )
                val cookieManager: CookieManager = CookieManager.getInstance()
                cookies = cookieManager.getCookie(url)?:""
                println("cookie:${cookies}")
                view?.let { view ->
                    view.title?.let {
                        Log.d("xiaoya",it)
                        if(it.contains("小雅")){

                        }

                    }

                }



                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                request?.let {

                    // 打印请求的 URL
                    println("Request URL: ${it.url}")

                    // 打印请求头信息
                    for ((key, value) in it.requestHeaders) {
                        if(key.contains("authorization")){
                            authorization = value
                            println("Request Header: $key: $value")
                        }
                        if(key.contains("Cookie")){
                            println("Request Header: $key: $value")
                        }
                    }

                    // 示例：如果需要修改请求，可以在这里创建新的请求
                    // 或者使用返回自定义的 WebResourceResponse 来替代请求的响应
                    // return WebResourceResponse(mimeType, encoding, data)
                }
                return super.shouldInterceptRequest(view, request)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)

            }
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                //自行处理....
            }
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed();
                super.onReceivedSslError(view, handler, error)
            }
        }
        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    if(consoleMessage.message().contains("欢迎")) {

                    }

                }
                return super.onConsoleMessage(consoleMessage)
            }
        }
        webView.settings.apply {
            //支持js交互
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls =false
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            defaultTextEncodingName = "utf-8"
            userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36"

            setDomStorageEnabled(true)
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }
}