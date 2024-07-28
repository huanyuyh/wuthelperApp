package com.huanyu.wuthelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huanyu.wuthelper.Dao.BuildingDao
import com.huanyu.wuthelper.Dao.CourseDao
import com.huanyu.wuthelper.Dao.CourseInfoDao
import com.huanyu.wuthelper.Dao.CourseTimeDao
import com.huanyu.wuthelper.Dao.DianFeeDao
import com.huanyu.wuthelper.Dao.OneWordDao
import com.huanyu.wuthelper.Dao.PlatformDao
import com.huanyu.wuthelper.Dao.UserDao
import com.huanyu.wuthelper.entity.Building
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.entity.DianFee
import com.huanyu.wuthelper.entity.OneWord
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User

@Database(entities = [OneWord::class], version = 1)
abstract class OneWordDatabase: RoomDatabase() {
    abstract fun oneWordDao(): OneWordDao


    companion object {
        @Volatile private var INSTANCE: OneWordDatabase? = null

        fun getDatabase(context: Context): OneWordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OneWordDatabase::class.java,
                    "oneword_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }


}