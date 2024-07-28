package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.databinding.ActivityToolWebBinding
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.utils.CustomHttps.Companion.updateScript
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myAlertDialogWithTwoBtn
import com.huanyu.wuthelper.utils.FileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToolWebActivity : AppCompatActivity() {
    companion object{
        private val LOG = "ToolWebActivity"
    }
    lateinit var webView: WebView
    var webPlatform:String = ""
    var userPlat:String = ""
    var webUser:String = ""
    var webPass:String = ""
    var webUrl:String = "http://1.1.1.1"
    var action:String = ""
    var webJs = ""
    lateinit var _binding: ActivityToolWebBinding
    lateinit var viewModel: ToolWebViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityToolWebBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ToolWebViewModel::class.java]
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        webView = _binding.wfiWeb
        val intent = intent
        intent?.let {
            webPlatform = it.getStringExtra("webPlatform")?:""
            webUser = it.getStringExtra("webUser")?:""
            webPass = it.getStringExtra("webPass")?:""
            webUrl = it.getStringExtra("webUrl")?:"http://1.1.1.1"
            action = it.getStringExtra("action")?:""
            Log.d(LOG,action)
        }
        var wifiPlat = Platform(0,
            "校园网认证(WLAN)",
            "http://172.30.21.100",
            "校园网认证(WLAN)",
            "document.getElementById('username').value ='';\n" +
                    "document.getElementById('password').value ='';\n" +
                    "checkForm()\n",
            "")
        var activity =this
        CoroutineScope(Dispatchers.IO).launch{
            if(action.contains("wifiLogin")){
                webJs = wifiPlat.webJs
                webPlatform = wifiPlat.platName
                userPlat = wifiPlat.userPLat
            }else{
                var platform = UserDatabase.getDatabase(activity).PlatformDao().getPlatformByName(webPlatform)
                webJs = platform.webJs
                userPlat = platform.userPLat
            }
                var user = UserDatabase.getDatabase(activity).UserDao().getUserByPlatform(userPlat)
                webUser = user.name
                webPass = user.pass

            launch(Dispatchers.Main) {
                Log.d("wifilogin",webUrl)
                initWebView(webView,webJs,webUser,webPass)
                webView.loadUrl(webUrl)
            }
        }


    }
    @SuppressLint("JavascriptInterface")
    fun initWebView(webView: WebView,js:String,name:String,pass:String){
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {

                var myJs = updateScript(js,name,pass)
                Log.d("weblogin",myJs)
                view?.evaluateJavascript(myJs, ValueCallback {

                } )
                if(action.contains("importClass")){
                    val downloadjs = "var context = '<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>';\n" +
                            "console.log(context);"
                    Log.d(LOG +"downloadjs" ,downloadjs)
                    view?.evaluateJavascript(downloadjs, ValueCallback {
                    })
                }

                super.onPageFinished(view, url)
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
                        Log.d(LOG +"onConsoleMessage" ,"欢迎")
                        Log.d("html=", consoleMessage.message())
                        FileUtil.saveText(path = viewModel.path, text = consoleMessage.message()?:"")
                        if (!isFinishing && !isDestroyed) {
                            myAlertDialogWithTwoBtn(this@ToolWebActivity,"导入课程表","确认导入课程表吗？","确认","取消", onOkClick = {
                                viewModel.importClass(this@ToolWebActivity)
                            }, onCancelClick = {

                            })
                        }

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
    private fun showPopup() {
        Log.d(LOG +"showPopup" ,"showPopup")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("导入课程表")
        builder.setMessage("确认导出课程表吗？")
        builder.setPositiveButton("确定",
            DialogInterface.OnClickListener { dialog, which ->
                Log.d(LOG +"DialogInterface" ,"确定")
                viewModel.importClass(this)


            })
        builder.setNegativeButton("取消",
            DialogInterface.OnClickListener { dialog, which ->
                Log.d(LOG +"DialogInterface" ,"取消")
                // 在这里处理点击取消按钮的事件
                this.finish()
            })
        if (!isFinishing && !isDestroyed) {
            builder.create().show()
        }
    }
}