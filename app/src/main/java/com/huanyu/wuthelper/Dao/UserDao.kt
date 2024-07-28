package com.huanyu.wuthelper.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(users: List<User>)
    @Delete
    fun delete(user: User)
    @Query("DELETE FROM users")
    fun deleteAllUsers():Int
    @Update
    fun update(user: User)

    @Update
    fun updates(user: List<User>)

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>


    @Query("SELECT COUNT(*) FROM users")
    fun getUsersCount(): Int

    @Query("SELECT * FROM users WHERE platform = :platform LIMIT 1")
    fun getUserByPlatform(platform: String):User
}