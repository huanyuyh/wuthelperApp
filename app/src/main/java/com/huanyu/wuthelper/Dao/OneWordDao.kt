package com.huanyu.wuthelper.Dao

import androidx.room.*
import com.huanyu.wuthelper.entity.OneWord

@Dao
interface OneWordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(oneWord: OneWord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(oneWords: List<OneWord>)

    @Delete
    fun delete(oneWord: OneWord)

    @Query("DELETE FROM onewords")
    fun deleteAll():Int
    @Query("DELETE FROM onewords WHERE id = :id")
    fun deleteByID(id:String):Int

    @Update
    fun update(oneWord: OneWord)

    @Query("SELECT * FROM onewords")
    fun getAll(): List<OneWord>

    @Query("SELECT COUNT(*) FROM onewords")
    fun getCount(): Int
}