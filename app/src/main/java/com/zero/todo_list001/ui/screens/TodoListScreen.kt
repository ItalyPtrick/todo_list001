@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.zero.todo_list001.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zero.todo_list001.*
import com.zero.todo_list001.R
import com.zero.todo_list001.data.AppDatabase
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.data.TaskRepository
import com.zero.todo_list001.ui.TaskViewModel
import com.zero.todo_list001.ui.TaskViewModelFactory
import com.zero.todo_list001.ui.theme.Todo_list001Theme
import com.zero.todo_list001.ui.components.MultiSelectTopBar
import com.zero.todo_list001.ui.components.TodoListTopBar
import com.zero.todo_list001.ui.components.TodoListBottomBar
import com.zero.todo_list001.ui.components.AdaptiveTaskList
import com.zero.todo_list001.ui.components.*
import com.zero.todo_list001.ui.dialog.TaskBottomSheet
import com.zero.todo_list001.ui.components.CategoryRow

@Preview(showBackground = true)
@Composable
fun PreviewTodoListScreen() {
    Todo_list001Theme {
        val db = AppDatabase.getDatabase(LocalContext.current)
        val repo = TaskRepository(db.taskDao(), db.categoryDao())
        TodoListScreen(viewModel = viewModel(factory = TaskViewModelFactory(repo)))
    }
}

// ===================== 主屏幕 =====================

@Composable
fun TodoListScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isCalendarExpanded by viewModel.isCalendarExpanded.collectAsStateWithLifecycle() // 收集日历展开状态
    val currentViewMonth by viewModel.currentViewMonth.collectAsStateWithLifecycle()   // 收集当前查看月份
    val isTaskListExpanded by viewModel.isTaskListExpanded.collectAsStateWithLifecycle() // 收集任务列表展开状态
    val showYearMonthPicker by viewModel.showYearMonthPicker.collectAsStateWithLifecycle() // 收集年月选择器显示状态
    val haptic = LocalHapticFeedback.current
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedTaskIds by remember { mutableStateOf(setOf<Int>()) }
    BackHandler(enabled = isSelectionMode) {
        isSelectionMode = false
        selectedTaskIds = emptySet()
    }

    if (showAddDialog) {
        TaskBottomSheet(
            existingTask = null, selectedDate = selectedDate, categories = categories,
            viewModel = viewModel, onDismiss = { showAddDialog = false },
            onConfirm = { task -> viewModel.addTask(task); showAddDialog = false }
        )
    }
    if (editingTask != null) {
        TaskBottomSheet(
            existingTask = editingTask,
            selectedDate = editingTask!!.taskDate,
            categories = categories,
            viewModel = viewModel,
            onDismiss = { editingTask = null },
            onConfirm = { updated -> viewModel.updateTask(updated); editingTask = null }
        )
    }

    //scaffold
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(R.mipmap.bg),
                    contentScale = ContentScale.Crop
                ),
            containerColor = Color.Transparent,
            topBar = {
                // 顶栏动画切换
                AnimatedContent(targetState = isSelectionMode, label = "topbar") { mode ->
                    if (mode) {
                        MultiSelectTopBar(
                            selectedCount = selectedTaskIds.size,
                            onSelectAll = {
                                if (selectedTaskIds.size == tasks.size && tasks.isNotEmpty()) {
                                    selectedTaskIds = emptySet()
                                } else {
                                    selectedTaskIds = tasks.map { it.id }.toSet()
                                }
                            },
                            onCancel = {
                                isSelectionMode = false
                                selectedTaskIds = emptySet()
                            }
                        )
                    } else {
                        TodoListTopBar()
                    }
                }
            },
            bottomBar = {
                TodoListBottomBar(
                    isSelectionMode = isSelectionMode,
                    hasSelection = selectedTaskIds.isNotEmpty(),
                    onAddClick = { showAddDialog = true },
                    onDeleteSelected = {
                        selectedTaskIds.forEach { id ->
                            tasks.find { it.id == id }?.let {
                                viewModel.deleteTask(it)
                            }
                        }
                        isSelectionMode = false
                        selectedTaskIds = emptySet()
                    }
                )
            }
        ) { innerPadding ->

            var horizontalDragOffset by remember { mutableFloatStateOf(0f) }
            var expandDirection by remember { mutableIntStateOf(1) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // --- 1. 日历动画容器 ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = tween(
                                    350,
                                    easing = FastOutSlowInEasing
                                )
                            )
                    ) {

                        AnimatedVisibility(
                            visible = !isCalendarExpanded,
                            enter = fadeIn(tween(250)) + expandVertically(expandFrom = Alignment.Top),
                            exit = fadeOut(tween(300)) +
                                    slideOutHorizontally(
                                        targetOffsetX = { it * -expandDirection },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) +
                                    shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        animationSpec = tween(300)
                                    ) +
                                    scaleOut(
                                        targetScale = 0.6f,
                                        transformOrigin = TransformOrigin(
                                            if (expandDirection == 1) 0f else 1f,
                                            0f
                                        ),
                                        animationSpec = tween(300)
                                    )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(x = with(LocalDensity.current) { horizontalDragOffset.toDp() })
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                if (horizontalDragOffset > 80f) {
                                                    expandDirection = 1
                                                    viewModel.toggleCalendar()
                                                    viewModel.setTaskListExpanded(false)
                                                } else if (horizontalDragOffset < -80f) {
                                                    expandDirection = -1
                                                    viewModel.toggleCalendar()
                                                    viewModel.setTaskListExpanded(false)
                                                }
                                                horizontalDragOffset = 0f
                                            },
                                            onDragCancel = { horizontalDragOffset = 0f }
                                        ) { _, dragAmount ->
                                            horizontalDragOffset += dragAmount * 0.4f
                                        }
                                    }
                            ) {

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        DateRow(
                                            viewModel = viewModel,
                                            selectedDate = selectedDate
                                        )
                                        SelectedDateDisplay(
                                            selectedDate = selectedDate
                                        )
                                    }

                                    VerticalDragHandles(
                                        modifier = Modifier.matchParentSize()
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isCalendarExpanded,
                            enter = fadeIn(tween(350)) +
                                    slideInHorizontally(
                                        initialOffsetX = { it * -expandDirection },
                                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                                    ) +
                                    expandVertically(
                                        expandFrom = Alignment.Top,
                                        animationSpec = tween(350)
                                    ),
                            exit = fadeOut(tween(300)) +
                                    slideOutHorizontally(
                                        targetOffsetX = { it * -expandDirection },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) +
                                    shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        animationSpec = tween(300)
                                    ) +
                                    scaleOut(
                                        targetScale = 0.8f,
                                        transformOrigin = TransformOrigin(
                                            if (expandDirection == 1) 0f else 1f,
                                            0f
                                        ),
                                        animationSpec = tween(300)
                                    )
                        ) {
                            ExpandableCalendar(
                                viewModel = viewModel,
                                selectedDate = selectedDate,
                                currentMonth = currentViewMonth,
                                onMonthYearClick = {
                                    viewModel.toggleYearMonthPicker()
                                }
                            )
                        }
                    }

                    // --- 2. 分类按钮 ---
                    AnimatedVisibility(
                        visible = !isSelectionMode,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        CategoryRow(
                            viewModel = viewModel,
                            selectedCategory = selectedCategory,
                            onSwipeUp = {
                                if (isCalendarExpanded) {
                                    viewModel.toggleCalendar()
                                }
                            }
                        )
                    }

                    // --- 3. 任务列表 ---
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {

                        AdaptiveTaskList(
                            tasks = tasks,
                            isExpanded = isTaskListExpanded,
                            selectedDate = selectedDate,
                            isSelectionMode = isSelectionMode,
                            selectedTaskIds = selectedTaskIds,
                            onToggleSelection = { id ->
                                selectedTaskIds =
                                    if (selectedTaskIds.contains(id))
                                        selectedTaskIds - id
                                    else
                                        selectedTaskIds + id
                            },
                            onDeleteTask = {
                                viewModel.deleteTask(it)
                            },
                            onToggleComplete = { task ->
                                viewModel.updateTask(
                                    task.copy(isCompleted = !task.isCompleted)
                                )
                            },
                            onEditTask = { editingTask = it },
                            onLongPressTask = { task ->
                                if (!isSelectionMode) {
                                    isSelectionMode = true
                                    selectedTaskIds = setOf(task.id)
                                }
                            }
                        )

                        if (isCalendarExpanded) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.toggleCalendar()
                                            }
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}