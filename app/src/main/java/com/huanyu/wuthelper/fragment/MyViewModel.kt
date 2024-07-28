package com.huanyu.wuthelper.fragment

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.database.BuildingDatabase
import com.huanyu.wuthelper.database.BuildingDatabaseUtil.Companion.insertUnionDue
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.Building
import com.huanyu.wuthelper.entity.DianFee
import com.huanyu.wuthelper.entity.User
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeGet
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeGetAreaInfo
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeGetRoomInfo
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeQueryBuildList
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeQueryFloorList
import com.huanyu.wuthelper.utils.CustomHttps.Companion.tryGetWutDianFeeQueryRoomElec
import com.huanyu.wuthelper.utils.CustomHttps.Companion.wutDianFeeGet
import com.huanyu.wuthelper.utils.CustomHttps.Companion.wutDianFeeGetAreaInfo
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myAlertDialog
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeAreaSave
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeBuildSave
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeFloorSave
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeMeterId
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeRoomSave
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeSaveRoom
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeefirstUse
import com.huanyu.wuthelper.utils.SPTools.Companion.getDianFeeisSave
import com.huanyu.wuthelper.utils.SPTools.Companion.getWUTFeeCookie
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeAreaSave
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeBuildSave
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeFloorSave
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeMeterId
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeRoomSave
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeSaveRoom
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeefirstUse
import com.huanyu.wuthelper.utils.SPTools.Companion.putDianFeeisSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOG = "MyViewModel:"
    }
    // TODO: Implement the ViewModel
    private lateinit var _wifiUser: User
    private var _wifiName: MutableLiveData<String> = MutableLiveData("")
    val wifiName: LiveData<String> get() = _wifiName
    private var _wifiPass: MutableLiveData<String> = MutableLiveData("")
    val wifiPass: LiveData<String> get() = _wifiPass

    private var firstUseDianFei = true
    private var isSaveDianFeiInfo = false
    private var _dFStr:MutableLiveData<String> = MutableLiveData()
    val dFStr: LiveData<String> get() = _dFStr
    private var saveId = ""
    private var saveRoom = ""
    private var cookie:String = ""

    private var _areas:MutableLiveData<List<String>> = MutableLiveData()
    val areas: LiveData<List<String>> get() = _areas
    private var _builds:MutableLiveData<List<String>> = MutableLiveData()
    val builds: LiveData<List<String>> get() = _builds
    private var _floors:MutableLiveData<List<String>> = MutableLiveData()
    val floors: LiveData<List<String>> get() = _floors
    private var _rooms:MutableLiveData<List<String>> = MutableLiveData()
    val rooms: LiveData<List<String>> get() = _rooms
    private var _areaselect:MutableLiveData<Int> = MutableLiveData(0)
    val areaselect: LiveData<Int> get() = _areaselect
    private var _buildselect:MutableLiveData<Int> = MutableLiveData(0)
    val buildselect: LiveData<Int> get() = _buildselect
    private var _floorselect:MutableLiveData<Int> = MutableLiveData(0)
    val floorselect: LiveData<Int> get() = _floorselect
    private var _roomselect:MutableLiveData<Int> = MutableLiveData(0)
    val roomselect: LiveData<Int> get() = _roomselect
    private val client = OkHttpClient()

    private fun parseAreaList(key:String, jsonString: String): Pair<Map<String, String>, List<String>> {
        Log.d(LOG +"parseAreaList","parseAreaList")
        val jsonObject = JSONObject(jsonString)
        val jsonArray = jsonObject.getJSONArray(key)
        val areaMap = mutableMapOf<String, String>()
        val areaNames = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val areaInfo = jsonArray.getString(i)
            val parts = areaInfo.split("@")
            val id = parts[0]
            val name = parts[1]
            areaMap[name] = id
            areaNames.add(name)
        }

        return Pair(areaMap, areaNames)
    }
    private fun parseJsonToFloorList(jsonString: String): List<String> {
        Log.d(LOG +"parseJsonToFloorList","parseJsonToFloorList")
        // 创建 JSONObject
        val jsonObject = JSONObject(jsonString)

        // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
        val jsonArray = jsonObject.getJSONArray("floorList")

        // 初始化一个空的 MutableList<String>
        val floorList = mutableListOf<String>()

        // 遍历 JSONArray
        for (i in 0 until jsonArray.length()) {
            // 将每个整数转换为 String 并添加到列表中
            floorList.add(jsonArray.getInt(i).toString())
        }

        return floorList
    }
    fun saveDFInfo(
        context: Context,
        selectedItemPositionArea: Int,
        selectedItemPositionBuild: Int,
        selectedItemPositionFloor: Int,
        selectedItemPositionRoom: Int
    ) {
        Log.d(LOG +"saveDFInfo","saveDFInfo")
        putDianFeeMeterId(getApplication(),saveId)
        putDianFeeSaveRoom(getApplication(),saveRoom)
        putDianFeeisSave(getApplication(),true)
        putDianFeeAreaSave(getApplication(),selectedItemPositionArea)
        putDianFeeBuildSave(getApplication(),selectedItemPositionBuild)
        putDianFeeFloorSave(getApplication(),selectedItemPositionFloor)
        putDianFeeRoomSave(getApplication(),selectedItemPositionRoom)
        myAlertDialog(context,"保存查询信息","信息：$saveRoom 已保存", onClick = {})

    }
    private fun showCustomDialog(context: Context, msg:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("提示")
        builder.setMessage(msg)

        // 添加确认按钮
        builder.setPositiveButton("确认") { dialog, _ ->
//            Toast.makeText(context, "点击了确认按钮", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // 关闭对话框
        }

        // 添加关闭按钮
        builder.setNegativeButton("关闭") { dialog, _ ->
//            Toast.makeText(context, "点击了关闭按钮", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // 关闭对话框
        }

        val dialog = builder.create()
        dialog.show()
    }
    fun initDianFei(){
        Log.d(LOG +"initDianFei","initDianFei")
        firstUseDianFei = getDianFeefirstUse(getApplication())
        isSaveDianFeiInfo = getDianFeeisSave(getApplication())
        saveId = getDianFeeMeterId(getApplication())
        saveRoom = getDianFeeSaveRoom(getApplication())
        cookie = getWUTFeeCookie(getApplication())
        Log.d(LOG +"cookie",cookie)
        if(!firstUseDianFei){
            viewModelScope.launch (Dispatchers.IO){
                val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
                val buildings = buildingDao.queryBuildingsByAreaParent("root")
                val areas = mutableListOf<String>()
                buildings.forEach {
                    areas.add(it.area)
                }
                launch (Dispatchers.Main){
                    _areas.value = areas
                    _areaselect.value = getDianFeeAreaSave(getApplication())
                }
            }
        }
    }
    fun dianFeiReturn(areaSelectPos: Int) {
        viewModelScope.launch (Dispatchers.IO){
            Log.d(LOG +"dianFeiReturn","dianFeiReturn")
            cookie = getWUTFeeCookie(getApplication())
            if(firstUseDianFei){
                Log.d(LOG +"dianFeiReturn","firstUseDianFei")
                Log.d(LOG +"dianFeiReturn",cookie)
                if(cookie!="null"){
                    tryGetWutDianFeeGetAreaInfo(cookie, onSuccess = {
                        Log.d(LOG +"dianFeiReturn",it)
                        val (areaMap, areaNames) = parseAreaList("areaList",it)
                        val buildList:MutableList<Building> = mutableListOf()
                        areaNames.forEach {areaName->
                            areaMap[areaName]?.let {id->
                                buildList.add(Building(0,"root","",areaName,id))
                            }
                        }
                        val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
                        buildingDao.deleteBuildingByParent("root")
                        buildingDao.inserts(buildList)
                        putDianFeefirstUse(getApplication(),false)
                        viewModelScope.launch (Dispatchers.Main){
                            _areas.value = areaNames.toMutableList()
                        }
                    }, onError = {
                        viewModelScope.launch (Dispatchers.Main){
                            _areas.value = mutableListOf()
                        }

                    })
                }
            }else{
                if(isSaveDianFeiInfo){
                    Log.d(LOG +"dianFeiReturn","isSaveDianFeiInfo")
                    tryGetWutDianFeeGet(cookie,saveId, onSuccess = {
                        firstUseDianFei = true
                        Log.d(LOG +"dianFeiReturn",it)
                        if(it.contains("meterOverdue")){
                            // 创建 JSONObject
                            val jsonObject = JSONObject(it)

                            // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                            val meterOverdue = jsonObject.getString("meterOverdue")
                            val remainPower = jsonObject.getString("remainPower")
                            Log.d(LOG +"dianFeiReturn",meterOverdue)
                            // 获取当前时间
                            val currentDateTime = LocalDateTime.now()
                            // 定义日期时间格式
                            val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
                            // 格式化当前时间为字符串
                            val formattedDateTime = currentDateTime.format(formatter)
                            val dianfeeDao = BuildingDatabase.getDatabase(getApplication()).dianFeeDao()
                            insertUnionDue(dianfeeDao,DianFee(0,saveRoom,meterOverdue,formattedDateTime,remainPower))

                            viewModelScope.launch (Dispatchers.Main){
                                _dFStr.value = "$saveRoom 电费剩余${meterOverdue}元 ${remainPower}度"
                            }
                        }
                    }, onError = {})
                }else{
                    getBuildList(areaSelectPos)
                }

            }
        }
    }

    fun getBuildList(position: Int){
        Log.d(LOG +"getBuildList","getBuildList")
        viewModelScope.launch (Dispatchers.IO){
            val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
            val building = buildingDao.queryBuildingsByArea(areas.value!![position])
            val buildings = buildingDao.queryBuildingsByAreaParent(building.area)
            if(buildings.isEmpty()){
                tryGetWutDianFeeQueryBuildList(cookie,building.areaId, onSuccess = {
                    Log.d(LOG +"getBuildList",it)
                    if(it.contains("buildList")){
                        val (areaMap, areaNames) = parseAreaList("buildList",it)

                        val buildList:MutableList<Building> = mutableListOf()
                        areaNames.forEach {areaName->
                            areaMap[areaName]?.let {id->
                                buildList.add(Building(0,building.area,building.areaId,areaName,id))
                            }
                        }
                        buildingDao.deleteBuildingByParent(building.area)
                        buildingDao.inserts(buildList)
                        Log.d(LOG +"dianfeiBuild",buildList.toString())
                        viewModelScope.launch (Dispatchers.Main){
                            _builds.value = areaNames.toMutableList()
                        }
                    }else{
                        viewModelScope.launch (Dispatchers.Main){
                            _builds.value = mutableListOf()
                            _floors.value = mutableListOf()
                            _rooms.value = mutableListOf()
                        }
                    }
                }, onError = {
                    viewModelScope.launch (Dispatchers.Main){
                        _builds.value = mutableListOf()
                    }

                })
            }else{
                val builds = mutableListOf<String>()
                buildings.forEach {
                    builds.add(it.area)
                }
                launch (Dispatchers.Main){
                    _builds.value = builds
                    _buildselect.value = getDianFeeBuildSave(getApplication())
                }
            }
        }
    }
    fun getFloorList(position: Int){
        Log.d(LOG +"getFloorList","getFloorList")
        viewModelScope.launch (Dispatchers.IO){
            val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
            val building = buildingDao.queryBuildingsByArea(builds.value!![position])
            val buildings = buildingDao.queryBuildingsByAreaParent(building.area)
            if(buildings.isEmpty()){
                tryGetWutDianFeeQueryFloorList(cookie,building.areaParentId,building.areaId, onSuccess = {
                    Log.d(LOG +"getFloorList",it)
                    if(it.contains("floorList")){
                        val areaNames = parseJsonToFloorList(it)
                        val buildList:MutableList<Building> = mutableListOf()
                        val newfloors:MutableList<String> = mutableListOf()
                        areaNames.forEach {areaName->
                            val name = building.area+areaName+"层"
                            newfloors.add(name)
                            buildList.add(Building(0,building.area,building.areaId,name,areaName))

                        }

                        Log.d(LOG +"dianfeiBuild",buildList.toString())
                        buildingDao.deleteBuildingByParent(building.area)
                        buildingDao.inserts(buildList)
                        viewModelScope.launch (Dispatchers.Main){
                            _floors.value = newfloors
                        }
                    }else{
                        viewModelScope.launch (Dispatchers.Main) {
                            _floors.value = mutableListOf()
                            _rooms.value = mutableListOf()
                        }
                    }
                }, onError = {
                    viewModelScope.launch (Dispatchers.Main){
                        _floors.value = mutableListOf()
                    }

                })
            }else{
                val builds = mutableListOf<String>()
                buildings.forEach {
                    builds.add(it.area)
                }
                launch (Dispatchers.Main){
                    _floors.value = builds
                    _floorselect.value = getDianFeeFloorSave(getApplication())
                }
            }
        }
    }
    fun getRoomList(position: Int){
        Log.d(LOG +"getRoomList","getRoomList")
        viewModelScope.launch (Dispatchers.IO){
            val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
            val building = buildingDao.queryBuildingsByArea(floors.value!![position])
            val buildings = buildingDao.queryBuildingsByAreaParent(building.area)

            if(buildings.isEmpty()){
                tryGetWutDianFeeGetRoomInfo(cookie,building.areaParentId,building.areaId, onSuccess = {
                    Log.d(LOG +"getRoomList",it)
                    if(it.contains("roomList")){
                        val (areaMap, areaNames) = parseAreaList("roomList",it)
                        val buildList:MutableList<Building> = mutableListOf()
                        areaNames.forEach {areaName->
                            areaMap[areaName]?.let {id->
                                buildList.add(Building(0,building.area,building.areaId,areaName,id))
                            }
                        }
                        Log.d(LOG +"dianfeiBuild",buildList.toString())
                        buildingDao.deleteBuildingByParent(building.area)
                        buildingDao.inserts(buildList)
                        viewModelScope.launch (Dispatchers.Main){
                            _rooms.value = areaNames.toMutableList()
                        }
                    }else{
                        viewModelScope.launch (Dispatchers.Main){
                        _rooms.value = mutableListOf()
                        }
                    }
                }, onError = {
                    viewModelScope.launch (Dispatchers.Main){
                        _rooms.value = mutableListOf()
                    }
                })
            }else{
                val builds = mutableListOf<String>()
                buildings.forEach {
                    builds.add(it.area)
                }
                launch (Dispatchers.Main){
                    _rooms.value = builds
                    _roomselect.value = getDianFeeRoomSave(getApplication())
                }
            }
        }
    }

    fun getDianFeiInfo(position: Int){
        Log.d(LOG +"getDianFeiInfo","getDianFeiInfo")
        viewModelScope.launch (Dispatchers.IO){
            val buildingDao = BuildingDatabase.getDatabase(getApplication()).buildingDao()
            val building = buildingDao.queryBuildingsByArea(rooms.value!![position])
            val buildings = buildingDao.queryBuildingsByAreaParent(building.area)

            if(buildings.isEmpty()){
                tryGetWutDianFeeQueryRoomElec(cookie,building.areaId, onSuccess = {
                    Log.d("dianfei",it)
                    if(it.contains("meterId")){
                        // 创建 JSONObject
                        val jsonObject = JSONObject(it)

                        // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                        val meterId = jsonObject.getString("meterId")

                        Log.d("dianfei",meterId)
                        tryGetWutDianFeeGet(cookie,meterId, onSuccess = {success->
                            Log.d("dianfei",success)
                            // 创建 JSONObject
                            if(success.contains("meterOverdue")){
                                val meterjsonObject = JSONObject(success)

                                // 从 JSONObject 中获取名为 "floorList" 的 JSONArray
                                val meterOverdue = meterjsonObject.getString("meterOverdue")
                                val remainPower = meterjsonObject.getString("remainPower")
                                Log.d("dianfei",meterOverdue)
                                saveRoom = building.area
                                saveId = meterId
                                viewModelScope.launch (Dispatchers.Main){
                                    _dFStr.value = building.area +" 电费剩余${meterOverdue}元 ${remainPower}度"
                                }


                            }
                        }, onError = {})
                    }

                }, onError = {})

            }else{
                val builds = mutableListOf<String>()
                buildings.forEach {
                    builds.add(it.area)
                }
                launch (Dispatchers.Main){
                    _rooms.value = builds
                }
            }
        }
    }


    private fun handleGetDianfei(action:String="getAreaInfo", cookie: String, areaid:String="", buildid:String="", floorid:String="", roomid:String="", meterId:String="", onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        //action {getAreaInfo,queryBuildList }
        Log.d(LOG +"handleGetDianfei","handleGetDianfei")

        viewModelScope.launch(Dispatchers.IO) {
            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            var body = "factorycode=E035".toRequestBody(mediaType)
            try {
                Log.d(LOG +"dianfeicookie",cookie)
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

                val request = Request.Builder()
                    .url("http://cwsf.whut.edu.cn/MNetWorkUI/${action}")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Host", "cwsf.whut.edu.cn")
                    .addHeader("Cookie", cookie)
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    launch(Dispatchers.Main) {
                        onSuccess(responseData)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        onError(Exception("Error fetching data"))
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
    fun handleLogin(username: String,password: String,nasId: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        Log.d(LOG +"handleLogin","handleLogin")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mediaType = "application/x-www-form-urlencoded".toMediaType()
                Log.d("wifi","username=${username}&password=${password}&nasId=${nasId}")
                val body = "username=${username}&password=${password}&nasId=${nasId}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("http://172.30.21.100/api/account/login")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Host", "172.30.21.100")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    launch(Dispatchers.Main) {
                        onSuccess(responseData)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        onError(Exception("Error fetching data"))
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
    fun downloadXiaoli(context: Context,onSuccess: (File) -> Unit){
        Log.d(LOG +"downloadXiaoli","downloadXiaoli")
        viewModelScope.launch (Dispatchers.IO){

            val xlrequest: Request = Request.Builder()
                .url("http://i.whut.edu.cn/xl")
                .build()
            try {
                var xiaoliName :String?
                val xiaoliUrl :String?
                val response = client.newCall(xlrequest).execute()
                val respon = response.body!!.string()
                if (respon.contains("学期校历")) {
                    xiaoliName = respon.substring(
                        respon.indexOf("title=\"") + "title=\"".length,
                        respon.indexOf("学期校历") + "学期校历".length
                    )
                    Log.d(LOG +"xiaoli", xiaoliName)
                    xiaoliUrl = "http://i.whut.edu.cn/xl" + xiaoliName.substring(
                        xiaoliName.lastIndexOf("a href=\".") + "a href=\".".length,
                        xiaoliName.lastIndexOf("\" title=")
                    )
                    Log.d(LOG +"xiaoli", xiaoliUrl)
                    xiaoliName =
                        xiaoliName.substring(xiaoliName.lastIndexOf("title=\"") + "title=\"".length)
                    Log.d(LOG +"xiaoliName", xiaoliName)
                    Log.d(LOG +"xiaoliUrl", xiaoliUrl)
                    if (xiaoliUrl.contains("http")) {
                        val clientpng = OkHttpClient().newBuilder()
                            .build()
                        val requestpng: Request = Request.Builder()
                            .url(xiaoliUrl)
                            .build()
                        try {
                            val responsepng = clientpng.newCall(requestpng).execute()
                            val responpng = responsepng.body!!.string()
                            if (responpng.contains("text-align: center;")) {
                                var xiaoliPng =
                                    responpng.substring(responpng.indexOf("\"text-align: center;\"><a href=\".") + "\"text-align: center;\"><a href=\".".length)
                                xiaoliPng = xiaoliPng.substring(0, xiaoliPng.indexOf("\""))
                                Log.d(LOG +"xiaoli", xiaoliPng)
                                val xiaoliPngUrl =
                                    xiaoliUrl.substring(0, xiaoliUrl.lastIndexOf("/")) + xiaoliPng
                                Log.d(LOG +"xiaoli", xiaoliPngUrl)
                                downloadImage(xiaoliPngUrl,xiaoliName,context,onSuccess = {
                                    onSuccess(it)
                                })
                            }
                        } catch (e: IOException) {
                            Log.d(LOG +"xiaoli", e.printStackTrace().toString())
                        }
                    }
                }
            } catch (e: IOException) {
                Log.d(LOG +"xiaoli", e.printStackTrace().toString())
            }

        }
    }

    private fun downloadImage(url: String, name:String, context: Context, onSuccess: (File) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理请求失败的情况
                Log.e("DownloadImage", "Image download failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val inputStream = responseBody.byteStream()
                    val outputStream = FileOutputStream(File(context.externalCacheDir, "${name}.png"))

                    try {
                        // 将输入流中的内容写入文件
                        outputStream.use { fileOut ->
                            inputStream.copyTo(fileOut)
                            Log.d("DownloadImage", "Image downloaded and saved successfully")
                            val file = File(
                                context.externalCacheDir,
                                "${name}.png"
                            )
                            onSuccess(file)

                        }
                    } catch (e: Exception) {
                        Log.e("DownloadImage", "Error saving image", e)
                    } finally {
                        inputStream.close()
                        responseBody.close()
                    }
                }
            }
        })
    }

    private fun getFinalURL(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false // 设置为false使我们可以手动处理重定向

        // 设置超时
        connection.connectTimeout = 5000 // 5秒连接超时
        connection.readTimeout = 5000 // 5秒读取超时

        try {
            connection.connect()
            val responseCode = connection.responseCode
            Log.d("wifinewUrl", connection.headerFields.toString())
            if (responseCode in 300..399) { // 检查是否是重定向响应
                val newUrl = connection.getHeaderField("Location") // 从头部获取新的URL
                connection.disconnect()
                Log.d("wifinewUrl", newUrl ?: "No new URL found")
                return newUrl ?: url // 保证即使newUrl为null也返回原始url
            }
            return url // 返回最终的URL
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("getFinalURL", "Connection timed out", e)
            return "fail" // 超时返回fail
        } catch (e: Exception) {
            Log.e("getFinalURL", "Error connecting to URL", e)
            return "fail" // 其他网络错误返回fail
        } finally {
            connection.disconnect() // 确保释放资源
        }
    }
    fun getWifiNasId(onSuccess: (String) -> Unit, onInternet:() -> Unit,onError: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("wifi","start")
            val url = getFinalURL("http://1.1.1.1")
            Log.d("wifi",url)
            if(url.contains("fail")){
                onError()
            }else if(!url.contains("1.1.1.1")){
                val nasId = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('?'))
                Log.d("wifiNasId",nasId)
                onSuccess(nasId)
            }else if(url.contains("1.1.1.1")){
                onInternet()
            }

        }
    }
    fun getWifiUser(){
        Log.d(LOG +"getWifiUser","getWifiUser")
        viewModelScope.launch (Dispatchers.IO){
            val user = UserDatabase.getDatabase(getApplication()).UserDao().getUserByPlatform("校园网认证(WLAN)")
            launch (Dispatchers.Main){
                _wifiUser = user
                _wifiName.value = user.name
                _wifiPass.value = user.pass
            }
        }
    }
    fun saveWifiUser(wifiName: String, wifiPass: String) {
        Log.d(LOG +"saveWifiUser","saveWifiUser")
        viewModelScope.launch (Dispatchers.IO){
            val user = _wifiUser
            user.name = wifiName
            user.pass = wifiPass
            UserDatabase.getDatabase(getApplication()).UserDao().update(user)

            launch (Dispatchers.Main){
                _wifiName.value = wifiName
                _wifiPass.value = wifiPass
            }
        }
    }



}