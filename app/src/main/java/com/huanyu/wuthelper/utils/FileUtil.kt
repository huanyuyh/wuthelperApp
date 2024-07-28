package com.huanyu.wuthelper.utils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileUtil {
    companion object{
        fun getFilesInDirectory(directoryPath: String): List<File> {
            val fileList: MutableList<File> = ArrayList()
            val directory = File(directoryPath)

            // 检查directory是否是一个目录
            if (directory.isDirectory) {
                val files = directory.listFiles()
                if (files != null) {
                    for (file in files) {
                        fileList.add(file)
                        // 如果需要递归查找子目录中的文件，可以在这里递归调用
                    }
                }
            }

            return fileList
        }
        public fun saveText(path: String,text: String){
            var os: BufferedWriter? = null
            os = BufferedWriter(FileWriter(path))
            os?.write(text)
            os?.close()
        }

        public fun readText(path: String): String? {
            var ins:BufferedReader? =null
            ins = BufferedReader(FileReader(path))
            var text = ins?.readText()
            ins?.close()
            return text
        }

    }
}