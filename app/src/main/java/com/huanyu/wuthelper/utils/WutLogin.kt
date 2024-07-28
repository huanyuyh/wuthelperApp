package com.huanyu.wuthelper.utils

import android.util.Log
import android.view.View.OnKeyListener
import com.google.gson.Gson
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.Security
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit

class WutLogin(var username: String,var password: String) {
    class RedirectCookieJar : CookieJar {
        private val cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            if (cookieStore[url.host] == null) {
                cookieStore[url.host] = mutableListOf()
            }
            cookieStore[url.host]?.let { cookieStores->
                cookieStores.replaceAll { item ->
                    val newItem = cookies.find { it.name == item.name }
                    newItem?:item
                }
                cookies.forEach { item ->
                    if (!cookieStores.any { it.name == item.name }) {
                        cookieStores.add(item)
                    }
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: mutableListOf()
        }
        fun getCookies(): Map<String, List<Cookie>> {
            return cookieStore
        }
    }
    var ltValue = ""
    var publicKey = ""
    // Encrypt credentials
    var encryptedUsername = ""
    var encryptedPassword = ""
    // 创建 OkHttpClient 实例
    val client = OkHttpClient.Builder()
        .followRedirects(false) // 禁用 OkHttp 自动重定向处理
        .followSslRedirects(false)
        .cookieJar(RedirectCookieJar())
        .build()
    val testClient = OkHttpClient.Builder()
        .cookieJar(RedirectCookieJar())
        .build()
    fun getLtValue() {
        val request = Request.Builder()
            .url("https://zhlgd.whut.edu.cn/tpass/login?service=https%3A%2F%2Fzhlgd.whut.edu.cn%2Ftp_up%2F")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            val document = Jsoup.parse(response.body?.string())
            ltValue = document.select("input[name=lt]").attr("value")
            Log.d("loginThread",ltValue)
        }
    }
    fun getPublicKey() {
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://zhlgd.whut.edu.cn/tpass/rsa?skipWechat=true")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val jsonResponse = response.body?.string()
            jsonResponse?.let{
                if(it.contains("publicKey")){
                    val jsonObject = JSONObject(it)
                    publicKey = jsonObject.getString("publicKey")
                }
                Log.d("loginThread",jsonResponse)
            }

        }
    }
    fun rsaEncode(){
        // Encrypt credentials
        encryptedUsername = rsaEncrypt(username, publicKey)
        encryptedPassword = rsaEncrypt(password, publicKey)
        Log.d("loginThread","Encrypted Username: $encryptedUsername")
        Log.d("loginThread","Encrypted Password: $encryptedPassword")
    }
    fun rsaEncrypt(data: String, publicKey: String): String {
        Security.addProvider(BouncyCastleProvider())
        val keySpec = X509EncodedKeySpec(Base64.decode(publicKey))
        val keyFactory = KeyFactory.getInstance("RSA")
        val rsaPublicKey = keyFactory.generatePublic(keySpec)

        val cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedData = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.toBase64String(encryptedData)
    }
    fun loginUnion() {
        Log.d("loginThread", "doLogin")
        // Define headers
        val headers = Headers.Builder()
            .add(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
            )
            .add("Accept-Encoding", "gzip, deflate, br")
            .add("Accept-Language", "zh-CN,zh;q=0.9")
            .add("Cache-Control", "max-age=0")
            .add("Connection", "keep-alive")
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Host", "zhlgd.whut.edu.cn")
            .add("Origin", "https://zhlgd.whut.edu.cn")
            .add(
                "Referer",
                "https://zhlgd.whut.edu.cn/tpass/login?service=https%3A%2F%2Fzhlgd.whut.edu.cn%2Ftp_up%2F"
            )
            .add("Sec-Ch-Ua", "\"Not=A?Brand\";v=\"99\", \"Chromium\";v=\"118\"")
            .add("Sec-Ch-Ua-Mobile", "?0")
            .add("Sec-Ch-Ua-Platform", "\"Windows\"")
            .add("Sec-Fetch-Dest", "document")
            .add("Sec-Fetch-Mode", "navigate")
            .add("Sec-Fetch-Site", "same-origin")
            .add("Sec-Fetch-User", "?1")
            .add("Upgrade-Insecure-Requests", "1")
            .add(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36"
            )
            .build()
        val loginUrl =
            "https://zhlgd.whut.edu.cn/tpass/login?service=https%3A%2F%2Fzhlgd.whut.edu.cn%2Ftp_up%2F"
        val formBody = FormBody.Builder()
            .add("rsa", "")
            .add("ul", encryptedUsername)
            .add("pl", encryptedPassword)
            .add("lt", ltValue)
            .add("execution", "e1s1")
            .add("_eventId", "submit")
            .build()

        var request = Request.Builder()
            .url(loginUrl)
            .headers(headers)
            .post(formBody)
            .build()

        var response: Response = client.newCall(request).execute()
        response.body?.let {
            Log.d("loginThread", it.string())
        }
        while (response.isRedirect) {
            val location = response.header("Location") ?: throw IOException("No Location header in response")
            request = Request.Builder()
                .url(location)
                .get()
                .build()
            response = client.newCall(request).execute()
            response.body?.let {
                Log.d("loginThread", it.string())
            }

        }
        val cookieJar = client.cookieJar as RedirectCookieJar
        var cookies = cookieJar.getCookies()

        Log.d("loginThread", cookies.toString())
    }
    fun loginCwsf(){
        Log.d("loginThread", "doLoginCwsf")
        var request = Request.Builder()
            .url("http://zhlgd.whut.edu.cn/tpass/login?service=http%3A%2F%2Fcwsf.whut.edu.cn%2FcasLogin")
            .get()
            .build()
        var response = client.newCall(request).execute()
        while (response.isRedirect) {
            val location = response.header("Location") ?: throw IOException("No Location header in response")
            request = Request.Builder()
                .url(location)
                .get()
                .build()
            response = client.newCall(request).execute()
            response.body?.let { Log.d("loginThread", it.string()) }
        }
        val cookieJar = client.cookieJar as RedirectCookieJar
        var cookies = cookieJar.getCookies()
        Log.d("loginThread", cookies.toString())

    }
    fun getDianFei(meterId:String):String{
        Log.d("loginThread", "doGetDianFei")
        var request = Request.Builder()
            .url("http://cwsf.whut.edu.cn/queryReserve?meterId=$meterId&factorycode=E035")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Connection", "keep-alive")
            .addHeader("Host", "cwsf.whut.edu.cn")
            .addHeader("Pragma", "no-cache")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.76")
            .build()
        var response = client.newCall(request).execute()
        response.body?.let {
            var reStr = it.string()
            Log.d("loginThread","Login Response: ${reStr}")
            return reStr
        }
        return "null"
    }
    fun testLogin(cookie: String):String?{
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://zhlgd.whut.edu.cn/")
            .addHeader("Cookie", cookie)
            .build()
        val response = testClient.newCall(request).execute()
//        while (response.isRedirect) {
//            val location = response.header("Location") ?: throw IOException("No Location header in response")
//            request = Request.Builder()
//                .url(location)
//                .addHeader("Cookie", cookie)
//                .get()
//                .build()
//            response = client.newCall(request).execute()
//            response.body?.let { Log.d("loginThread", it.string()) }
//        }
//        var re = testLogin2(cookie)
//        re?.let {
//            Log.d("loginth",it)
//        }

        return response.body?.string()
    }
    fun testLogin2(cookie: String):String?{

        var request = Request.Builder()
            .url("https://zhlgd.whut.edu.cn/")
            .build()
        var response = testClient.newCall(request).execute()
//        while (response.isRedirect) {
//            val location = response.header("Location") ?: throw IOException("No Location header in response")
//            request = Request.Builder()
//                .url(location)
//                .addHeader("Cookie", cookie)
//                .get()
//                .build()
//            response = client.newCall(request).execute()
//            response.body?.let { Log.d("loginThread", it.string()) }
//        }
        return response.body?.string()
    }
    fun loginCwsfwithcookie(cookie: String): List<Cookie>? {
        Log.d("loginThread", "doLoginCwsfwithCookie")
        var request = Request.Builder()
            .url("http://zhlgd.whut.edu.cn/tpass/login?service=http%3A%2F%2Fcwsf.whut.edu.cn%2FcasLogin")
            .addHeader("Cookie",cookie)
            .get()
            .build()
        var response = testClient.newCall(request).execute()
        response.body?.let { Log.d("loginThread", it.string()) }
        while (response.isRedirect) {
            val location = response.header("Location") ?: throw IOException("No Location header in response")
            request = Request.Builder()
                .url(location)
                .get()
                .build()
            response = testClient.newCall(request).execute()
            response.body?.let {
                var reStr = it.string()
                if(reStr.contains("个人信息查询")){
                    Log.d("loginThread", reStr)
                    val cookieJar = testClient.cookieJar as RedirectCookieJar
                    var cookies = cookieJar.getCookies()
                    Log.d("loginThread", cookies.toString())
                    Log.d("loginThread", cookies["cwsf.whut.edu.cn"].toString())
                    return cookies["cwsf.whut.edu.cn"]
                }

            }
        }
//        var reStr = response.body?.string()
//        if(reStr!=null){
//            if()
//        }
        val cookieJar = testClient.cookieJar as RedirectCookieJar
        var cookies = cookieJar.getCookies()
        Log.d("loginThread", cookies.toString())
        Log.d("loginThread", cookies["cwsf.whut.edu.cn"].toString())
        return cookies["cwsf.whut.edu.cn"]
    }
    fun getCookies(): Map<String, List<Cookie>> {
        Log.d("loginThread", "dogetCookies")
        val cookieJar = client.cookieJar as RedirectCookieJar
        var cookies = cookieJar.getCookies()
        return cookies
    }
    fun getTestCookies(): Map<String, List<Cookie>> {
        Log.d("loginThread", "dogetTestCookies")
        val cookieJar = testClient.cookieJar as RedirectCookieJar
        var cookies = cookieJar.getCookies()
        return cookies
    }

}