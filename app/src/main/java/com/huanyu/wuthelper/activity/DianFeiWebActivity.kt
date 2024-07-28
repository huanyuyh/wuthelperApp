package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.MyApplication
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.databinding.ActivityDianFeiWebBinding
import com.huanyu.wuthelper.utils.SPTools.Companion.putWUTFeeCookie

class DianFeiWebActivity : AppCompatActivity() {
    companion object{
        private val LOG_DianFeiWebActivity = "DianFeiWebActivity:"
    }
    lateinit var webView: WebView
    lateinit var _binding:ActivityDianFeiWebBinding
    var isJsDo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_DianFeiWebActivity +"onCreate" ,"onCreate")
        super.onCreate(savedInstanceState)
        _binding = ActivityDianFeiWebBinding.inflate(layoutInflater)
        webView = _binding.dianfeiWeb
        webView.loadUrl("http://cwsf.whut.edu.cn/casLogin")
        initWebView(webView,this)
        enableEdgeToEdge()
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun updateScript(baseScript: String, firstValue: String, secondValue: String): String {
        Log.d(LOG_DianFeiWebActivity +"updateScript" ,baseScript+firstValue+secondValue)
        // 用于替换.value=''的正则表达式
        val regex = """\.value\s*=\s*''""".toRegex()

        // 将第一个.value=''替换为.firstValue
        val updatedScript = regex.replaceFirst(baseScript, ".value='$firstValue'")

        // 将第二个.value=''替换为.secondValue

        var returnScript = regex.replaceFirst(updatedScript, ".value='$secondValue'")
        Log.d(LOG_DianFeiWebActivity +"updateScriptReturn" ,returnScript)
        return returnScript
    }
    @SuppressLint("JavascriptInterface")
    fun initWebView(webView: WebView, activity: Activity){
        Log.d(LOG_DianFeiWebActivity +"initWebView" ,"initWebView")
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if(isJsDo<5){
                    Log.d(LOG_DianFeiWebActivity +"onPageFinished" ,"isJsDo<5")
                    Thread(Runnable {
                        var user = UserDatabase.getDatabase(activity).UserDao().getUserByPlatform("智慧理工大")
                        var platform = UserDatabase.getDatabase(activity).PlatformDao().getPlatformByName("缴费平台")

                        var myJs = updateScript(platform.webJs,user.name,user.pass)
                        Log.d(LOG_DianFeiWebActivity+"myJs",platform.webJs)
                        runOnUiThread {
                            view?.evaluateJavascript(myJs, ValueCallback {
                            } )
                        }
                    }).start()
                    isJsDo ++
                }
                view?.title?.let {
                    if(it.contains("登录")){
                        Log.d(LOG_DianFeiWebActivity +"view?.title?.let" ,"登录")
                        var js = "setTimeout(formSubmit, 1000);"
                        Log.d(LOG_DianFeiWebActivity+"dosetTimeoutformSubmit","dosetTimeout")
                        view?.evaluateJavascript(js, ValueCallback {
                        } )
                    }
                }
                val cookieManager: CookieManager = CookieManager.getInstance()
                url?.let {
                    if(url.contains("showPublic")){
                        Log.d(LOG_DianFeiWebActivity +"url.contains" ,"showPublic")
                        val cookies: String = cookieManager.getCookie(url)?:""
                        if(cookies.contains("JSESSIONID")){
                            if(cookies.substring(cookies.indexOf("JSESSIONID")+"JSESSIONID".length+1)
                                    .contains("JSESSIONID")){
                                Log.d(LOG_DianFeiWebActivity+"difeicookie",cookies)
                                putWUTFeeCookie(baseContext,cookies)
                                val returnIntent = Intent()
                                returnIntent.putExtra("key", "finish")
                                setResult(Activity.RESULT_OK, returnIntent)
                                activity.finish()
                            }
                        }
                    }
                }


                super.onPageFinished(view, url)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(LOG_DianFeiWebActivity +"onPageStarted" ,"onPageStarted")
                view?.evaluateJavascript("var temp = alert;\n" +
                        "alert=null;\n" +
                        "alert(1);\n" +
                        "alert=temp; ", ValueCallback {

                } )
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
            userAgentString = "Mozilla/5.0 (Linux; Android 7.0; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/48.0.2564.116 Mobile Safari/537.36 T7/10.3 SearchCraft/2.6.2 (Baidu; P1 7.0)"
            setDomStorageEnabled(true)
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }
}