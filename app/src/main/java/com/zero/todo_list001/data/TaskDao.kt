package com.zero.todo_list001.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao // 标记为DAO接口
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY taskDate ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>> // 使用Flow，当数据变化时会自动通知

    @Query("SELECT * FROM tasks WHERE taskDate = :date ORDER BY id DESC")
    fun getTasksByDate(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category AND taskDate = :date ORDER BY id DESC")
    fun getTasksByCategoryAndDate(category: String, date: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 插入，如果冲突则替换
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks() // 示例：删除所有任务
}