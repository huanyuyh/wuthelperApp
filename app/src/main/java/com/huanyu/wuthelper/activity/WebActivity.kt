package com.huanyu.wuthelper.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    companion object{
        private val LOG = "WebActivity"
    }

    lateinit var webView: WebView
    lateinit var viewModel: WebActivityViewModel
    lateinit var _binding :ActivityWebBinding
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == viewModel.FILE_CHOOSER_RESULT_CODE) {
            if (viewModel.uploadMessage == null) return
            viewModel.uploadMessage!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            viewModel.uploadMessage = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val bundle = intent.getBundleExtra("bundle")
        //视图绑定
        _binding = ActivityWebBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(WebActivityViewModel ::class.java)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bundle?.let {
            it.getString("platform")?.let {
                viewModel.getPlatformInfo(it)
                Log.d(LOG,it)
            }
        }
        // 获取CookieManager实例
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
//        var cookietemp = SharedPreferenceUtil(this).getString("zhlgdCookie","")
//        cookieManager.setCookie("zhlgd.whut.edu.cn", "route=c515fa7d47fa0daa33d1011f48cecef3;Language=zh_CN;JSESSIONID=xhUCvCnMyDp-xmvpfrsQz5etMKjtG9MuNOiwwTBUUN6wsL3LK9yK!518610784;CASPRIVACY=;CASTGC=TGT-0122109361613-297804-pfQC39Gb2AFGiq3TvOCvzXUwJxfc3izd5KG2KnOnyL4MDM99le-tpass;tp_up=DKoCvF51V_B-k3a_CqZWagpIozjjRJvY2Cgcaj16xTonNa13AQet!490301310;");
        val cookies = cookieManager.getCookie("zhlgd.whut.edu.cn")

//        // 强制同步 Cookies
//        cookieManager.flush()
        System.out.println("Cookies for zhlgd.whut.edu.cn: $cookies")
        //viewModel实例化

        viewModel.context = this
        viewModel.activity = this
        viewModel.path = this.externalCacheDir!!.path+"/jwc.html"

//防止键盘影响布局        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        viewModel.action = bundle?.getString("action").toString()

        webView = _binding.webview

        viewModel.initWebView(webView)
//        webView.loadUrl(viewModel.webUrl)
        viewModel.webTitle.observe(this, Observer {
            it?.let {
                _binding.editUrl.setText(it)
            }
        })
        viewModel.progressWeb.observe(this, Observer {
            it?.let {
                _binding.progressWeb.progress = it
                if(it>=100){
                    _binding.progressWeb.visibility = View.GONE
                }else{
                    _binding.progressWeb.visibility = View.VISIBLE
                }
            }
        })
        _binding.editUrl.setOnFocusChangeListener{view,hasFocus->
            if(hasFocus) {
                viewModel.urlFocus = true
                _binding.editUrl.setText(webView.url)
            }
            else {
                viewModel.urlFocus = false
                _binding.editUrl.setText(viewModel.webTitle.value)
            }
        }
        _binding.editUrl.addTextChangedListener {
            if(viewModel.urlFocus)
                viewModel.tempUrl = it.toString()
        }
        _binding.goUrlButton.setOnClickListener {
            _binding.editUrl.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(_binding.editUrl.windowToken, 0)
            viewModel.urlFocus = false
            viewModel.webLoadUrl()
        }
        _binding.backButton.setOnClickListener {
            viewModel.webBack()
        }
        _binding.forwardButton.setOnClickListener {
            viewModel.webForWard()
        }
        _binding.homeButton.setOnClickListener {
            viewModel.webHome()
        }
        _binding.refreshButton.setOnClickListener {
            viewModel.webRefresh()
        }
        _binding.backPageButton.setOnClickListener {
            this.finish()
        }
        _binding.siteMoreButton.setOnClickListener{
            showMyPopWindows(it)

        }
    }
    fun showMyPopWindows(view: View){
        val myPopWindows = LayoutInflater.from(this).inflate(R.layout.pop_web,null)
//        Toast.makeText(this,"yes",Toast.LENGTH_SHORT).show()
//        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupWindow = inflater.inflate(R.layout.web_pop,null)
        val textView: TextView = myPopWindows.findViewById(R.id.openBoswer)
        textView.setText("在浏览器打开")
        val popWindow = PopupWindow(myPopWindows,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,true)
//        popWindow.contentView = myPopWindows
//        popWindow.width = LayoutParams.WRAP_CONTENT
//        popWindow.height = LayoutParams.WRAP_CONTENT
        popWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popWindow.setAnimationStyle(android.R.style.Animation_Dialog);
//        val rootview = LayoutInflater.from(this).inflate(R.layout.activity_web, null)
        popWindow.showAsDropDown(view)
        popWindow.setOnDismissListener {
            backgroundAlpha(1f);
        }
        backgroundAlpha(0.5f);
        textView.setOnClickListener {
            popWindow.dismiss()
            backgroundAlpha(1f);
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webView.url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun backgroundAlpha(alpha: Float) {
        val layoutParams = window.attributes
        layoutParams.alpha = alpha
        window.attributes = layoutParams
    }

    override fun onStart() {

        super.onStart()
    }

    override fun onStop() {

        super.onStop()
    }
}