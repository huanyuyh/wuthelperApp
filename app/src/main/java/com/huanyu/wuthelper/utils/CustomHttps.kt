package com.huanyu.wuthelper.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.work.ListenableWorker.Result.Success
import com.google.gson.JsonParser
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.MainActivity
import com.huanyu.wuthelper.database.BuildingDatabase
import com.huanyu.wuthelper.database.BuildingDatabaseUtil
import com.huanyu.wuthelper.database.CourseTaskDatabase
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.fragment.HomeViewModel
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.UTCtoTime
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.isCurrentTimeInRange
import com.huanyu.wuthelper.utils.SPTools.Companion.getNotifyId
import com.huanyu.wuthelper.utils.SPTools.Companion.getUpdateUrl
import com.huanyu.wuthelper.utils.SPTools.Companion.getXiaoYaAuth
import com.huanyu.wuthelper.utils.SPTools.Companion.getXiaoYaCookie
import com.huanyu.wuthelper.utils.SPTools.Companion.putNotifyId
import com.huanyu.wuthelper.utils.SPTools.Companion.putNotifyMsg
import com.huanyu.wuthelper.utils.SPTools.Companion.putNotifyTime
import com.huanyu.wuthelper.utils.SPTools.Companion.putNotifyTitle
import com.huanyu.wuthelper.utils.SPTools.Companion.putXiaoYaUpdateTime
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.InetAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
class CustomDns(private val dnsServers: List<InetAddress>) : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return dnsServers.flatMap { server ->
            Dns.SYSTEM.lookup(hostname).map { InetAddress.getByAddress(it.address) }
        }
    }

    companion object {
        fun create(servers: List<String>): CustomDns {
            val inetAddresses = servers.map { InetAddress.getByName(it) }
            return CustomDns(inetAddresses)
        }
    }
}
class CustomHttps {
    companion object{
        fun getXiaoYaTasks(context: Context,onSuccess:()->Unit){
            val xiaoyacookie = getXiaoYaCookie(context)
            val xiaoyaauth = getXiaoYaAuth(context)
            if (!xiaoyaauth.contains("null") && !xiaoyacookie.contains("null")) {
                val courseTaskDao = CourseTaskDatabase.getDatabase(context).courseTaskDao()
                var reStr: String? = null
                try {

                    val response = oKHttpGet("https://whut.ai-augmented.com/api/jx-stat/group/task/un_finish",xiaoyacookie,xiaoyaauth)
                    reStr = response.body?.string()
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
                if(reStr?.contains("success") == true){
                    val jsonObject = JsonParser.parseString(reStr).asJsonObject
                    val isSuccess = jsonObject.get("success").asBoolean
                    if (isSuccess) {
                        val currentDateTime = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
                        val formattedDateTime = currentDateTime.format(formatter)
                        putXiaoYaUpdateTime(context,formattedDateTime)
                        courseTaskDao.deleteAllByPlatForm("小雅")
                        val data = jsonObject.get("data").asJsonArray
                        data?.forEach {
                            Log.d("getXiaoYaTasks", it.toString())
                            Log.d("getXiaoYaTasks", it.asJsonObject.toString())
                            val name = it.asJsonObject.get("name").asString
                            Log.d("getXiaoYaTasks", name)
                            val group_name = it.asJsonObject.get("group_name").asString
                            Log.d("getXiaoYaTasks", group_name)
                            var start_time = it.asJsonObject.get("start_time").asString
                            Log.d("getXiaoYaTasks", start_time)
                            var end_time = it.asJsonObject.get("end_time").asString
                            Log.d("getXiaoYaTasks", end_time)
                            var is_course_task = "null"
                            try {
                                is_course_task = it.asJsonObject.get("is_course_task").asString
                            } catch (e: Exception) {
                                Log.d("getXiaoYaTasks", e.printStackTrace().toString())
                            }
                            Log.d("getXiaoYaTasks", is_course_task)
                            val is_allow_after_submitted = it.asJsonObject.get("is_allow_after_submitted").asBoolean
                            Log.d("getXiaoYaTasks", is_allow_after_submitted.toString())
                            val task_type = it.asJsonObject.get("task_type").asInt
                            Log.d("getXiaoYaTasks", task_type.toString())
                            start_time = UTCtoTime(start_time)
                            end_time = UTCtoTime(end_time)
                            try {
                                courseTaskDao.insert(
                                    CourseTask(
                                        _id = 0, name = name, platform = "小雅", group_name = group_name,
                                        start_time = start_time, end_time = end_time, is_course_task = is_course_task,
                                        is_allow_after_submitted = is_allow_after_submitted, task_type = task_type,
                                        note = "", isFinish = false
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.d("xiaoyaerror", e.toString())
                            } finally {

                            }
                        }
                        onSuccess()
                    } else {
                        Log.d("getXiaoYaTasks", "登录过期")
                    }

                }
            }
        }
       fun wutDianFeeGetAndTryByLogin(applicationContext: Context,onSuccess: (String,String)->Unit,onError:()->Unit){
           try {
               val zhcookie = SPTools.getZWUTCookie(applicationContext)
               val platform = UserDatabase.getDatabase(applicationContext).UserDao()
                   .getUserByPlatform("智慧理工大")
               if (!isCurrentTimeInRange("23:20", "00:10")) {
                   Log.d("wutDianFeeGetAndTryByLogin","!isCurrentTimeInRange")
                   val cookie = SPTools.getWUTFeeCookie(applicationContext)
                   val saveId = SPTools.getDianFeeMeterId(applicationContext)
                   val saveRoom = SPTools.getDianFeeSaveRoom(applicationContext)
                   if (cookie.length > 1 && saveId.length > 1) {
                       Log.d("wutDianFeeGetAndTryByLogin","cookie.length > 1")
                       var response = wutDianFeeGet( cookie,saveId)
                       var restr = response.body?.string()
                       // 处理响应并更新Widget
                       if (restr != null) {
                           if (restr.contains("meterOverdue")) {
                               val jsonObject = org.json.JSONObject(restr)

                               // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                               val meterOverdue = jsonObject.getString("meterOverdue")
                               val remainPower = jsonObject.getString("remainPower")
                               val dianFeeDao =
                                   BuildingDatabase.getDatabase(applicationContext).dianFeeDao()
                               BuildingDatabaseUtil.insertUnionDianFeeDue(
                                   dianFeeDao,
                                   saveRoom,
                                   meterOverdue,
                                   remainPower
                               )
                               onSuccess(remainPower, meterOverdue)
                               Log.d("wutDianFeeGetAndTryByLogin", remainPower)
                           } else {
                               if (platform.name.length > 1) {
                                   val wutLogin = WutLogin(platform.name, platform.pass)
                                   val danfeiCookie = wutLogin.loginCwsfwithcookie(zhcookie)
                                   var tempdanfeiCookie = ""
                                   danfeiCookie?.forEach {
                                       if (it.name.contains("JSESSIONID")) {
                                           tempdanfeiCookie = it.name + "=" + it.value
                                           SPTools.putWUTFeeCookie(
                                               applicationContext,
                                               tempdanfeiCookie
                                           )
                                       }
                                   }
                                   //                                tempdanfeiCookie = "JSESSIONID=0A3249A61EEF9AC2F876DBD1190D"
                                   response = wutDianFeeGet(saveId, cookie)
                                   restr = response.body?.string()
                                   if (restr != null) {
                                       if (restr.contains("meterOverdue")) {
                                           val jsonObject = org.json.JSONObject(restr)

                                           // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                                           val meterOverdue = jsonObject.getString("meterOverdue")
                                           val remainPower = jsonObject.getString("remainPower")
                                           val dianFeeDao =
                                               BuildingDatabase.getDatabase(applicationContext)
                                                   .dianFeeDao()
                                           BuildingDatabaseUtil.insertUnionDianFeeDue(
                                               dianFeeDao,
                                               saveRoom,
                                               meterOverdue,
                                               remainPower
                                           )
                                           onSuccess(remainPower, meterOverdue)
                                           Log.d("wutDianFeeGetAndTryByLogin", remainPower)


                                       } else {
                                           wutLogin.getLtValue()
                                           wutLogin.getPublicKey()
                                           wutLogin.rsaEncode()
                                           wutLogin.loginUnion()
                                           wutLogin.loginCwsf()
                                           val reStr = wutLogin.getDianFei(saveId)
                                           if (reStr.contains("meterOverdue")) {
                                               val jsonObject = org.json.JSONObject(reStr)
                                               // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                                               val meterOverdue =
                                                   jsonObject.getString("meterOverdue")
                                               val remainPower = jsonObject.getString("remainPower")
                                               val dianFeeDao =
                                                   BuildingDatabase.getDatabase(applicationContext)
                                                       .dianFeeDao()
                                               BuildingDatabaseUtil.insertUnionDianFeeDue(
                                                   dianFeeDao,
                                                   saveRoom,
                                                   meterOverdue,
                                                   remainPower
                                               )
                                               onSuccess(remainPower, meterOverdue)
                                               Log.d("wutDianFeeGetAndTryByLogin", remainPower)


                                           }else{
                                               onError()
                                           }
                                           val cookies = wutLogin.getCookies()
                                           var tempCookie = ""
                                           cookies["zhlgd.whut.edu.cn"]?.let {
                                               it.forEach {
                                                   tempCookie += it.name + "=" + it.value + ";"
                                               }
                                               SPTools.putZWUTCookie(applicationContext, tempCookie)
                                           }
                                           var tempDianfeiCookie = ""
                                           cookies["cwsf.whut.edu.cn"]?.let {
                                               it.forEach {
                                                   if (it.name.contains("JSESSIONID")) {
                                                       tempDianfeiCookie += it.name + "=" + it.value + ";"
                                                       SPTools.putWUTFeeCookie(
                                                           applicationContext,
                                                           tempDianfeiCookie
                                                       )

                                                   }
                                               }

                                           }
                                       }
                                   }else{
                                       onError()
                                   }
                               }else{
                                   onError()
                               }
                           }
                       }else{
                           onError()
                       }
                   }else{
                       onError()
                   }
               }else{
                   Log.d("wutDianFeeGetAndTryByLogin","isCurrentTimeInRange")
                   onError()
               }
           }catch (e:Exception){

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
        fun tryGetWutDianFeeGetAreaInfo(cookie:String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeGetAreaInfo(cookie)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun tryGetWutDianFeeGet(cookie:String,meterId: String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeGet(cookie,meterId)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun tryGetWutDianFeeQueryBuildList(cookie:String,areaid: String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeQueryBuildList(cookie,areaid)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun tryGetWutDianFeeQueryFloorList(cookie:String,areaid: String,buildid: String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeQueryFloorList(cookie,areaid,buildid  )
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun tryGetWutDianFeeGetRoomInfo(cookie:String,buildid: String,floorid: String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeGetRoomInfo(cookie,buildid,floorid)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun tryGetWutDianFeeQueryRoomElec(cookie:String,meterId: String,onSuccess: (String) -> Unit,onError: () -> Unit){
            try {
                var response = wutDianFeeQueryRoomElec(cookie,meterId)
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    onSuccess(responseData)
                }else{
                    onError()
                }
            }catch (e:Exception){

            }
        }
        fun wutDianFeeGet(cookie:String,meterId:String,):Response{
            val body = buildDianFeeRequestBody("queryReserve",meterId=meterId)
            return oKHttpPost("http://cwsf.whut.edu.cn/queryReserve",cookie,body)

        }
        fun wutDianFeeGetAreaInfo(cookie:String):Response{
            val body = buildDianFeeRequestBody("getAreaInfo")
            return oKHttpPost("http://cwsf.whut.edu.cn/getAreaInfo",cookie,body)

        }
        fun wutDianFeeQueryBuildList(cookie:String,areaid:String):Response{
            val body = buildDianFeeRequestBody("queryBuildList",areaid=areaid)
            return oKHttpPost("http://cwsf.whut.edu.cn/queryBuildList",cookie,body)
        }
        fun wutDianFeeQueryFloorList(cookie:String,areaid:String,buildid: String):Response{
            val body = buildDianFeeRequestBody("queryFloorList",areaid=areaid, buildid = buildid)
            return oKHttpPost("http://cwsf.whut.edu.cn/queryFloorList",cookie,body)
        }
        fun wutDianFeeGetRoomInfo(cookie:String,buildid: String,floorid: String):Response{
            val body = buildDianFeeRequestBody("getRoomInfo", buildid = buildid, floorid = floorid)
            return oKHttpPost("http://cwsf.whut.edu.cn/getRoomInfo",cookie,body)
        }
        fun wutDianFeeQueryRoomElec(cookie:String,roomid: String):Response{
            val body = buildDianFeeRequestBody("queryRoomElec", roomid = roomid)
            return oKHttpPost("http://cwsf.whut.edu.cn/queryRoomElec",cookie,body)
        }
        fun buildDianFeeRequestBody(action:String="getAreaInfo",areaid:String="", buildid:String="", floorid:String="", roomid:String="", meterId:String=""):RequestBody{
            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            var body:RequestBody = "factorycode=E035".toRequestBody(mediaType)
            when (action) {
                "getAreaInfo" -> {
                    body = "factorycode=E035".toRequestBody(mediaType)
                    Log.d("dianfeibody","getAreaInfo factorycode=E035")
                }
                "queryBuildList" -> {
                    body = "factorycode=E035&areaid=${areaid}".toRequestBody(mediaType)
                    Log.d("dianfeibody","queryBuildList factorycode=E035&areaid=${areaid}")
                }
                "queryFloorList" -> {
                    body = "factorycode=E035&areaid=${areaid}&buildid=${buildid}".toRequestBody(mediaType)
                    Log.d("dianfeibody","queryFloorList factorycode=E035&areaid=${areaid}&buildid=${buildid}")
                }
                "getRoomInfo" -> {
                    body = "factorycode=E035&buildid=${buildid}&floorid=${floorid}".toRequestBody(mediaType)
                    Log.d("dianfeibody","getRoomInfo factorycode=E035&buildid=${buildid}&floorid=${floorid}")
                }
                "queryRoomElec" -> {
                    body = "factorycode=E035&roomid=${roomid}".toRequestBody(mediaType)
                    Log.d("dianfeibody","getRoomInfo factorycode=E035&roomid=${roomid}")
                }
                "queryReserve" -> {
                    body = "factorycode=E035&meterId=${meterId}".toRequestBody(mediaType)
                    Log.d("dianfeibody","getRoomInfo factorycode=E035&meterId=${meterId}")
                }
            }
            return body
        }
        fun oKHttpPost(url:String,cookies: String,body:RequestBody): Response {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build()
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", cookies)
                .addHeader("Pragma", "no-cache")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.76")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
            val response = client.newCall(request).execute()
            return response
        }
        fun oKHttpPost(url:String,body:RequestBody): Response {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build()
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Pragma", "no-cache")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.76")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
            val response = client.newCall(request).execute()
            return response
        }
        fun oKHttpGet(url:String,cookies: String,auth:String): Response {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build()
            val request = Request.Builder()
                .url(url)
                .addHeader("Cookie", cookies)
                .addHeader("Authorization", auth)
                .build()
            val response = client.newCall(request).execute()
            return response
        }
        fun getUpdate(context: Context,onUpdate: (String, String,String) -> Unit,onLatest:(String,String, String,String)->Unit){
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val versionName = packageInfo.versionName
                val versionCode = packageInfo.versionCode
                val updateUrl = getUpdateUrl(context)
                updateUrl.forEach {
                    var url = "${it}/check_update?version=${versionName}"
                    Log.d("Customhttp",url)
                    try {
                        val response = oKHttpGetWithGoogleDns(
                            url
                        )
                        response.body?.let {
                            var reStr = it.string()
                            Log.d("Customhttp",reStr)
                            if (reStr.contains("update")){

                                val jsonObject = org.json.JSONObject(reStr)
                                var isUpdate = jsonObject.getBoolean("update")
                                if(isUpdate){
                                    var latest_version_info = jsonObject.getJSONObject("latest_version_info")
                                    val release_notes = latest_version_info.getString("release_notes")
                                    val release_url = latest_version_info.getString("url")
                                    val version = latest_version_info.getString("version")
                                    Log.d("Customhttp",release_url.toString())

                                    onUpdate(release_notes,release_url,version)
                                }else{
                                    var message = jsonObject.getString("message")
                                    var latest_version_info = jsonObject.getJSONObject("latest_version_info")
                                    val release_notes = latest_version_info.getString("release_notes")
                                    val release_url = latest_version_info.getString("url")
                                    val version = latest_version_info.getString("version")
                                    onLatest(message,release_notes,release_url,version)
                                }
                            }
                        }
                    }catch (e:Exception){

                    }
                }
            } catch (e: Exception) {
                Log.d("CustomHttps",e.printStackTrace().toString())

            }
        }
        fun oKHttpGetWithGoogleDns(url:String): Response {
            val dnsServers = listOf("119.29.29.29","8.8.8.8", "8.8.4.4") // Google DNS servers
            val customDns = CustomDns.create(dnsServers)
            val client = OkHttpClient.Builder()
                .dns(customDns)
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            return response
        }
        fun getMsg(context: Context,onUpdate: (String, String,String) -> Unit){
            try {
                val updateUrl = getUpdateUrl(context)
                updateUrl.forEach {
                    var url = "${it}/get_msg"
                    Log.d("Customhttp",url)
                    try {
                        val response = oKHttpGetWithGoogleDns(
                            url
                        )
                        response.body?.let {
                            var reStr = it.string()
                            Log.d("Customhttp",reStr)
                            if (reStr.contains("notification")){
                                val jsonObject = org.json.JSONObject(reStr)
                                var notifyId= jsonObject.getString("notifyId")
                                var notification = jsonObject.getJSONObject("notification")
                                val msg = notification.getString("msg")
                                val title = notification.getString("title")
                                val time = notification.getString("time")
                                val lastNotifyId = getNotifyId(context)
                                if(lastNotifyId<notifyId||lastNotifyId.length<notifyId.length){
                                    onUpdate(title,msg,time)
                                    putNotifyId(context,notifyId)
                                    putNotifyTitle(context,title)
                                    putNotifyMsg(context,msg)
                                    putNotifyTime(context,time)
                                }

                            }
                        }
                    }catch (e:Exception){

                    }
                }
            } catch (e: Exception) {
                Log.d("CustomHttps",e.printStackTrace().toString())

            }
        }

    }
}