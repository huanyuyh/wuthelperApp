package com.huanyu.newjetpackstart.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class SharedPreferenceUtil private constructor(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)

    companion object {
        private const val CONFIG_FILE = "config"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SharedPreferenceUtil? = null

        fun getInstance(context: Context): SharedPreferenceUtil {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferenceUtil(context.applicationContext).also { instance = it }
            }
        }
    }

    fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun remove(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    // 保存字符串数组到 SharedPreferences
    fun putStringList(key: String, stringArray: List<String>) {
        val editor = sharedPreferences.edit()

        // 将数组转换为字符串
        val stringBuilder = StringBuilder()
        for (s in stringArray) {
            stringBuilder.append(s).append(",")
        }
        // 去掉最后一个逗号
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.setLength(stringBuilder.length - 1)
        }

        // 保存字符串到 SharedPreferences
        editor.putString(key, stringBuilder.toString())
        editor.apply()
    }
    // 从 SharedPreferences 读取字符串数组
    fun getStringList(key: String): List<String> {
        val savedString = sharedPreferences.getString(key, "") ?: ""

        // 将字符串转换为数组
        return if (savedString.isNotEmpty()) {
            savedString.split(",").toList()
        } else {
            listOf<String>()
        }
    }
}