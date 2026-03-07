package com.zero.todo_list001.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zero.todo_list001.R
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

    // 当前选中的日期 (以Epoch Day表示)
    private val _selectedDate = MutableStateFlow(LocalDate.now().toEpochDay())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    // 当前选中的任务类别
    private val _selectedCategory = MutableStateFlow("All") // 默认选中"All"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // 根据选中的日期和类别，从Repository获取任务列表
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

    init {
        // 可以在这里加载初始数据，或者通过UI交互触发
        viewModelScope.launch {
            // !!! 修复: 将 repository.getAllTasks() 更改为 repository.allTasks
            if (repository.allTasks.first().isEmpty()) { // 检查数据库是否为空
                val todayLocalDate = LocalDate.now()
                val todayEpochDay = todayLocalDate.toEpochDay()

                repository.insert(
                    TaskEntity(
                        0,
                        "Market Research",
                        "Grocery shopping app design",
                        "Done",
                        "10:00 AM",
                        R.mipmap.briefcase,
                        todayEpochDay,
                        true
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "Competitive Analysis",
                        "Grocery shopping app design",
                        "In Progress",
                        "12:00 PM",
                        R.mipmap.briefcase,
                        todayEpochDay
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "Create Low-fidelity Wireframe",
                        "Uber Eats redesign challenge",
                        "To-do",
                        "07:00 PM",
                        R.mipmap.user,
                        todayEpochDay
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "How to pitch a Design Sprint",
                        "About design sprint",
                        "To-do",
                        "09:00 PM",
                        R.mipmap.book,
                        todayLocalDate.plusDays(1).toEpochDay()
                    )
                )
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date.toEpochDay()
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}

// ViewModel的工厂类，用于创建带参数的ViewModel
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}