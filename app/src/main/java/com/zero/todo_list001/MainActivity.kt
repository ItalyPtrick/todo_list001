@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.zero.todo_list001

import android.os.Bundle
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.filled.Settings
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material3.Card
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import com.zero.todo_list001.data.AppDatabase
import com.zero.todo_list001.data.CategoryEntity
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.data.TaskRepository
import com.zero.todo_list001.ui.TaskViewModel
import com.zero.todo_list001.ui.TaskViewModelFactory
import com.zero.todo_list001.ui.theme.Todo_list001Theme
import com.zero.todo_list001.ui.util.px
import com.zero.todo_list001.ui.util.textPx
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.systemBarsPadding

// ===================== Activity =====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todo_list001Theme {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = TaskRepository(database.taskDao(), database.categoryDao())
                val factory = TaskViewModelFactory(repository)
                TodoListScreen(viewModel = viewModel(factory = factory))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoListScreen() {
    Todo_list001Theme {
        val db = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
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

// ===================== 任务表单对话框（新增/编辑两用） =====================

private enum class DialogView {
    MAIN, TIME_PICKER, STATUS_PICKER, ADD_PROJECT, DELETE_PROJECT_CONFIRM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    existingTask: TaskEntity?, selectedDate: Long, categories: List<CategoryEntity>,
    viewModel: TaskViewModel, onDismiss: () -> Unit, onConfirm: (TaskEntity) -> Unit
) {
    val isEdit = existingTask != null
    var currentView by remember { mutableStateOf(DialogView.MAIN) }

    val today = remember { LocalDate.now() }
    val initDate = remember(existingTask) {
        if (existingTask != null) LocalDate.ofEpochDay(existingTask.taskDate) else LocalDate.ofEpochDay(
            selectedDate
        )
    }
    val initOffset = remember(existingTask) {
        ChronoUnit.DAYS.between(today, initDate).toInt().coerceIn(-30, 30)
    }
    val initTimeParts = remember(existingTask) { existingTask?.date?.split(":") }

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var selectedProjectName by remember {
        mutableStateOf(
            existingTask?.category ?: categories.firstOrNull()?.name ?: ""
        )
    }
    var selectedStatus by remember { mutableStateOf(existingTask?.status ?: "To-do") }
    var selectedDateOffset by remember { mutableIntStateOf(initOffset) }
    var selectedHour by remember {
        mutableIntStateOf(
            initTimeParts?.getOrNull(0)?.toIntOrNull() ?: 9
        )
    }
    var selectedMinute by remember {
        mutableIntStateOf(
            initTimeParts?.getOrNull(1)?.toIntOrNull() ?: 0
        )
    }
    var projectToDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var newProjectName by remember { mutableStateOf("") }
    var newProjectColor by remember { mutableStateOf(0xFF2196F3L) }
    var keyboardShown by remember { mutableStateOf(false) }

    val displayTime = remember(selectedDateOffset, selectedHour, selectedMinute) {
        val date = today.plusDays(selectedDateOffset.toLong())
        "${date.year}年${date.monthValue}月${date.dayOfMonth}日 %02d:%02d".format(
            selectedHour,
            selectedMinute
        )
    }


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1C1C1E),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding()
        ) {
            when (currentView) {
                DialogView.MAIN -> MainFormView(
                    dialogTitle = if (isEdit) "编辑任务" else "新增任务",
                    confirmLabel = if (isEdit) "保存" else "确认",
                    title = title,
                    onTitleChange = { title = it },
                    categories = categories,
                    selectedProjectName = selectedProjectName,
                    onProjectSelected = { selectedProjectName = it },
                    onAddProject = { currentView = DialogView.ADD_PROJECT },
                    onDeleteProject = { cat ->
                        projectToDelete = cat; currentView = DialogView.DELETE_PROJECT_CONFIRM
                    },
                    displayTime = displayTime,
                    selectedStatus = selectedStatus,
                    onTimeClick = { currentView = DialogView.TIME_PICKER },
                    onStatusClick = { currentView = DialogView.STATUS_PICKER },
                    showKeyboard = !isEdit && !keyboardShown,
                    onKeyboardShown = { keyboardShown = true },
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (title.isNotBlank()) {
                            val taskEpochDay =
                                today.plusDays(selectedDateOffset.toLong()).toEpochDay()
                            onConfirm(
                                TaskEntity(
                                    id = existingTask?.id ?: 0,
                                    title = title.trim(),
                                    category = selectedProjectName,
                                    status = selectedStatus,
                                    date = "%02d:%02d".format(selectedHour, selectedMinute),
                                    iconResId = existingTask?.iconResId ?: R.mipmap.briefcase,
                                    taskDate = taskEpochDay,
                                    isCompleted = selectedStatus == "Done"
                                )
                            )
                        }
                    }
                )

                DialogView.TIME_PICKER -> TimePickerView(
                    initialDateOffset = selectedDateOffset,
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    today = today,
                    onConfirm = { d, h, m ->
                        selectedDateOffset = d; selectedHour = h; selectedMinute = m; currentView =
                        DialogView.MAIN
                    },
                    onBack = { currentView = DialogView.MAIN })

                DialogView.STATUS_PICKER -> StatusPickerView(
                    initialStatus = selectedStatus,
                    onConfirm = { selectedStatus = it; currentView = DialogView.MAIN },
                    onBack = { currentView = DialogView.MAIN })

                DialogView.ADD_PROJECT -> AddProjectView(
                    newProjectName = newProjectName,
                    onNameChange = { newProjectName = it },
                    selectedColor = newProjectColor,
                    onColorSelected = { newProjectColor = it },
                    onConfirm = {
                        if (newProjectName.isNotBlank()) {
                            viewModel.addCategory(
                                CategoryEntity(
                                    0,
                                    newProjectName.trim(),
                                    newProjectColor
                                )
                            ); selectedProjectName = newProjectName.trim(); newProjectName = ""
                        }; currentView = DialogView.MAIN
                    },
                    onBack = { currentView = DialogView.MAIN })

                DialogView.DELETE_PROJECT_CONFIRM -> DeleteProjectConfirmView(
                    categoryName = projectToDelete?.name ?: "",
                    onConfirm = {
                        projectToDelete?.let { viewModel.deleteCategoryAndTasks(it) }; if (selectedProjectName == projectToDelete?.name) selectedProjectName =
                        categories.firstOrNull { it.id != projectToDelete?.id }?.name
                            ?: ""; projectToDelete = null; currentView = DialogView.MAIN
                    },
                    onBack = { projectToDelete = null; currentView = DialogView.MAIN })
            }
        }
    }
}

// ===================== 主表单视图 =====================

@Composable
fun MainFormView(
    dialogTitle: String,
    confirmLabel: String,
    title: String, onTitleChange: (String) -> Unit,
    categories: List<CategoryEntity>,
    selectedProjectName: String, onProjectSelected: (String) -> Unit,
    onAddProject: () -> Unit, onDeleteProject: (CategoryEntity) -> Unit,
    displayTime: String, selectedStatus: String,
    onTimeClick: () -> Unit, onStatusClick: () -> Unit,
    showKeyboard: Boolean, onKeyboardShown: () -> Unit,
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val primary = MaterialTheme.colorScheme.primary

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            delay(150); focusRequester.requestFocus(); keyboardController?.show(); onKeyboardShown()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(dialogTitle, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

        OutlinedTextField(
            value = title, onValueChange = onTitleChange,
            label = { Text("任务标题", color = Color(0xFF9E9E9E)) }, singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = primary, unfocusedBorderColor = Color(0xFF444444)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Text("所属项目", color = Color(0xFF9E9E9E), fontSize = 13.sp)

        // 项目选择器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            categories.forEach { cat ->

                val isSel = cat.name == selectedProjectName

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSel)
                                Color(cat.colorValue)
                            else
                                Color.Transparent
                        )
                        .then(
                            if (!isSel)
                                Modifier.border(
                                    1.dp,
                                    Color(0xFF3A3A3C),
                                    RoundedCornerShape(20.dp)
                                )
                            else Modifier
                        )
                        .clickable { onProjectSelected(cat.name) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (isSel) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(cat.colorValue))
                        )
                    }

                    Text(
                        cat.name,
                        color = if (isSel) Color.White else Color(0xFFCCCCCC),
                        fontSize = 14.sp
                    )
                }
            }

            // 右侧新建项目类别入口
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2C2C2E))
                    .clickable { onAddProject() }
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .border(1.dp, Color.White, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }
        }

        Text("时间", color = Color(0xFF9E9E9E), fontSize = 13.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E), RoundedCornerShape(10.dp))
                .clickable { onTimeClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("开始", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(displayTime, color = Color(0xFF9E9E9E), fontSize = 14.sp)
        }

        Text("状态", color = Color(0xFF9E9E9E), fontSize = 13.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E), RoundedCornerShape(10.dp))
                .clickable { onStatusClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("状态", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(
                text = selectedStatus,
                color = when (selectedStatus) {
                    "Done" -> primary; "In Progress" -> Color(0xFFFF7D53); else -> Color(0xFF4FC3F7)
                },
                fontSize = 14.sp, fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("取消", color = Color(0xFF9E9E9E))
            }
            Button(
                onClick = onConfirm, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) { Text(confirmLabel, color = Color.White) }
        }
    }
}

// ===================== 时间选择器 =====================

@Composable
fun TimePickerView(
    initialDateOffset: Int, initialHour: Int, initialMinute: Int, today: LocalDate,
    onConfirm: (Int, Int, Int) -> Unit, onBack: () -> Unit
) {
    val dateRange = -30..30
    val dateItems = remember {
        dateRange.map { offset ->
            val date = today.plusDays(offset.toLong())
            val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINESE)
            when (offset) {
                0 -> "今天 $dow"; 1 -> "明天 $dow"; -1 -> "昨天 $dow"
                else -> "${date.monthValue}月${date.dayOfMonth}日 $dow"
            }
        }
    }
    val hourItems = remember { (0..23).map { "%02d".format(it) } }
    val minuteItems = remember { (0..59).map { "%02d".format(it) } }
    val dateInitialIdx = (initialDateOffset - dateRange.first).coerceIn(0, dateItems.size - 1)
    var selectedDateIdx by remember { mutableIntStateOf(dateInitialIdx) }
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("选择时间", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E), RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(dateItems, dateInitialIdx, { selectedDateIdx = it }, Modifier.weight(1.8f))
            WheelPicker(hourItems, initialHour, { selectedHour = it }, Modifier.weight(0.8f))
            Text(
                ":",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            WheelPicker(minuteItems, initialMinute, { selectedMinute = it }, Modifier.weight(0.8f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(
                    "返回",
                    color = Color(0xFF9E9E9E)
                )
            }
            Button(
                onClick = {
                    onConfirm(
                        selectedDateIdx + dateRange.first,
                        selectedHour,
                        selectedMinute
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("确认", color = Color.White) }
        }
    }
}

// ===================== 状态选择器 =====================

@Composable
fun StatusPickerView(initialStatus: String, onConfirm: (String) -> Unit, onBack: () -> Unit) {
    val statusItems = listOf("To-do", "In Progress", "Done")
    val initialIdx = statusItems.indexOf(initialStatus).coerceAtLeast(0)
    var selectedIdx by remember { mutableIntStateOf(initialIdx) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("选择状态", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
        ) {
            WheelPicker(statusItems, initialIdx, { selectedIdx = it }, Modifier.fillMaxWidth())
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(
                    "返回",
                    color = Color(0xFF9E9E9E)
                )
            }
            Button(
                onClick = { onConfirm(statusItems[selectedIdx]) }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("确认", color = Color.White) }
        }
    }
}

// ===================== 新建项目视图 =====================

@Composable
fun AddProjectView(
    newProjectName: String, onNameChange: (String) -> Unit,
    selectedColor: Long, onColorSelected: (Long) -> Unit,
    onConfirm: () -> Unit, onBack: () -> Unit
) {
    val presetColors =
        listOf(0xFF2196F3L, 0xFFE05C5CL, 0xFFE0A83CL, 0xFF6CC56CL, 0xFF9B59B6L, 0xFFE67E22L)
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("新建项目", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        OutlinedTextField(
            value = newProjectName, onValueChange = onNameChange,
            label = { Text("项目名称", color = Color(0xFF9E9E9E)) }, singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = primary, unfocusedBorderColor = Color(0xFF444444)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Text("选择颜色", color = Color(0xFF9E9E9E), fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            presetColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(color))
                        .clickable { onColorSelected(color) },
                    contentAlignment = Alignment.Center
                ) {
                    if (color == selectedColor) Icon(
                        Icons.Default.Check,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(
                    "返回",
                    color = Color(0xFF9E9E9E)
                )
            }
            Button(
                onClick = onConfirm, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) { Text("确认", color = Color.White) }
        }
    }
}

// ===================== 删除项目确认 =====================

@Composable
fun DeleteProjectConfirmView(categoryName: String, onConfirm: () -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("确认删除", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Text(
            "若删除「$categoryName」项目，则所有绑定此项目的任务将会被一起删除，此操作不可撤销。",
            color = Color(0xFFCCCCCC), fontSize = 14.sp, lineHeight = 20.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(
                    "返回",
                    color = Color(0xFF9E9E9E)
                )
            }
            Button(
                onClick = onConfirm, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
            ) { Text("确认删除", color = Color.White) }
        }
    }
}

// ===================== iOS风格滚轮 =====================

@Composable
fun WheelPicker(
    items: List<String>, initialIndex: Int = 0, onIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier, itemHeightDp: Dp = 44.dp, visibleCount: Int = 5
) {
    val halfVisible = visibleCount / 2
    val paddedItems =
        remember(items) { List(halfVisible) { "" } + items + List(halfVisible) { "" } }
    val listState =
        rememberLazyListState(initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)))
    val snapBehavior =
        androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(listState)
    val selectedIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(selectedIndex) { if (selectedIndex in items.indices) onIndexChanged(selectedIndex) }

    Box(modifier = modifier.height(itemHeightDp * visibleCount)) {
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(paddedItems) { paddedIdx, item ->
                val distance = abs((paddedIdx - halfVisible) - selectedIndex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isNotEmpty()) {
                        Text(
                            text = item,
                            color = when (distance) {
                                0 -> Color.White; 1 -> Color(0xFF8E8E93); else -> Color(0xFF48484A)
                            },
                            fontSize = if (distance == 0) 18.sp else 14.sp,
                            fontWeight = if (distance == 0) FontWeight.SemiBold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeightDp)
                .background(Color.White.copy(alpha = 0.07f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(itemHeightDp * halfVisible)
                .background(Brush.verticalGradient(listOf(Color(0xFF2C2C2E), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(itemHeightDp * halfVisible)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF2C2C2E))))
        )
    }
}

// ===================== 滑动任务 =====================

@Composable
fun SwipeableTaskItem(
    task: TaskEntity,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onLongClick: () -> Unit,
    onToggleSelection: () -> Unit
) {
    val density = LocalDensity.current
    var cardWidthPx by remember { mutableFloatStateOf(0f) }
    var rawDragPx by remember { mutableFloatStateOf(0f) }
    var isPendingEdit by remember { mutableStateOf(false) }
    val latestMaxRedPx = rememberUpdatedState(cardWidthPx * 0.25f)
    var isValidDrag by remember { mutableStateOf(true) }

    BackHandler(enabled = isPendingEdit) { isPendingEdit = false; rawDragPx = 0f }

    val animatedEditWidthPx by animateFloatAsState(
        targetValue = if (isPendingEdit) latestMaxRedPx.value else rawDragPx.coerceAtLeast(0f),
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "edit_zone"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.px()))
            .onSizeChanged { cardWidthPx = it.width.toFloat() }
            .pointerInput(isPendingEdit, isSelectionMode) {
                if (isSelectionMode) return@pointerInput
                if (!isPendingEdit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset -> isValidDrag = offset.x > size.width * 0.25f },
                        onDragEnd = {
                            if (isValidDrag && rawDragPx > 30f) isPendingEdit =
                                true else rawDragPx = 0f
                        },
                        onDragCancel = { if (isValidDrag) rawDragPx = 0f },
                        onHorizontalDrag = { _, delta ->
                            if (isValidDrag) rawDragPx =
                                (rawDragPx - delta).coerceIn(0f, latestMaxRedPx.value)
                        }
                    )
                } else {
                    detectHorizontalDragGestures { _, delta ->
                        if (delta > 20f) {
                            isPendingEdit = false; rawDragPx = 0f
                        }
                    }
                }
            }
    ) {
        val noOp: () -> Unit = {}
        AnimatedTaskItem(
            task = task, isSelectionMode = isSelectionMode, isSelected = isSelected,
            onToggleComplete = if (!isPendingEdit) onToggleComplete else noOp,
            onLongClick = if (!isPendingEdit) onLongClick else noOp,
            onToggleSelection = onToggleSelection
        )

        // 编辑蓝区
        if (animatedEditWidthPx > 0f) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(with(density) { animatedEditWidthPx.toDp() })
                        .then(if (isPendingEdit) Modifier.clickable {
                            isPendingEdit = false; rawDragPx = 0f; onEdit()
                        } else Modifier)
                        .background(
                            if (isPendingEdit) Color(0xFF4FC3F7) else Color(0xFF4FC3F7).copy(
                                alpha = 0.75f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPendingEdit) Icon(
                        Icons.Default.Edit,
                        "编辑",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTaskItem(
    task: TaskEntity, isSelectionMode: Boolean, isSelected: Boolean,
    onToggleComplete: () -> Unit, onLongClick: () -> Unit, onToggleSelection: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val isDone = task.isCompleted && !isSelectionMode

    val transition = updateTransition(targetState = isDone, label = "task_transition")

    val verticalPadding by transition.animateDp(
        transitionSpec = { tween(350, easing = FastOutSlowInEasing) }, label = "padding"
    ) { done -> if (done) 16.dp else 12.dp }

    val cardBg by transition.animateColor(
        transitionSpec = { tween(350, easing = FastOutSlowInEasing) }, label = "bg"
    ) { done -> if (done) Color(0xFFF9F9F9) else Color.White }

    val titleScale by transition.animateFloat(
        transitionSpec = { tween(350, easing = FastOutSlowInEasing) }, label = "title_scale"
    ) { done -> if (done) 1f else 0.93f }

    val strikeProgress by transition.animateFloat(
        transitionSpec = { tween(350, easing = FastOutSlowInEasing) }, label = "strike"
    ) { done -> if (done) 1f else 0f }

    //选中任务呼吸效果
    val selectionProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "selection_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "item_breathing")
    val breathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "item_border_alpha"
    )

    val currentBorderWidth = 2.dp * selectionProgress
    val currentBorderColor = primary.copy(alpha = breathAlpha * selectionProgress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = currentBorderWidth,
                color = currentBorderColor,
                shape = RoundedCornerShape(15.px())
            )
            .clip(RoundedCornerShape(15.px()))
            .background(cardBg)
            .combinedClickable(
                onClick = { if (isSelectionMode) onToggleSelection() else onToggleComplete() },
                onLongClick = { if (!isSelectionMode) onLongClick() }
            )
            .padding(horizontal = 14.dp, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedVisibility(
            visible = isSelectionMode,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(350))
        ) {
            Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.CenterStart) {

                // --- 【高级感升级 3】勾选框Q弹切换：加入物理弹簧模型 ---
                AnimatedContent(
                    targetState = isSelected,
                    transitionSpec = {
                        // 进入时放大弹跳 (spring) + 渐显，退出时轻微缩小 + 渐隐
                        (scaleIn(
                            spring(
                                dampingRatio = 0.5f,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(tween(200))) togetherWith
                                (scaleOut(tween(150)) + fadeOut(tween(150)))
                    },
                    label = "checkbox_bounce"
                ) { selected ->
                    if (selected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "已选中",
                            tint = primary,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        // 未选中的空心小圆圈
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, Color(0xFFD0D0D0), CircleShape)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            transition.AnimatedVisibility(
                visible = { !it },
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(350)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(350))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(task.category, fontSize = 11.sp, color = Color(0xFF6E6A7C))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFFFE4F2)), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(task.iconResId),
                            null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 30.dp)
            ) {
                transition.AnimatedVisibility(
                    visible = { it },
                    enter = expandHorizontally(
                        expandFrom = Alignment.Start,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(350)),
                    exit = shrinkHorizontally(
                        shrinkTowards = Alignment.Start,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(350))
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        null,
                        tint = Color(0xFF6CC56C),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                    )
                }

                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    color = if (isDone) Color(0xFF999999) else Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = titleScale
                            scaleY = titleScale
                            transformOrigin = TransformOrigin(0f, 0.5f)
                        }
                        .drawBehind {
                            if (strikeProgress > 0f) {
                                drawLine(
                                    color = Color(0xFF999999),
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(size.width * strikeProgress, size.height / 2),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                        }
                )

                Spacer(modifier = Modifier.weight(1f))

                transition.AnimatedVisibility(
                    visible = { it },
                    enter = expandHorizontally(
                        expandFrom = Alignment.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(350)),
                    exit = shrinkHorizontally(
                        shrinkTowards = Alignment.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(350))
                ) {
                    Text(
                        text = "完毕",
                        color = Color(0xFF6CC56C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            transition.AnimatedVisibility(
                visible = { !it },
                enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(350)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(350))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(painterResource(R.mipmap.time), null, modifier = Modifier.size(14.dp))
                        Text(task.date, fontSize = 11.sp, color = Color(0xFFAB94FF))
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(task.status, fontSize = 9.sp, color = Color(0xFF0087FF))
                    }
                }
            }
        }
    }
}

@Composable
fun FullTaskItemContent(task: TaskEntity) {
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(3.px())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(task.category, fontSize = 11.textPx(), color = Color(0xFF6E6A7C))
            Box(
                modifier = Modifier
                    .size(24.px())
                    .clip(RoundedCornerShape(7.px()))
                    .background(
                        when (task.iconResId) {
                            R.mipmap.briefcase -> Color(0xFFFFE4F2)
                            R.mipmap.user -> Color(0xFFEDE4FF)
                            R.mipmap.book -> Color(0xFFFFE6D4)
                            else -> Color.White
                        }
                    ),
                contentAlignment = Alignment.Center
            ) { Image(painterResource(task.iconResId), null, modifier = Modifier.size(14.px())) }
        }
        Text(task.title, fontSize = 14.textPx(), color = Color.Black)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.px())
            ) {
                Image(painterResource(R.mipmap.time), null, modifier = Modifier.size(14.px()))
                Text(task.date, fontSize = 11.textPx(), color = Color(0xFFAB94FF))
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        when (task.status) {
                            "Done" -> Color(0xFFD6EAFF); "In Progress" -> Color(0xFFFFE9E1)
                            "To-do" -> Color(0xFFE3F2FF); else -> Color.White
                        }
                    )
                    .padding(horizontal = 6.px(), vertical = 2.px()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    task.status, fontSize = 9.textPx(),
                    color = when (task.status) {
                        "Done" -> primary; "In Progress" -> Color(0xFFFF7D53)
                        "To-do" -> Color(0xFF0087FF); else -> Color.Gray
                    }
                )
            }
        }
    }
}

// ===================== 任务列表 =====================
@Composable
fun AdaptiveTaskList(
    tasks: List<TaskEntity>,
    isExpanded: Boolean,
    selectedDate: Long,
    isSelectionMode: Boolean,
    selectedTaskIds: Set<Int>,
    onToggleSelection: (Int) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onLongPressTask: (TaskEntity) -> Unit
) {
    val sortedTasks = remember(tasks) { tasks.sortedBy { it.date } }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.px())
            .padding(top = 28.px()),
        verticalArrangement = Arrangement.spacedBy(12.px())
    ) {
        if (sortedTasks.isEmpty()) {
            item {
                Text(
                    "暂无任务，点击 + 新增吧！",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(sortedTasks.size) { index ->
                val task = sortedTasks[index]
                key(task.id) {
                    SwipeableTaskItem(
                        task = task,
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedTaskIds.contains(task.id),
                        onToggleComplete = { onToggleComplete(task) },
                        onEdit = { onEditTask(task) },
                        onLongClick = { onLongPressTask(task) },
                        onToggleSelection = { onToggleSelection(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoListTopBar(viewModel: TaskViewModel = viewModel()) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 22.px(), vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "日程任务",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A2E)
        )
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "通知设置",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(24.dp)
                .clickable { viewModel.toggleNotificationSetting() }
        )
    }
}

@Composable
fun MultiSelectTopBar(
    selectedCount: Int,
    onSelectAll: () -> Unit,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        title = {
            val modifierShake = Modifier.graphicsLayer { rotationZ = rotation } // 抖动修饰符
            if (selectedCount > 0) {
                Text(
                    text = "已选择${selectedCount}项",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF3B30), // 变红！
                    modifier = modifierShake // 加抖动！
                )
            } else {
                Text(
                    text = "删除任务",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF3B30),
                    modifier = modifierShake
                )
            }
        },
        navigationIcon = {
            TextButton(onClick = onSelectAll) {
                Text(
                    text = if (selectedCount > 0) "取消全选" else "全选",
                    color = Color(0xFFE0A83C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            TextButton(onClick = onCancel) {
                Text(
                    "取消",
                    color = Color(0xFFE0A83C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

// ===================== 日期行 =====================

data class DayInfo(
    val month: String,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val isToday: Boolean,
    val localDate: LocalDate
)

val dayInfoList: List<DayInfo>
    @Composable
    get() = remember {
        buildList {
            val today = LocalDate.now()
            val locale = Locale.getDefault()
            for (i in -3..3) {
                val day = today.plusDays(i.toLong())
                add(
                    DayInfo(
                        month = day.month.getDisplayName(TextStyle.SHORT, locale),
                        dayOfMonth = String.format(locale, "%02d", day.dayOfMonth),
                        dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                        isToday = day == today, localDate = day
                    )
                )
            }
        }
    }

@Composable
fun DateRow(viewModel: TaskViewModel, selectedDate: Long) {
    val dates = dayInfoList
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // 均匀分配
        verticalAlignment = Alignment.CenterVertically
    ) {
        dates.forEach { date ->
            val isSelected = date.localDate.toEpochDay() == selectedDate
            val textColor = if (isSelected) Color.White else Color(0xFF333333)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) primary else Color.Transparent)
                    .clickable { viewModel.setSelectedDate(date.localDate) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    date.dayOfMonth,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
        }
    }
}

// ===================== 选定日期显示 =====================

@Composable
fun SelectedDateDisplay(selectedDate: Long) {
    val date = LocalDate.ofEpochDay(selectedDate)
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINESE)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${date.monthValue}月${date.dayOfMonth}日",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
        Text(text = dayOfWeek, fontSize = 14.sp, color = Color(0xFF6E6A7C))
    }
}

// ===================== 侧边拖拽小竖条 =====================
@Composable
fun VerticalDragHandles(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(vertical = 12.dp, horizontal = 6.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFD0D0D0))
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFD0D0D0))
        )
    }
}

// ===================== 可展开的月历视图 =====================

@Composable
fun ExpandableCalendar(
    viewModel: TaskViewModel,
    selectedDate: Long,
    currentMonth: LocalDate,
    onMonthYearClick: () -> Unit
) {
    val screenWidthPx =
        with(LocalDensity.current) { androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp.toPx() }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = dragAccumulator,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "calendar_drag"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // 松手时判断是否达到翻页阈值
                        if (dragAccumulator > screenWidthPx / 2.5) {
                            viewModel.navigateToPreviousMonth()
                        } else if (dragAccumulator < -screenWidthPx / 2.5) {
                            viewModel.navigateToNextMonth()
                        }
                        dragAccumulator = 0f // 松手后归零，AnimatedContent 会接管翻页效果
                    },
                    onDragCancel = { dragAccumulator = 0f }
                ) { _, dragAmount ->
                    dragAccumulator += dragAmount // 记录滑动偏移量
                }
            }
    ) {
        // 应用跟手的偏移量
        Box(modifier = Modifier.offset(x = with(LocalDensity.current) { animatedOffsetX.toDp() })) {
            AnimatedContent(
                targetState = currentMonth,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { w -> w } + fadeIn() togetherWith slideOutHorizontally { w -> -w / 2 } + fadeOut()
                    } else {
                        slideInHorizontally { w -> -w } + fadeIn() togetherWith slideOutHorizontally { w -> w / 2 } + fadeOut()
                    }.using(SizeTransform(clip = false))
                }, label = "calendar_page"
            ) { month ->
                Column {
                    CalendarMonthNavigation(
                        currentMonth = month,
                        onPreviousMonth = { viewModel.navigateToPreviousMonth() },
                        onNextMonth = { viewModel.navigateToNextMonth() },
                        onMonthYearClick = onMonthYearClick,
                        onBackToToday = { viewModel.backToToday() }
                    )
                    WeekdayHeader()
                    CalendarGrid(
                        month = month,
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.setSelectedDate(it) })
                }
            }
        }
    }
}


@Composable
fun CalendarMonthNavigation(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthYearClick: () -> Unit,
    onBackToToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painterResource(R.mipmap.left), // 向左箭头图标
            contentDescription = "Previous Month",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(24.dp)
                .clickable { onPreviousMonth() }
        )
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年MM月")),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMonthYearClick() },
                    onDoubleTap = { onBackToToday() } // 双击触发回今天
                )
            }
        )
        Icon(
            painterResource(R.mipmap.left), // 使用 left.png 并镜像
            contentDescription = "Next Month",
            tint = Color(0xFF333333),
            modifier = Modifier
                .size(24.dp)
                .clickable { onNextMonth() }
                .scale(scaleX = -1f, scaleY = 1f) // 镜像
        )
    }
}


@Composable
fun WeekdayHeader() {
    val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekdays.forEach { day ->
            Text(
                text = day,
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CalendarGrid(
    month: LocalDate,
    selectedDate: Long,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = month.withDayOfMonth(1)
    val firstDayOfWeek =
        firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday (根据 Locale 调整)
    val daysInMonth = month.lengthOfMonth()

    // 日期列表，包括上个月和下个月的灰显日期
    val days = buildList {
        // 上个月的灰显日期
        for (i in (firstDayOfWeek - 1) downTo 0) {
            add(firstDayOfMonth.minusDays((i + 1).toLong()) to true)
        }
        // 当前月的日期
        for (i in 1..daysInMonth) {
            add(month.withDayOfMonth(i) to false)
        }
        // 补充下个月的日期，确保显示6行7列的网格 (42个日期)
        val remainingDays = 42 - size
        for (i in 1..remainingDays) {
            add(month.plusMonths(1).withDayOfMonth(i) to true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 每周一行
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0 until 7) {
                    val dayIndex = week * 7 + dayOfWeek
                    if (dayIndex < days.size) {
                        val (date, isGreyed) = days[dayIndex]
                        val isSelected = date.toEpochDay() == selectedDate
                        val isToday = date == LocalDate.now()

                        DayItem(
                            date = date,
                            isGreyed = isGreyed,
                            isSelected = isSelected,
                            isToday = isToday,
                            onDateSelected = onDateSelected
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DayItem(
    date: LocalDate,
    isGreyed: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val backgroundColor = when {
        isSelected -> primary
        isToday && !isGreyed -> Color(0xFFE0E0E0)
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isGreyed -> Color(0xFFCCCCCC)
        else -> Color(0xFF1A1A2E)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// ===================== 年月选择器对话框 =====================

@Composable
fun YearMonthPickerDialog(
    currentMonth: LocalDate,
    onMonthYearSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    // 使用临时状态保存当前滚轮选中的值
    var tempYear by remember { mutableIntStateOf(currentMonth.year) }
    var tempMonth by remember { mutableIntStateOf(currentMonth.monthValue) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color(0xFF1C1C1E), RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "选择年月",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    YearPicker(
                        currentYear = currentMonth.year,
                        onYearSelected = { tempYear = it }, // 只更新临时状态
                        modifier = Modifier.weight(1f)
                    )
                    MonthPicker(
                        currentMonth = currentMonth.monthValue,
                        onMonthSelected = { tempMonth = it }, // 只更新临时状态
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("取消", color = Color(0xFF9E9E9E))
                    }
                    Button(
                        onClick = {
                            // 点击确认时，才将合并后的年月传出去并关闭
                            onMonthYearSelected(LocalDate.of(tempYear, tempMonth, 1))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("确认", color = Color.White) }
                }
            }
        }
    }
}

// ===================== 分类筛选行 =====================

val CustomShape = GenericShape { size, _ ->
    val (w, h) = size.width to size.height
    moveTo(0f, h / 2); cubicTo(0f, h / 8, 0f, 0f, w / 2, 0f); cubicTo(
    w,
    0f,
    w,
    h / 8,
    w,
    h / 2
)
    cubicTo(w, h - h / 8, w, h, w / 2, h); cubicTo(0f, h, 0f, h - h / 8, 0f, h / 2); close()
}

@Composable
fun CategoryRow(viewModel: TaskViewModel, selectedCategory: String, onSwipeUp: () -> Unit) {
    // 映射 UI 文本与底层 Status 逻辑
    val categories = listOf(
        "All" to "全部",
        "To-do" to "待做",
        "In Progress" to "进行",
        "Done" to "完毕"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) onSwipeUp() // 上滑收起日历
                }
            },
        horizontalArrangement = Arrangement.SpaceEvenly // 均匀分布
    ) {
        categories.forEach { (catId, catName) ->
            val isSelected = catId == selectedCategory

            // 动态颜色配置
            val bgColor = when (catId) {
                "All" -> Color.White
                "To-do" -> Color(0xFF4FC3F7)
                "In Progress" -> Color(0xFFFF7D53)
                "Done" -> Color(0xFF6CC56C)
                else -> Color.White
            }
            val textColor =
                if (catId == "All" && !isSelected) Color(0xFF333333) else if (catId == "All" && isSelected) Color.Black else Color.White

            // 选中按钮实行悬浮阴影与浅灰框动画
            val elevation by animateDpAsState(
                targetValue = if (isSelected) 6.dp else 0.dp,
                label = "elevation"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) Color.LightGray else Color.Transparent,
                label = "border"
            )

            // 下划线进度动画 (0f -> 1f)
            val underlineProgress by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(300),
                label = "underline"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .shadow(elevation, RoundedCornerShape(20.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .clickable { viewModel.setSelectedCategory(catId) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = catName,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.drawBehind {
                        // 绘制从左到右的下划线
                        if (underlineProgress > 0f) {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height + 4.dp.toPx() // 文字下方
                            drawLine(
                                color = textColor,
                                start = Offset(0f, y),
                                end = Offset(size.width * underlineProgress, y), // 宽度随动画变长
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                )
            }
        }
    }
}

// ===================== 底部导航栏 =====================

val CustomShape2 = GenericShape { size, _ ->
    val (w, h) = size.width to size.height;
    val fw = w / 5f
    moveTo(0f, h); lineTo(0f, h / 3); quadraticTo(0f, 0f, h / 3f, 0f)
    lineTo(w * 2 / 5 - fw / 8, 0f); cubicTo(
    w * 2 / 5 + fw / 8,
    0f,
    w * 2 / 5 + fw / 8,
    h / 2,
    w / 2,
    h / 2
)
    cubicTo(w * 3 / 5 - fw / 8, h / 2, w * 3 / 5 - fw / 8, 0f, w * 3 / 5 + fw / 8, 0f)
    lineTo(w - h / 3, 0f); quadraticTo(w, 0f, w, h / 3f); lineTo(w, h); lineTo(
    0f,
    h
); close()
}

@Composable
fun TodoListBottomBar(
    isSelectionMode: Boolean = false,
    hasSelection: Boolean = false,
    onAddClick: () -> Unit = {},
    onDeleteSelected: () -> Unit = {}
) {
    val list = remember {
        listOf(
            R.mipmap.home,
            R.mipmap.calendar,
            R.mipmap.add,
            R.mipmap.doc,
            R.mipmap.profile
        )
    }
    var currentIndex by remember { mutableIntStateOf(0) }
    val primary = MaterialTheme.colorScheme.primary
    val trashColor = Color(0xFFFF3B30)
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.px())
                .clip(CustomShape2)
                .background(Color(0xFFE3F0FF))
                .navigationBarsPadding()
        ) {
            list.fastForEachIndexed { index, icon ->
                if (index == 2) Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                else Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { currentIndex = index },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(icon), null,
                        modifier = Modifier
                            .shadow(
                                if (currentIndex == index) 8.dp else 0.dp,
                                CircleShape,
                                ambientColor = primary,
                                spotColor = primary
                            )
                            .size(24.px())
                    )
                }
            }
        }

        Surface(
            onClick = if (isSelectionMode) onDeleteSelected else onAddClick,
            modifier = Modifier
                .offset(0.dp, (-32).px())
                .align(Alignment.Center)
                .size(44.px()),
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = if (isSelectionMode) trashColor else primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isSelectionMode) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除选中",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                } else {
                    Image(painterResource(list[2]), null, modifier = Modifier.size(24.px()))
                }
            }
        }
    }
}

// ===================== 年份选择器 =====================

@Composable
fun YearPicker(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = remember { (currentYear - 100..currentYear + 100).toList() }
    val initialIndex = years.indexOf(currentYear).coerceAtLeast(0)

    WheelPicker(
        items = years.map { it.toString() },
        initialIndex = initialIndex,
        onIndexChanged = { index -> onYearSelected(years[index]) },
        modifier = modifier
    )
}

// ===================== 月份选择器 =====================

@Composable
fun MonthPicker(
    currentMonth: Int,
    onMonthSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val months = remember { (1..12).toList() }
    val initialIndex = months.indexOf(currentMonth).coerceAtLeast(0)

    WheelPicker(
        items = months.map { "${it}月" },
        initialIndex = initialIndex,
        onIndexChanged = { index -> onMonthSelected(months[index]) },
        modifier = modifier
    )
}