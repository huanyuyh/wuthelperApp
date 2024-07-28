package com.huanyu.wuthelper.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.entity.User


class UserListAdapter(private var mUserList: List<User>?): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    fun getUserList():List<User>?{
        return mUserList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usercard, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, @SuppressLint("RecyclerView") position: Int) {
        mUserList?.let {
            holder.bind(it[position])
            holder.editName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it[position].name = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            holder.editPass.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it[position].pass = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

    }

    override fun getItemCount() = mUserList?.size?:0
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.platname)
        val editName: EditText = itemView.findViewById(R.id.platUser)
        val editPass: EditText = itemView.findViewById(R.id.platPass)

        fun bind(user: User) {
            textView.text = user.platform
            editName.setText(user.name)
            editPass.setText(user.pass)

        }

    }
    fun updateData(items: List<User>?) {
        this.mUserList = items!!
        notifyDataSetChanged()
    }
}