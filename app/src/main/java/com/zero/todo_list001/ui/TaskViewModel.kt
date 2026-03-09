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

    // 日历展开状态
    private val _isCalendarExpanded = MutableStateFlow(false)
    val isCalendarExpanded: StateFlow<Boolean> = _isCalendarExpanded.asStateFlow()

    // 当前查看的月份
    private val _currentViewMonth = MutableStateFlow(LocalDate.now())
    val currentViewMonth: StateFlow<LocalDate> = _currentViewMonth.asStateFlow()

    // 任务列表展开状态
    private val _isTaskListExpanded = MutableStateFlow(false)
    val isTaskListExpanded: StateFlow<Boolean> = _isTaskListExpanded.asStateFlow()

    // 年月选择器显示状态
    private val _showYearMonthPicker = MutableStateFlow(false)
    val showYearMonthPicker: StateFlow<Boolean> = _showYearMonthPicker.asStateFlow()

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
                repository.insert(
                    TaskEntity(
                        0,
                        "Market Research",
                        "个人",
                        "Done",
                        "10:00 AM",
                        R.mipmap.briefcase,
                        today,
                        true
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "Competitive Analysis",
                        "工作",
                        "In Progress",
                        "12:00 PM",
                        R.mipmap.briefcase,
                        today
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "Create Low-fidelity Wireframe",
                        "个人",
                        "To-do",
                        "07:00 PM",
                        R.mipmap.user,
                        today
                    )
                )
                repository.insert(
                    TaskEntity(
                        0,
                        "How to pitch a Design Sprint",
                        "工作",
                        "To-do",
                        "09:00 PM",
                        R.mipmap.book,
                        today + 1
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
        viewModelScope.launch { repository.insert(task) }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch { repository.update(task) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.delete(task) }
    }

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch { repository.insertCategory(category) }
    }

    fun deleteCategoryAndTasks(category: CategoryEntity) {
        viewModelScope.launch { repository.deleteCategoryAndTasks(category) }
    }

    // 新增的状态管理函数
    fun toggleCalendar() {
        _isCalendarExpanded.value = !_isCalendarExpanded.value
    }

    fun setViewMonth(month: LocalDate) {
        _currentViewMonth.value = month
    }

    fun setTaskListExpanded(expanded: Boolean) {
        _isTaskListExpanded.value = expanded
    }

    fun toggleYearMonthPicker() {
        _showYearMonthPicker.value = !_showYearMonthPicker.value
    }

    // 月份导航函数
    fun navigateToNextMonth() {
        _currentViewMonth.value = _currentViewMonth.value.plusMonths(1)
    }

    fun navigateToPreviousMonth() {
        _currentViewMonth.value = _currentViewMonth.value.minusMonths(1)
    }


    // 双击返回今天
    fun backToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today.toEpochDay()
        _currentViewMonth.value = today
    }

    // 后续用于控制通知设置界面的状态
    private val _showNotificationSetting = MutableStateFlow(false)
    val showNotificationSetting = _showNotificationSetting.asStateFlow()

    fun toggleNotificationSetting() {
        _showNotificationSetting.value = !_showNotificationSetting.value
    }
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