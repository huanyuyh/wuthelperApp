package com.huanyu.wuthelper.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huanyu.wuthelper.entity.Platform

import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(platform: Platform)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(platforms: List<Platform>)
    @Delete
    fun delete(platform: Platform)
    @Query("DELETE FROM platforms")
    fun deleteAllPlatforms():Int
    @Update
    fun update(platform: Platform)

    @Query("SELECT * FROM platforms")
    fun getAllPlatform(): List<Platform>


    @Query("SELECT COUNT(*) FROM platforms")
    fun getPlatformsCount(): Int
    @Query("SELECT * FROM platforms WHERE platName = :name LIMIT 1")
    fun getPlatformByName(name:String): Platform
}