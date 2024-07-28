package com.huanyu.wuthelper.activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.newjetpackstart.utils.SharedPreferenceUtil
import com.huanyu.wuthelper.database.BuildingDatabase
import com.huanyu.wuthelper.database.OneWordDatabase
import com.huanyu.wuthelper.entity.DianFee
import com.huanyu.wuthelper.entity.OneWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OneWordViewModel(application: Application):AndroidViewModel(application) {
    companion object{
        private val LOG_OneWordViewModel = "OneWordViewModel:"
    }
    private var _oneWordList: MutableLiveData<List<OneWord>> = MutableLiveData(listOf())
    val oneWordList: LiveData<List<OneWord>> get() = _oneWordList
    //从数据库获取课程表
    fun getOneWordList(){
        Log.d(LOG_OneWordViewModel+"getDianfeeList","getDianfeeList()")
        viewModelScope.launch(Dispatchers.IO){

            val oneWordDao = OneWordDatabase.getDatabase(getApplication()).oneWordDao()
            var oneWord:List<OneWord> = oneWordDao.getAll()
            launch(Dispatchers.Main) {
                _oneWordList.value = oneWord
            }
        }
    }
}