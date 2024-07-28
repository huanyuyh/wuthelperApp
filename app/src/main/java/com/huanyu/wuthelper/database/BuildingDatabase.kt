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
import com.huanyu.wuthelper.Dao.PlatformDao
import com.huanyu.wuthelper.Dao.UserDao
import com.huanyu.wuthelper.entity.Building
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.CourseTime
import com.huanyu.wuthelper.entity.DianFee
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User

@Database(entities = [Building::class,DianFee::class], version = 2)
abstract class BuildingDatabase: RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    abstract fun dianFeeDao():DianFeeDao

    companion object {
        @Volatile private var INSTANCE: BuildingDatabase? = null

        fun getDatabase(context: Context): BuildingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BuildingDatabase::class.java,
                    "building_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE dianfees (
                        _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        areaName TEXT NOT NULL,
                        dianfee TEXT NOT NULL,
                        time TEXT NOT NULL,
                        due TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

    }


}