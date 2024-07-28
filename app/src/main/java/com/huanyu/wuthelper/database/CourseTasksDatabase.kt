package com.huanyu.wuthelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huanyu.wuthelper.Dao.CourseTaskDao
import com.huanyu.wuthelper.Dao.MoocCourseDao
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.entity.MoocCourse

@Database(entities = [CourseTask::class,MoocCourse::class], version = 1)
abstract class CourseTaskDatabase: RoomDatabase() {
    abstract fun courseTaskDao(): CourseTaskDao
    abstract fun moocCourseDao(): MoocCourseDao

    companion object {
        @Volatile private var INSTANCE: CourseTaskDatabase? = null

        fun getDatabase(context: Context): CourseTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseTaskDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }


}