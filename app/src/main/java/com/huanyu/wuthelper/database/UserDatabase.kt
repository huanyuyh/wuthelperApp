package com.huanyu.wuthelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huanyu.wuthelper.Dao.CourseDao
import com.huanyu.wuthelper.Dao.CourseInfoDao
import com.huanyu.wuthelper.Dao.CourseTimeDao
import com.huanyu.wuthelper.Dao.PlatformDao
import com.huanyu.wuthelper.Dao.UserDao
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User

@Database(entities = [User::class,Platform::class], version = 1)
abstract class UserDatabase: RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun PlatformDao(): PlatformDao

    companion object {
        @Volatile private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }


}