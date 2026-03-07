package com.zero.todo_list001.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insert(task: TaskEntity) = taskDao.insertTask(task)
    suspend fun update(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun delete(task: TaskEntity) = taskDao.deleteTask(task)

    fun getTasksByDate(date: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByDate(date)

    fun getTasksByCategoryAndDate(category: String, date: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategoryAndDate(category, date)

    // ---- 项目（Category）相关 ----

    fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAllCategories()

    suspend fun insertCategory(category: CategoryEntity) =
        categoryDao.insertCategory(category)

    // 删除项目时，同时删除该项目下的所有任务
    suspend fun deleteCategoryAndTasks(category: CategoryEntity) {
        taskDao.deleteTasksByCategory(category.name)
        categoryDao.deleteCategory(category)
    }
}