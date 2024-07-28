package com.huanyu.wuthelper.Dao

import androidx.room.*
import com.huanyu.wuthelper.entity.DianFee

@Dao
interface DianFeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dianFee: DianFee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(dianFees: List<DianFee>)

    @Delete
    fun delete(dianFee: DianFee)

    @Query("DELETE FROM dianfees")
    fun deleteAll():Int

    @Query("DELETE FROM dianfees WHERE areaName=:name")
    fun deleteDianFeesByAreaName(name:String):Int
    @Update
    fun update(dianFee: DianFee)

    @Query("SELECT * FROM dianfees")
    fun getAll(): List<DianFee>

    @Query("SELECT COUNT(*) FROM dianfees")
    fun getCount(): Int

    @Query("SELECT * FROM dianfees WHERE areaName=:name ORDER BY time DESC")
    fun queryDianFeesByAreaName(name:String): List<DianFee>

    @Query("SELECT due FROM dianfees ORDER BY time DESC LIMIT 1")
    fun getLatestDue(): String?
    @Query("SELECT dianfee FROM dianfees ORDER BY time DESC LIMIT 1")
    fun getLatestDianfee(): String?
}