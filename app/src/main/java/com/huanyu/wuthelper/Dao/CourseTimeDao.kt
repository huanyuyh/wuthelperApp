package com.huanyu.wuthelper.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huanyu.wuthelper.entity.CourseTime
@Dao
interface CourseTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(courseTime: CourseTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(courseTimes: List<CourseTime>)
    @Delete
    fun delete(course: CourseTime)
    @Query("DELETE FROM coursetimes")
    fun deleteAll():Int
    @Update
    fun update(courseTime: CourseTime)

    @Query("SELECT * FROM coursetimes")
    fun getAllCourseTimes(): List<CourseTime>


    @Query("SELECT COUNT(*) FROM coursetimes")
    fun getCourseTimeCount(): Int
}