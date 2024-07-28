package com.huanyu.wuthelper.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huanyu.wuthelper.entity.Course
import com.huanyu.wuthelper.entity.CourseShowEx

@Dao
interface CourseShowExDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(courseShowEx: CourseShowEx)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(coursesShowEx: List<CourseShowEx>)
    @Delete
    fun delete(course: CourseShowEx)
    @Query("DELETE FROM courseshowex")
    fun deleteAll():Int

    @Update
    fun update(courseShowEx:CourseShowEx)

    @Query("SELECT * FROM courseshowex")
    fun getAllCourseExs(): List<CourseShowEx>


    @Query("SELECT COUNT(*) FROM courseshowex")
    fun getCourseCount(): Int


}
