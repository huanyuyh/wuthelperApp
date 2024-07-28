package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.database.CourseTaskDatabase
import com.huanyu.wuthelper.databinding.ActivityMoocWebBinding
import com.huanyu.wuthelper.entity.MoocCourse
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.unixtoDateString
import com.huanyu.wuthelper.utils.FileUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MoocWebActivity : AppCompatActivity() {
    companion object{
        private val LOG_MoocWebActivity = "MoocWebActivity:"
    }
    lateinit var webView: WebView
    lateinit var _binding:ActivityMoocWebBinding
    private var cookies:String = ""
    private var termId:String = ""
    private var csrfKey:String = ""
    var path = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG_MoocWebActivity  +"onCreate" ,"onCreate")
        super.onCreate(savedInstanceState)
        _binding = ActivityMoocWebBinding.inflate(layoutInflater)
        webView = _binding.dianfeiWeb
        webView.loadUrl("https://www.icourse163.org/member/login.htm")
        initWebView(webView,this)
        enableEdgeToEdge()
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        path = this.externalCacheDir!!.path+"/mooc.html"
        _binding.floatingActionButtonRefresh.setOnClickListener {
            Log.d(LOG_MoocWebActivity  +"ButtonRefresh" ,"OnClick")
            Thread(Runnable {
                Log.d(LOG_MoocWebActivity  +"moocrecookies", cookies)
                Log.d(LOG_MoocWebActivity  +"moocrecsrfKey", csrfKey)
                Log.d(LOG_MoocWebActivity  +"moocretermId", termId)
                val moocCourseDao = CourseTaskDatabase.getDatabase(this).moocCourseDao()
                if(moocCourseDao.getCourseCount()>0){
                    Log.d(LOG_MoocWebActivity  +"moocCourseDao", "getCourseCount")
                    var moocCourses = moocCourseDao.getAllCourses()
                    moocCourses.forEach {
                        val client = OkHttpClient()
                        val mediaType = "application/x-www-form-urlencoded".toMediaType()
                        val body = "csrfKey=${csrfKey}&termId=${it.tid}".toRequestBody(mediaType)
                        val request = Request.Builder()
                            .url("https://www.icourse163.org/web/j/courseBean.getLastLearnedMocTermDto.rpc")
                            .post(body)
                            .addHeader("Cookie", cookies)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build()
                        val response = client.newCall(request).execute()
                        val responseData = response.body?.string()?:"responseNull"
                        Log.d(LOG_MoocWebActivity  +"moocre", responseData)
                        val jsonObject: JsonObject = JsonParser.parseString(responseData).asJsonObject
                        val result: JsonObject = jsonObject.get("result").asJsonObject
                        val mocTermDto: JsonObject = result.get("mocTermDto").asJsonObject
                        val group_name: String = mocTermDto.get("courseName").asString
                        Log.d(LOG_MoocWebActivity  +"moocre", group_name)
                        val chapters: JsonArray = mocTermDto.get("chapters").asJsonArray
                        Log.d(LOG_MoocWebActivity  +"moocre", chapters.toString())

                        val courseTaskDao = CourseTaskDatabase.getDatabase(this).courseTaskDao()
                        courseTaskDao.deleteMoocByGroupName(group_name)
                        chapters.forEach {
                            if(!it.asJsonObject.get("homeworks").isJsonNull){
                                var homeworks = it.asJsonObject.get("homeworks").asJsonArray
                                Log.d(LOG_MoocWebActivity  +"moocre", homeworks.toString())
                                homeworks.forEach {
                                    var name = it.asJsonObject.get("name").asString
                                    Log.d(LOG_MoocWebActivity  +"moocre",name)
                                    var viewStatus = it.asJsonObject.get("viewStatus").asInt
                                    Log.d(LOG_MoocWebActivity  +"moocre",viewStatus.toString())
                                    var isFinish = true
                                    if(viewStatus == 0){
                                        isFinish = false
                                    }
                                    var test = it.asJsonObject.get("test").asJsonObject
                                    Log.d(LOG_MoocWebActivity  +"moocre",test.toString())
                                    var deadline = test.asJsonObject.get("deadline").asLong
                                    Log.d(LOG_MoocWebActivity  +"moocre",deadline.toString())
                                    var deadlineDate = unixtoDateString(deadline)
                                    Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                                    var note = ""
                                    try {
                                        var evaluateStart = test.asJsonObject.get("evaluateStart").asLong
                                        var evaluateStartDate = unixtoDateString(evaluateStart)
                                        Log.d(LOG_MoocWebActivity  +"moocre",evaluateStartDate)
                                        var evaluateEnd = test.asJsonObject.get("evaluateEnd").asLong
                                        var evaluateEndDate = unixtoDateString(evaluateEnd)
                                        Log.d(LOG_MoocWebActivity  +"moocre",evaluateEndDate)
                                        note = "评教时间:"+evaluateStartDate+"-"+evaluateEndDate
                                    }catch (e:Exception){
                                        Log.d(LOG_MoocWebActivity  +"error",e.toString())
                                    }
                                    Log.d(LOG_MoocWebActivity  +"moocrenote",note)
//                        var evaluateEnd = it.asJsonObject.get("evaluateEnd").asLong
                                    try {
                                        courseTaskDao.insert(
                                            CourseTask(_id = 0,name = name, platform = "mooc",group_name = group_name, start_time = "", end_time = deadlineDate,
                                                is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = note, isFinish = isFinish)
                                        )
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        Log.d(LOG_MoocWebActivity  +"error",e.toString())
                                    }
                                }
                            }
                            if(!it.asJsonObject.get("quizs").isJsonNull){
                                var quizs = it.asJsonObject.get("quizs").asJsonArray
                                Log.d(LOG_MoocWebActivity  +"moocre", quizs.toString())
                                quizs.forEach {
                                    var name = it.asJsonObject.get("name").asString
                                    Log.d(LOG_MoocWebActivity  +"moocre",name)
                                    var viewStatus = it.asJsonObject.get("viewStatus").asInt
                                    Log.d(LOG_MoocWebActivity  +"moocre",viewStatus.toString())
                                    var isFinish = true
                                    if(viewStatus == 0){
                                        isFinish = false
                                    }
                                    var test = it.asJsonObject.get("test").asJsonObject
                                    Log.d(LOG_MoocWebActivity  +"moocre",test.toString())
                                    var deadline = test.asJsonObject.get("deadline").asLong
                                    Log.d(LOG_MoocWebActivity  +"moocre",deadline.toString())
                                    var deadlineDate = unixtoDateString(deadline)
                                    Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                                    var note = ""
                                    try {
                                        var evaluateStart = test.asJsonObject.get("evaluateStart").asLong
                                        var evaluateStartDate = unixtoDateString(evaluateStart)
                                        Log.d(LOG_MoocWebActivity  +"moocre",evaluateStartDate)
                                        var evaluateEnd = test.asJsonObject.get("evaluateEnd").asLong
                                        var evaluateEndDate = unixtoDateString(evaluateEnd)
                                        Log.d(LOG_MoocWebActivity  +"moocre",evaluateEndDate)
                                        note = "评教时间:"+evaluateStartDate+"-"+evaluateEndDate
                                    }catch (e:Exception){
                                        Log.d(LOG_MoocWebActivity  +"error",e.toString())
                                    }
                                    Log.d(LOG_MoocWebActivity  +"moocrenote",note)
//                        var evaluateEnd = it.asJsonObject.get("evaluateEnd").asLong
                                    try {
                                        courseTaskDao.insert(
                                            CourseTask(_id = 0,name = name, platform = "mooc", group_name = group_name, start_time = "", end_time = deadlineDate,
                                                is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = note, isFinish = isFinish)
                                        )
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        Log.d(LOG_MoocWebActivity  +"error",e.toString())
                                    }
                                }
                            }
                            if(!it.asJsonObject.get("exam").isJsonNull){
                                var exam = it.asJsonObject.get("exam").asJsonObject
                                Log.d(LOG_MoocWebActivity  +"moocre",exam.toString())
                                var name = exam.asJsonObject.get("name").asString
                                Log.d(LOG_MoocWebActivity  +"moocre",name)
                                var deadline = exam.asJsonObject.get("deadline").asLong
                                var deadlineDate = unixtoDateString(deadline)
                                Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                                try {
                                    courseTaskDao.insert(
                                        CourseTask(_id = 0, platform = "mooc",name = name, group_name = group_name, start_time = "", end_time = deadlineDate,
                                            is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = "", isFinish = false)
                                    )
                                }catch (e: Exception){
                                    e.printStackTrace()
                                    Log.d(LOG_MoocWebActivity  +"error",e.toString())
                                }
                            }
                        }
                    }
                }

            }).start()
        }
        _binding.floatingActionButtonimport.setOnClickListener {
            Thread(Runnable {
                Log.d(LOG_MoocWebActivity  +"moocrecookies", cookies)
                Log.d(LOG_MoocWebActivity  +"moocrecsrfKey", csrfKey)
                Log.d(LOG_MoocWebActivity  +"moocretermId", termId)
                val client = OkHttpClient()
                val mediaType = "application/x-www-form-urlencoded".toMediaType()
                val body = "csrfKey=${csrfKey}&termId=${termId}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://www.icourse163.org/web/j/courseBean.getLastLearnedMocTermDto.rpc")
                    .post(body)
                    .addHeader("Cookie", cookies)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()?:"responseNull"
                Log.d(LOG_MoocWebActivity  +"moocre", responseData)
                val jsonObject: JsonObject = JsonParser.parseString(responseData).asJsonObject
                val result: JsonObject = jsonObject.get("result").asJsonObject
                val mocTermDto: JsonObject = result.get("mocTermDto").asJsonObject
                val group_name: String = mocTermDto.get("courseName").asString
                Log.d(LOG_MoocWebActivity  +"moocre", group_name)
                val chapters: JsonArray = mocTermDto.get("chapters").asJsonArray
                Log.d(LOG_MoocWebActivity  +"moocre", chapters.toString())
                val moocCourseDao = CourseTaskDatabase.getDatabase(this).moocCourseDao()
                if(moocCourseDao.getCourseCountByTid(termId)<1){
                    moocCourseDao.insert(MoocCourse(0,group_name,termId))
                }
                val courseTaskDao = CourseTaskDatabase.getDatabase(this).courseTaskDao()
                courseTaskDao.deleteAllByGourp(group_name)
                chapters.forEach {
                    if(!it.asJsonObject.get("homeworks").isJsonNull){
                        var homeworks = it.asJsonObject.get("homeworks").asJsonArray
                        Log.d(LOG_MoocWebActivity  +"moocre", homeworks.toString())
                        homeworks.forEach {
                            var name = it.asJsonObject.get("name").asString
                            Log.d(LOG_MoocWebActivity  +"moocre",name)
                            var viewStatus = it.asJsonObject.get("viewStatus").asInt
                            Log.d(LOG_MoocWebActivity  +"moocre",viewStatus.toString())
                            var isFinish = true
                            if(viewStatus == 0){
                                isFinish = false
                            }
                            var test = it.asJsonObject.get("test").asJsonObject
                            Log.d(LOG_MoocWebActivity  +"moocre",test.toString())
                            var deadline = test.asJsonObject.get("deadline").asLong
                            Log.d(LOG_MoocWebActivity  +"moocre",deadline.toString())
                            var deadlineDate = unixtoDateString(deadline)
                            Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                            var note = ""
                            try {
                                var evaluateStart = test.asJsonObject.get("evaluateStart").asLong
                                var evaluateStartDate = unixtoDateString(evaluateStart)
                                Log.d(LOG_MoocWebActivity  +"moocre",evaluateStartDate)
                                var evaluateEnd = test.asJsonObject.get("evaluateEnd").asLong
                                var evaluateEndDate = unixtoDateString(evaluateEnd)
                                Log.d(LOG_MoocWebActivity  +"moocre",evaluateEndDate)
                                note = "评教时间:"+evaluateStartDate+"-"+evaluateEndDate
                            }catch (e:Exception){
                                Log.d(LOG_MoocWebActivity  +"error",e.toString())
                            }
                            Log.d(LOG_MoocWebActivity  +"moocrenote",note)
//                        var evaluateEnd = it.asJsonObject.get("evaluateEnd").asLong
                            try {
                                courseTaskDao.insert(
                                    CourseTask(_id = 0,name = name, platform = "mooc",group_name = group_name, start_time = "", end_time = deadlineDate,
                                    is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = note, isFinish = isFinish)
                                )
                            }catch (e: Exception){
                                e.printStackTrace()
                                Log.d(LOG_MoocWebActivity  +"error",e.toString())
                            }
                        }
                    }
                    if(!it.asJsonObject.get("quizs").isJsonNull){
                        var quizs = it.asJsonObject.get("quizs").asJsonArray
                        Log.d(LOG_MoocWebActivity  +"moocre", quizs.toString())
                        quizs.forEach {
                            var name = it.asJsonObject.get("name").asString
                            Log.d(LOG_MoocWebActivity  +"moocre",name)
                            var viewStatus = it.asJsonObject.get("viewStatus").asInt
                            Log.d(LOG_MoocWebActivity  +"moocre",viewStatus.toString())
                            var isFinish = true
                            if(viewStatus == 0){
                                isFinish = false
                            }
                            var test = it.asJsonObject.get("test").asJsonObject
                            Log.d(LOG_MoocWebActivity  +"moocre",test.toString())
                            var deadline = test.asJsonObject.get("deadline").asLong
                            Log.d(LOG_MoocWebActivity  +"moocre",deadline.toString())
                            var deadlineDate = unixtoDateString(deadline)
                            Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                            var note = ""
                            try {
                                var evaluateStart = test.asJsonObject.get("evaluateStart").asLong
                                var evaluateStartDate = unixtoDateString(evaluateStart)
                                Log.d(LOG_MoocWebActivity  +"moocre",evaluateStartDate)
                                var evaluateEnd = test.asJsonObject.get("evaluateEnd").asLong
                                var evaluateEndDate = unixtoDateString(evaluateEnd)
                                Log.d(LOG_MoocWebActivity  +"moocre",evaluateEndDate)
                                note = "评教时间:"+evaluateStartDate+"-"+evaluateEndDate
                            }catch (e:Exception){
                                Log.d(LOG_MoocWebActivity  +"error",e.toString())
                            }
                            Log.d(LOG_MoocWebActivity  +"moocrenote",note)
//                        var evaluateEnd = it.asJsonObject.get("evaluateEnd").asLong
                            try {
                                courseTaskDao.insert(
                                    CourseTask(_id = 0,name = name, platform = "mooc", group_name = group_name, start_time = "", end_time = deadlineDate,
                                    is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = note, isFinish = isFinish)
                                )
                            }catch (e: Exception){
                                e.printStackTrace()
                                Log.d(LOG_MoocWebActivity  +"error",e.toString())
                            }
                        }
                    }
                    if(!it.asJsonObject.get("exam").isJsonNull){
                        var exam = it.asJsonObject.get("exam").asJsonObject
                        Log.d(LOG_MoocWebActivity  +"moocre",exam.toString())
                        var name = exam.asJsonObject.get("name").asString
                        Log.d(LOG_MoocWebActivity  +"moocre",name)
                        var deadline = exam.asJsonObject.get("deadline").asLong
                        var deadlineDate = unixtoDateString(deadline)
                        Log.d(LOG_MoocWebActivity  +"moocre",deadlineDate)
                        try {

                            courseTaskDao.insert(
                                CourseTask(_id = 0, platform = "mooc",name = name, group_name = group_name, start_time = "", end_time = deadlineDate,
                                is_course_task = "null", is_allow_after_submitted = false, task_type = 999, note = "", isFinish = false)
                            )
                        }catch (e: Exception){
                            e.printStackTrace()
                            Log.d(LOG_MoocWebActivity  +"error",e.toString())
                        }
                    }
                }
            }).start()
//

        }
    }
    fun updateScript(baseScript: String, firstValue: String, secondValue: String): String {
        Log.d(LOG_MoocWebActivity +"updateScript" ,"updateScript")
        // 用于替换.value=''的正则表达式
        val regex = """\.value\s*=\s*''""".toRegex()

        // 将第一个.value=''替换为.firstValue
        val updatedScript = regex.replaceFirst(baseScript, ".value='$firstValue'")

        // 将第二个.value=''替换为.secondValue
        var returnScript = regex.replaceFirst(updatedScript, ".value='$secondValue'")
        Log.d(LOG_MoocWebActivity +"updateScript" ,returnScript)
        return returnScript
    }
    @SuppressLint("JavascriptInterface")
    fun initWebView(webView: WebView, activity: Activity){
        Log.d(LOG_MoocWebActivity +"initWebView" ,"initWebView")
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(LOG_MoocWebActivity +"onPageFinished" ,"onPageFinished")
//                var userList = MyApplication.userDBHelper.queryAllUserInfo()
//                var user = userList.filter { it.platform == "智慧理工大" }
//                var platform = MyApplication.userDBHelper.queryPlatformInfoByplatName("缴费平台")
//                var myJs = updateScript(platform.webJs,user[0].name,user[0].pass)
//                Log.d("dianfei",myJs)
//                view?.evaluateJavascript(myJs, ValueCallback {
//
//                } )
//                val downloadjs = "var context = '<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>';\n" +
//                        "console.log(context);"
//                view?.evaluateJavascript(downloadjs, ValueCallback {
//                })
                val js = "document.getElementsByClassName('j-inputtext dlemail j-nameforslide')[0].value ='13886245155@163.com';\n" +
                        "console.log(document.getElementsByTagName('input'));"+
                        "setTimeout(function() {\n" +
                        "document.getElementsByName('password')[0].value ='nyh314nyh';\n"+
                        "document.getElementById('dologin').click()"+
                        "}, 1000);\n"
                view?.evaluateJavascript(js, ValueCallback {
                })
                val cookieManager: CookieManager = CookieManager.getInstance()
                cookies = cookieManager.getCookie(url)?:""
                Log.d(LOG_MoocWebActivity +"onPageFinishedCookie" ,cookies)
                if(cookies.length>0){
                    csrfKey = cookies.substring(cookies.lastIndexOf("NTESSTUDYSI=")+"NTESSTUDYSI=".length,cookies.indexOf(";",cookies.indexOf("NTESSTUDYSI=")))
                    Log.d(LOG_MoocWebActivity +"moocre","csrfKey:${csrfKey}")
                    Log.d(LOG_MoocWebActivity +"moocre","url:${url}")
                    termId = url?.substring(url.indexOf("tid=")+"tid=".length)?:""
                    Log.d(LOG_MoocWebActivity +"moocre","termId:${termId}")
                }

                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                request?.let {
                    Log.d(LOG_MoocWebActivity +"Request URL", it.url.toString())


                    // 打印请求头信息
                    for ((key, value) in it.requestHeaders) {
                        if(key.contains("authorization")){
                            Log.d(LOG_MoocWebActivity +"Request Header", "$key: $value")
                        }
                        if(key.contains("Cookie")){
                            Log.d(LOG_MoocWebActivity +"Request Header", "$key: $value")
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
                super.onProgressChanged(view, newProgress)
            }
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {

                    Log.d(LOG_MoocWebActivity +"html=", consoleMessage.message())
                    FileUtil.saveText(path = path, text = consoleMessage.message()?:"")
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