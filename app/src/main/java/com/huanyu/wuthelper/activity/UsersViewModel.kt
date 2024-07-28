package com.huanyu.wuthelper.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(application: Application):AndroidViewModel(application) {
    private var _userList: MutableLiveData<List<User>> = MutableLiveData()
    val userList: LiveData<List<User>> get() = _userList
    fun getUsers(){
        viewModelScope.launch (Dispatchers.IO){
            var users =  UserDatabase.getDatabase(getApplication()).UserDao().getAllUsers()
            launch(Dispatchers.Main) {
                _userList.value = users
            }

        }
    }
    fun setUsers(users:List<User>){
        _userList.value = users
    }
    fun updateUsers(users:List<User>){
        viewModelScope.launch (Dispatchers.IO){
            UserDatabase.getDatabase(getApplication()).UserDao().updates(users)
        }
    }
}