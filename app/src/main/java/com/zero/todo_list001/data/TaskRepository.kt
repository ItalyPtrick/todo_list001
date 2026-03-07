package com.zero.todo_list001.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insert(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    fun getTasksByDate(date: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDate(date)
    }

    fun getTasksByCategoryAndDate(category: String, date: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksByCategoryAndDate(category, date)
    }
}