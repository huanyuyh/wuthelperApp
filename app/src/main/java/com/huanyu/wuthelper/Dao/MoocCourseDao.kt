package com.huanyu.wuthelper.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huanyu.wuthelper.entity.MoocCourse

@Dao
interface MoocCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(moocCourse: MoocCourse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(moocCourses: List<MoocCourse>)
    @Delete
    fun delete(moocCourse: MoocCourse)
    @Query("DELETE FROM mooccourse")
    fun deleteAll():Int
    @Update
    fun update(moocCourse: MoocCourse)

    @Query("SELECT * FROM mooccourse")
    fun getAllCourses(): List<MoocCourse>

    @Query("SELECT COUNT(*) FROM mooccourse")
    fun getCourseCount(): Int

    @Query("SELECT * FROM mooccourse WHERE tid = :tid")
    fun getCourseByTid(tid:String): MoocCourse
    @Query("SELECT COUNT(*) FROM mooccourse WHERE tid = :tid")
    fun getCourseCountByTid(tid:String): Int
}