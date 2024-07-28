package com.huanyu.wuthelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huanyu.wuthelper.Dao.CourseDao
import com.huanyu.wuthelper.Dao.CourseInfoDao
import com.huanyu.wuthelper.Dao.CourseShowExDao
import com.huanyu.wuthelper.Dao.CourseTimeDao
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.CourseShowEx
import com.huanyu.wuthelper.entity.CourseTime

@Database(entities = [Course::class,CourseInfo::class,CourseTime::class,CourseShowEx::class],  version = 3)
abstract class CoursesDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseInfoDao(): CourseInfoDao
    abstract fun courseTimeDao(): CourseTimeDao
    abstract fun courseShowExDao(): CourseShowExDao

    companion object {
        @Volatile private var INSTANCE: CoursesDatabase? = null

        fun getDatabase(context: Context): CoursesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoursesDatabase::class.java,
                    "course_database"
                ).addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE courses ADD COLUMN color TEXT NOT NULL DEFAULT ''")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE courseshowex (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                // 创建新的表，不包括要删除的列
                database.execSQL(
                    """
                    CREATE TABLE courses_new (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        day INTEGER NOT NULL,
                        room TEXT NOT NULL,
                        teacher TEXT NOT NULL,
                        startNode INTEGER NOT NULL,
                        endNode INTEGER NOT NULL,
                        startWeek INTEGER NOT NULL,
                        endWeek INTEGER NOT NULL,
                        type INTEGER NOT NULL,
                        credit REAL NOT NULL,
                        note TEXT NOT NULL,
                        startTime TEXT NOT NULL,
                        endTime TEXT NOT NULL
                        
                    )
                    """.trimIndent()
                )

                // 复制旧表的数据到新表中
                database.execSQL(
                    """
                    INSERT INTO courses_new (_id, name, day, room, teacher, startNode, endNode, startWeek, endWeek, type, credit,note ,startTime, endTime)
                    SELECT _id, name, day, room, teacher, startNode, endNode, startWeek, endWeek, type, credit, startTime, endTime, note
                    FROM courses
                    """.trimIndent()
                )

                // 删除旧表
                database.execSQL("DROP TABLE courses")

                // 重命名新表为旧表名
                database.execSQL("ALTER TABLE courses_new RENAME TO courses")
            }
        }

    }



}