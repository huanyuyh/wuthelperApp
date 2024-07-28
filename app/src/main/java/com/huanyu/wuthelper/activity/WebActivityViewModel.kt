package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WebActivityViewModel(application: Application):AndroidViewModel(application) {
    var webUrl:String = "zhlgd.whut.edu.cn"
    var tempUrl:String = "zhlgd.whut.edu.cn"
    lateinit var platform:Platform
    lateinit var context:Context
    lateinit var activity: WebActivity
    lateinit var action:String
    lateinit var path:String
    lateinit var webJavaScript:String
    var webUserName:String = ""
    var webPassWord:String = ""
    var urlFocus:Boolean = false
    val webTitle:LiveData<String>
        get() = _webTitle
    private val _webTitle = MutableLiveData<String>("Hello")
    val progressWeb:LiveData<Int>
        get() = _progressWeb
    private val _progressWeb = MutableLiveData<Int>(-1)
    lateinit var mwebView: WebView
    var isJsDo = 0
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    val FILE_CHOOSER_RESULT_CODE = 1
    fun getPlatformInfo(name:String){
        viewModelScope.launch (Dispatchers.IO){
            platform = UserDatabase.getDatabase(getApplication()).PlatformDao().getPlatformByName(name)
            tempUrl = platform.platUrl
            webUrl = platform.platUrl
            webJavaScript = platform.webJs
            var user = UserDatabase.getDatabase(getApplication()).UserDao().getUserByPlatform(platform.userPLat)
            webUserName = user.name
            webPassWord = user.pass
            Log.d("webActivity",platform.toString())
            Log.d("webActivity",webUserName+webPassWord)
        }
    }
    fun webLoadUrl(){
        mwebView.loadUrl(tempUrl)
    }
    fun webBack(){
        mwebView.goBack()
    }
    fun webForWard(){
        mwebView.goForward()
    }
    fun webHome(){
        mwebView.loadUrl(webUrl)
    }
    fun webRefresh(){
        mwebView.clearCache(true)
        mwebView.reload()
    }

    fun updateScript(baseScript: String, firstValue: String, secondValue: String): String {
        // 用于替换.value=''的正则表达式
        val regex = """\.value\s*=\s*''""".toRegex()

        // 将第一个.value=''替换为.firstValue
        val updatedScript = regex.replaceFirst(baseScript, ".value='$firstValue'")

        // 将第二个.value=''替换为.secondValue
        return regex.replaceFirst(updatedScript, ".value='$secondValue'")
    }


    private fun sendMyBroadcast() {
        val intent = Intent("com.huanyu.RequestCookie")
        intent.putExtra("message", "yes")
        context.sendBroadcast(intent)
    }
    fun extractHTML() {
        mwebView.evaluateJavascript(
            "(function() { return document.documentElement.outerHTML; })();"
        ) { html ->
            // 在这里处理HTML内容
            // html是一个包含整个网页HTML内容的字符串
            Log.d("HTML", html)
            if(html.contains("欢迎")){

            }
        }
    }
    //view?.loadUrl("javascript:window.AndroidInterface.getSource('<head>'+" +
    //                            "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    private class JavaScriptInterface { // 你可以在这里定义其他与JavaScript交互的方法
        @JavascriptInterface
        fun getSource(html: String?) {
            Log.d("html=", html!!)

        }
    }
    @SuppressLint("JavascriptInterface")
    fun initWebView(webView: WebView){
        mwebView = webView
        webView.loadUrl(webUrl)
        webView.addJavascriptInterface(JavaScriptInterface(),"AndroidInterface")
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                _webTitle.value = view?.title
                Log.d("webActivity","onPageFinished")
                if(isJsDo<10){
                    Log.d("webActivity","isJsDo")
                    if(webUserName.length>0&&webPassWord.length>0) {
                        Log.d("webActivity","webUserName.length>0")
                        if (webJavaScript!="null"){
                            Log.d("webActivity","webJavaScript!")
                            var myJs = updateScript(webJavaScript,webUserName,webPassWord)
                            Log.d("webActivity",myJs)
                            view?.evaluateJavascript(myJs, ValueCallback {

                            })
                        }

                    }
                    isJsDo ++
                }
                view?.title?.let {
                    if(it.contains("登录")&&webUrl.contains("cwsf.whut.edu.cn")){
                        var js = "setTimeout(formSubmit, 1000);"
                        Log.d("dianfeiweb","dosetTimeout")
                        view?.evaluateJavascript(js, ValueCallback {
                        } )
                    }
                }


                if(action.contains("import")){
                    val downloadjs = "var context = '<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>';\n" +
                            "console.log(context);"
                    view?.evaluateJavascript(downloadjs, ValueCallback {
                    })
                }


                super.onPageFinished(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                _progressWeb.value = -1
//                view?.evaluateJavascript("var temp = alert;\n" +
//                        "alert=null;\n" +
//                        "alert(1);\n" +
//                        "alert=temp; ", ValueCallback {
//
//                } )
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if(null == request?.url) return false
                val showOverrideUrl = request?.url.toString()
                try {
                    if (!showOverrideUrl.startsWith("http://")
                        && !showOverrideUrl.startsWith("https://")) {
                        //处理非http和https开头的链接地址
                        Intent(Intent.ACTION_VIEW, Uri.parse(showOverrideUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            view?.context?.applicationContext?.startActivity(this)
                        }
                        return true
                    }else{
                        view?.loadUrl(showOverrideUrl)
                        return true
                    }
                }catch (e:Exception){
                    //没有安装和找到能打开(「xxxx://openlink.cc....」、「weixin://xxxxx」等)协议的应用
                    return true
                }
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
                _progressWeb.value = newProgress
                super.onProgressChanged(view, newProgress)
            }
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {


                }
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    activity.startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE)
                } catch (e: Exception) {
                    uploadMessage = null
                    return false
                }
                return true
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