package com.huanyu.wuthelper.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.adapter.UserListAdapter
import com.huanyu.wuthelper.database.UserDatabase
import com.huanyu.wuthelper.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    lateinit var _binding: ActivityUsersBinding
    private lateinit var usersViewModel: UsersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityUsersBinding.inflate(layoutInflater)
        usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usersViewModel.getUsers()
        _binding.userList.layoutManager = LinearLayoutManager(this)
        var userListAdapter = UserListAdapter(null)
        _binding.userList.adapter = userListAdapter
        usersViewModel.userList.observe(this){
            userListAdapter.updateData(it)
        }
        _binding.saveBtn.setOnClickListener {
            userListAdapter.getUserList()?.let { it1 -> usersViewModel.updateUsers(it1) }
        }
    }
}