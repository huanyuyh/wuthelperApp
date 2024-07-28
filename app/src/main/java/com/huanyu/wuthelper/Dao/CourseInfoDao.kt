package com.huanyu.wuthelper.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.huanyu.wuthelper.entity.CourseInfo

@Dao
interface CourseInfoDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(courseInfo: CourseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(courseInfo: List<CourseInfo>)
    @Delete
    fun delete(courseInfo: CourseInfo)
    @Query("DELETE FROM courseinfos")
    fun deleteAll():Int
    @Update
    fun update(courseInfo: CourseInfo)

    @Query("SELECT * FROM courseinfos")
    fun getAllCourseInfos(): List<CourseInfo>

    @Query("SELECT * FROM courseinfos WHERE name = :name")
    fun getCourseInfoByName(name:String): CourseInfo
    @Query("SELECT COUNT(*) FROM courseinfos")
    fun getCourseInfoCount(): Int
}
