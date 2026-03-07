package com.zero.todo_list001.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY taskDate ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE taskDate = :date ORDER BY id DESC")
    fun getTasksByDate(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :category AND taskDate = :date ORDER BY id DESC")
    fun getTasksByCategoryAndDate(category: String, date: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // 删除某个项目下的所有任务（级联删除用）
    @Query("DELETE FROM tasks WHERE category = :categoryName")
    suspend fun deleteTasksByCategory(categoryName: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}