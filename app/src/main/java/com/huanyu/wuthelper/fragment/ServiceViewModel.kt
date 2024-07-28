package com.huanyu.wuthelper.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOG_ServiceViewModel = "ServiceViewModel:"
    }
    // TODO: Implement the ViewModel
    private var _platformList: MutableLiveData<List<Platform>> = MutableLiveData()
    val platformList: LiveData<List<Platform>> get() = _platformList
    fun getPlatforms(){
        viewModelScope.launch (Dispatchers.IO){
            var platforms =  UserDatabase.getDatabase(getApplication()).PlatformDao().getAllPlatform()
            launch(Dispatchers.Main) {
                _platformList.value = platforms
            }

        }
    }
}