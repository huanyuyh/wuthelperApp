package com.huanyu.wuthelper.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huanyu.wuthelper.entity.Course

@Dao
interface CourseDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(course: List<Course>)
    @Delete
    fun delete(course: Course)
    @Query("DELETE FROM courses")
    fun deleteAll():Int

    @Update
    fun update(course: Course)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): List<Course>


    @Query("SELECT COUNT(*) FROM courses")
    fun getCourseCount(): Int
    @Query("SELECT COUNT(*) FROM courses WHERE startWeek <= :week AND endWeek >= :week AND day == :day")
    fun getCourseCounttoday(week:Int,day:Int): Int
    @Query("SELECT * FROM courses WHERE startWeek <= :week AND endWeek >= :week")
    fun getCoursesForWeek(week:Int):List<Course>
    @Query("SELECT * FROM courses WHERE startWeek <= :week AND endWeek >= :week AND day == :day")
    fun getCoursesForWeekAndDay(week:Int,day:Int):List<Course>


}
