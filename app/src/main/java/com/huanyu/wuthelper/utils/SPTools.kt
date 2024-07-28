package com.huanyu.wuthelper.utils

import android.content.Context
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil

class SPTools() {
    companion object{
        const val ZWUT_COOKIE = "zhlgdCookie"
        const val WUTFEE_COOKIE = "DFcookie"
        const val DIANFEE_METERID = "DFmeterId"
        const val DIANFEE_SAVEROOM = "DFsaveRoom"
        const val UNIT_DATE = "unitDate"
        const val WEEK_COUNT = "weekCount"
        const val XiaoYaUpdateTime = "XiaoYaUpdateTime"
        const val XIAOYA_COOKIE = "xiaoyacookies"
        const val xiaoya_authorization = "xiaoyaauthorization"
        const val isXiaoLiHave = "isXiaoLiHave"
        const val DFisSave = "DFisSave"
        const val DFAreaSave = "DFAreaSave"
        const val DFBuildSave = "DFBuildSave"
        const val DFFloorSave = "DFFloorSave"
        const val DFRoomSave = "DFRoomSave"
        const val DFfirstUse = "DFfirstUse"

        const val UPDATEURL = "updateUrl"
        const val NOTIFYID = "notifyId"
        const val NOTIFYTITLE = "notifyTitle"
        const val NOTIFYMSG = "notifyMsg"
        const val NOTIFYTIME = "notifyTime"

        const val FIRSTUSEAPP = "firstUseApp"

//        const val oneWordStr = "oneWordStr"
//        const val oneWordFrom = "oneWordFrom"
//        const val oneWordFromWho = "oneWordFromWho"
//        const val oneWordType = "oneWordType"
//        const val oneWordId = "oneWordId"
//        const val oneWordUuid = "oneWordUuid"
        fun removeDianFeeInfo(context: Context){
            SharedPreferenceUtil.getInstance(context).remove(DFfirstUse)
            SharedPreferenceUtil.getInstance(context).remove(DFisSave)


        }

        fun putFirstUseApp(context: Context,boolean: Boolean){
            return SharedPreferenceUtil.getInstance(context).putBoolean(FIRSTUSEAPP,boolean)
        }
        fun getFirstUseApp(context: Context):Boolean{
            return SharedPreferenceUtil.getInstance(context).getBoolean(FIRSTUSEAPP,true)
        }
        fun getNotifyTitle(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(NOTIFYTITLE,"null")
        }
        fun putNotifyTitle(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(NOTIFYTITLE,string)
        }
        fun getNotifyTime(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(NOTIFYTIME,"2024年6月28日")
        }
        fun putNotifyTime(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(NOTIFYTIME,string)
        }
        fun getNotifyMsg(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(NOTIFYMSG,"null")
        }
        fun putNotifyMsg(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(NOTIFYMSG,string)
        }
        fun getNotifyId(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(NOTIFYID,"0")
        }
        fun putNotifyId(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(NOTIFYID,string)
        }
        fun getUnitDate(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(UNIT_DATE,"2024-02-26")
        }
        fun putUnitDate(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(UNIT_DATE,string)
        }



        fun putisXiaoLiHave(context: Context,boolean: Boolean){
            return SharedPreferenceUtil.getInstance(context).putBoolean(isXiaoLiHave,boolean)
        }
        fun getisXiaoLiHave(context: Context):Boolean{
            return SharedPreferenceUtil.getInstance(context).getBoolean(isXiaoLiHave,false)
        }
        fun getWeekCount(context: Context):Int{
            return SharedPreferenceUtil.getInstance(context).getInt(WEEK_COUNT,0)
        }
        fun putWeekCount(context: Context,int: Int){
            SharedPreferenceUtil.getInstance(context).putInt(WEEK_COUNT,int)
        }
        fun getXiaoYaUpdateTime(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(XiaoYaUpdateTime,"null")
        }
        fun putXiaoYaUpdateTime(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(XiaoYaUpdateTime,string)
        }
        fun getXiaoYaCookie(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(XIAOYA_COOKIE,"null")
        }
        fun getXiaoYaAuth(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(xiaoya_authorization,"null")
        }
        fun getZWUTCookie(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(ZWUT_COOKIE,"null")
        }
        fun putZWUTCookie(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(ZWUT_COOKIE,string)
        }
        fun getWUTFeeCookie(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(WUTFEE_COOKIE, "null")
        }
        fun putWUTFeeCookie(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(WUTFEE_COOKIE,string)
        }

        fun getDianFeeMeterId(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(DIANFEE_METERID, "null")
        }
        fun putDianFeeMeterId(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(DIANFEE_METERID, string)
        }
        fun getDianFeeSaveRoom(context: Context):String{
            return SharedPreferenceUtil.getInstance(context).getString(DIANFEE_SAVEROOM, "null")
        }
        fun putDianFeeSaveRoom(context: Context,string: String){
            SharedPreferenceUtil.getInstance(context).putString(DIANFEE_SAVEROOM, string)
        }
        fun getDianFeeisSave(context: Context):Boolean{
            return SharedPreferenceUtil.getInstance(context).getBoolean(DFisSave, false)
        }
        fun putDianFeeisSave(context: Context,boolean: Boolean){
            SharedPreferenceUtil.getInstance(context).putBoolean(DFisSave,boolean)
        }
        fun getDianFeeAreaSave(context: Context):Int{
            return SharedPreferenceUtil.getInstance(context).getInt(DFAreaSave, 0)
        }
        fun putDianFeeAreaSave(context: Context,int: Int){
            SharedPreferenceUtil.getInstance(context).putInt(DFAreaSave,int)
        }
        fun getDianFeeBuildSave(context: Context):Int{
            return SharedPreferenceUtil.getInstance(context).getInt(DFBuildSave, 0)
        }
        fun putDianFeeBuildSave(context: Context,int: Int){
            SharedPreferenceUtil.getInstance(context).putInt(DFBuildSave,int)
        }
        fun getDianFeeFloorSave(context: Context):Int{
            return SharedPreferenceUtil.getInstance(context).getInt(DFFloorSave, 0)
        }
        fun putDianFeeFloorSave(context: Context,int: Int){
            SharedPreferenceUtil.getInstance(context).putInt(DFFloorSave,int)
        }
        fun getDianFeeRoomSave(context: Context):Int{
            return SharedPreferenceUtil.getInstance(context).getInt(DFRoomSave, 0)
        }
        fun putDianFeeRoomSave(context: Context,int: Int){
            SharedPreferenceUtil.getInstance(context).putInt(DFRoomSave,int)
        }
        fun getDianFeefirstUse(context: Context):Boolean{
            return SharedPreferenceUtil.getInstance(context).getBoolean(DFfirstUse, true)
        }
        fun putDianFeefirstUse(context: Context,boolean: Boolean){
            SharedPreferenceUtil.getInstance(context).putBoolean(DFfirstUse, boolean)
        }











        fun putUpdateUrl(context: Context,string: List<String>){
            SharedPreferenceUtil.getInstance(context).putStringList(UPDATEURL,string)
        }
        fun getUpdateUrl(context: Context):List<String>{
            return SharedPreferenceUtil.getInstance(context).getStringList(UPDATEURL)
        }

    }

}