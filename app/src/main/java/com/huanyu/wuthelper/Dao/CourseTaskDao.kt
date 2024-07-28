package com.huanyu.wuthelper.Dao

import androidx.room.*
import com.huanyu.wuthelper.entity.CourseTask

@Dao
interface CourseTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(courseTask: CourseTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(courseTask: List<CourseTask>)
    @Delete
    fun delete(courseTask: CourseTask)
    @Query("DELETE FROM coursetasks")
    fun deleteAll():Int
    @Query("DELETE FROM coursetasks WHERE platform == :platform")
    fun deleteAllByPlatForm(platform:String):Int
    @Query("DELETE FROM coursetasks WHERE platform == 'mooc' AND group_name == :groupName")
    fun deleteMoocByGroupName(groupName: String):Int
    @Update
    fun update(courseTask: CourseTask)

    @Query("SELECT * FROM coursetasks")
    fun getAllCourseTasks(): List<CourseTask>
    @Query("SELECT * FROM coursetasks WHERE isFinish == false ORDER BY group_name")
    fun getAllCourseTasksOrderByGroup(): List<CourseTask>
    @Query("SELECT * FROM coursetasks ORDER BY end_time")
    fun getAllCourseTasksOrderByEndTime(): List<CourseTask>
    @Query("DELETE FROM coursetasks WHERE group_name = :groupName")
    fun deleteAllByGourp(groupName:String):Int

    @Query("SELECT COUNT(*) FROM coursetasks")
    fun getCourseTasksCount(): Int
    @Query("SELECT * FROM coursetasks WHERE end_time > :endtime ORDER BY end_time ASC LIMIT :limit")
    fun getFirstFiveTasks(endtime:String,limit:Int):List<CourseTask>
    @Query("SELECT * FROM coursetasks WHERE end_time > :endtime ORDER BY group_name")
    fun getAllTasksNoLimitOrderByGroup(endtime:String):List<CourseTask>
    @Query("SELECT * FROM coursetasks WHERE end_time > :endtime ORDER BY end_time")
    fun getAllTasksNoLimitOrderByEndTime(endtime:String):List<CourseTask>
}