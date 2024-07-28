package com.huanyu.wuthelper.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huanyu.wuthelper.entity.Building
import com.huanyu.wuthelper.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(building: Building)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(buildings: List<Building>)

    @Delete
    fun delete(building: Building)

    @Query("DELETE FROM buildings")
    fun deleteAllBuildings():Int

    @Query("DELETE FROM buildings WHERE areaParent=:parent")
    fun deleteBuildingByParent(parent:String):Int
    @Update
    fun update(building: Building)

    @Query("SELECT * FROM buildings")
    fun getAllBuildings(): List<Building>

    @Query("SELECT COUNT(*) FROM buildings")
    fun getBuildingsCount(): Int

    @Query("SELECT * FROM buildings WHERE areaParent=:parent")
    fun queryBuildingsByAreaParent(parent:String): List<Building>

    @Query("SELECT * FROM buildings WHERE area=:area LIMIT 1")
    fun queryBuildingsByArea(area:String): Building

}