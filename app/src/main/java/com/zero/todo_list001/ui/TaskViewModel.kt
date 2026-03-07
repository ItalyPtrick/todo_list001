package com.zero.todo_list001.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zero.todo_list001.R
import com.zero.todo_list001.data.CategoryEntity
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val tasks: StateFlow<List<TaskEntity>> =
        _selectedDate.flatMapLatest { date ->
            _selectedCategory.flatMapLatest { category ->
                if (category == "All") {
                    repository.getTasksByDate(date)
                } else {
                    repository.getTasksByCategoryAndDate(category, date)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 所有项目（Category）的列表
    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            // 初始化默认项目
            if (repository.getAllCategories().first().isEmpty()) {
                repository.insertCategory(CategoryEntity(0, "个人", 0xFF5B8DEFL))
                repository.insertCategory(CategoryEntity(0, "工作", 0xFFE05C5CL))
                repository.insertCategory(CategoryEntity(0, "生日", 0xFFE0A83CL))
                repository.insertCategory(CategoryEntity(0, "倒数日", 0xFF6CC56CL))
            }
            // 初始化默认任务
            if (repository.getAllTasks().first().isEmpty()) {
                val today = LocalDate.now().toEpochDay()
                repository.insert(TaskEntity(0, "Market Research", "个人", "Done", "10:00 AM", R.mipmap.briefcase, today, true))
                repository.insert(TaskEntity(0, "Competitive Analysis", "工作", "In Progress", "12:00 PM", R.mipmap.briefcase, today))
                repository.insert(TaskEntity(0, "Create Low-fidelity Wireframe", "个人", "To-do", "07:00 PM", R.mipmap.user, today))
                repository.insert(TaskEntity(0, "How to pitch a Design Sprint", "工作", "To-do", "09:00 PM", R.mipmap.book, today + 1))
            }
        }
    }

    fun setSelectedDate(date: LocalDate) { _selectedDate.value = date.toEpochDay() }
    fun setSelectedCategory(category: String) { _selectedCategory.value = category }
    fun addTask(task: TaskEntity) { viewModelScope.launch { repository.insert(task) } }
    fun updateTask(task: TaskEntity) { viewModelScope.launch { repository.update(task) } }
    fun deleteTask(task: TaskEntity) { viewModelScope.launch { repository.delete(task) } }
    fun addCategory(category: CategoryEntity) { viewModelScope.launch { repository.insertCategory(category) } }
    fun deleteCategoryAndTasks(category: CategoryEntity) { viewModelScope.launch { repository.deleteCategoryAndTasks(category) } }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}