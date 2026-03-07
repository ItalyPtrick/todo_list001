@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.zero.todo_list001

import android.os.Bundle
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
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
    val haptic = LocalHapticFeedback.current

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    if (showAddDialog) {
        TaskFormDialog(
            existingTask = null,
            selectedDate = selectedDate,
            categories = categories,
            viewModel = viewModel,
            onDismiss = { showAddDialog = false },
            onConfirm = { task -> viewModel.addTask(task); showAddDialog = false }
        )
    }
    if (editingTask != null) {
        TaskFormDialog(
            existingTask = editingTask,
            selectedDate = editingTask!!.taskDate,
            categories = categories,
            viewModel = viewModel,
            onDismiss = { editingTask = null },
            onConfirm = { updated -> viewModel.updateTask(updated); editingTask = null }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .paint(painter = painterResource(R.mipmap.bg), contentScale = ContentScale.Crop),
        containerColor = Color.Transparent,
        topBar = { TodoListTopBar() },
        bottomBar = { TodoListBottomBar(onAddClick = { showAddDialog = true }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DateRow(viewModel = viewModel, selectedDate = selectedDate)
            CategoryRow(viewModel = viewModel, selectedCategory = selectedCategory)
            TaskList(
                tasks = tasks,
                onDeleteTask = { viewModel.deleteTask(it) },
                onToggleComplete = { task ->
                    // 修复3：只在"完成"时震动，"取消完成"不震动
                    if (!task.isCompleted) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    viewModel.updateTask(
                        task.copy(
                            isCompleted = !task.isCompleted,
                            status = if (!task.isCompleted) "Done" else "To-do"
                        )
                    )
                },
                onEditTask = { editingTask = it }
            )
        }
    }
}

// ===================== 统一的任务表单对话框（新增/编辑两用） =====================

private enum class DialogView {
    MAIN, TIME_PICKER, STATUS_PICKER, ADD_PROJECT, DELETE_PROJECT_CONFIRM
}

@Composable
fun TaskFormDialog(
    existingTask: TaskEntity?,          // null = 新增；非null = 编辑
    selectedDate: Long,
    categories: List<CategoryEntity>,
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onConfirm: (TaskEntity) -> Unit
) {
    val isEdit = existingTask != null
    var currentView by remember { mutableStateOf(DialogView.MAIN) }

    // 初始化：编辑时从现有任务取值，新增时取默认值
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
        mutableStateOf(existingTask?.category ?: categories.firstOrNull()?.name ?: "")
    }
    var selectedStatus by remember { mutableStateOf(existingTask?.status ?: "To-do") }
    var selectedDateOffset by remember { mutableIntStateOf(initOffset) }
    var selectedHour by remember {
        mutableIntStateOf(initTimeParts?.getOrNull(0)?.toIntOrNull() ?: 9)
    }
    var selectedMinute by remember {
        mutableIntStateOf(initTimeParts?.getOrNull(1)?.toIntOrNull() ?: 0)
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(Color(0xFF1C1C1E), RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            when (currentView) {
                DialogView.MAIN -> MainFormView(
                    dialogTitle = if (isEdit) "编辑任务" else "新增任务",
                    confirmLabel = if (isEdit) "保存" else "确认",
                    title = title, onTitleChange = { title = it },
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
                    initialHour = selectedHour, initialMinute = selectedMinute, today = today,
                    onConfirm = { d, h, m ->
                        selectedDateOffset = d; selectedHour = h; selectedMinute = m; currentView =
                        DialogView.MAIN
                    },
                    onBack = { currentView = DialogView.MAIN }
                )

                DialogView.STATUS_PICKER -> StatusPickerView(
                    initialStatus = selectedStatus,
                    onConfirm = { selectedStatus = it; currentView = DialogView.MAIN },
                    onBack = { currentView = DialogView.MAIN }
                )

                DialogView.ADD_PROJECT -> AddProjectView(
                    newProjectName = newProjectName, onNameChange = { newProjectName = it },
                    selectedColor = newProjectColor, onColorSelected = { newProjectColor = it },
                    onConfirm = {
                        if (newProjectName.isNotBlank()) {
                            viewModel.addCategory(
                                CategoryEntity(
                                    0,
                                    newProjectName.trim(),
                                    newProjectColor
                                )
                            )
                            selectedProjectName = newProjectName.trim()
                            newProjectName = ""
                        }
                        currentView = DialogView.MAIN
                    },
                    onBack = { currentView = DialogView.MAIN }
                )

                DialogView.DELETE_PROJECT_CONFIRM -> DeleteProjectConfirmView(
                    categoryName = projectToDelete?.name ?: "",
                    onConfirm = {
                        projectToDelete?.let { viewModel.deleteCategoryAndTasks(it) }
                        if (selectedProjectName == projectToDelete?.name)
                            selectedProjectName =
                                categories.firstOrNull { it.id != projectToDelete?.id }?.name ?: ""
                        projectToDelete = null; currentView = DialogView.MAIN
                    },
                    onBack = { projectToDelete = null; currentView = DialogView.MAIN }
                )
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2C2C2E), RoundedCornerShape(10.dp))
        ) {
            categories.forEachIndexed { index, cat ->
                ProjectListItem(
                    category = cat, isSelected = cat.name == selectedProjectName,
                    onSelect = { onProjectSelected(cat.name) }, onDelete = { onDeleteProject(cat) }
                )
                if (index < categories.size - 1)
                    HorizontalDivider(
                        color = Color(0xFF3A3A3C),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
            }
            HorizontalDivider(
                color = Color(0xFF3A3A3C),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddProject() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = primary, modifier = Modifier.size(18.dp))
                Text("新建项目", color = primary, fontSize = 14.sp)
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

// ===================== 项目列表单行 =====================

@Composable
fun ProjectListItem(
    category: CategoryEntity, isSelected: Boolean,
    onSelect: () -> Unit, onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(category.colorValue)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(category.name, color = Color.White, fontSize = 14.sp)
        }
        Box {
            Icon(
                Icons.Default.MoreVert, null, tint = Color(0xFF9E9E9E),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { menuExpanded = true })
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("删除项目", color = Color.Red)
                        }
                    },
                    onClick = { menuExpanded = false; onDelete() }
                )
            }
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

// ===================== 修复1：自定义左滑删除 =====================

@Composable
fun SwipeableTaskItem(
    task: TaskEntity,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit
) {
    val density = LocalDensity.current

    // 卡片宽度（px），用于计算最大红区（25%）
    var cardWidthPx by remember { mutableFloatStateOf(0f) }

    // 实时拖拽偏移（px，正值 = 已向左滑动的距离），最大 = cardWidthPx * 0.25
    var rawDragPx by remember { mutableFloatStateOf(0f) }

    // 是否处于"等待二次确认"状态
    var isPendingDelete by remember { mutableStateOf(false) }

    // 最新的 maxRedPx（防止 pointerInput 捕获旧值）
    val latestMaxRedPx = rememberUpdatedState(cardWidthPx * 0.25f)

    // 系统返回键 / 手势返回 取消 pending 状态
    BackHandler(enabled = isPendingDelete) {
        isPendingDelete = false
        rawDragPx = 0f
    }

    // 红区宽度动画：统一弹簧，避免 snap/spring 切换导致邻近卡片闪烁
    val animatedRedWidthPx by animateFloatAsState(
        targetValue = when {
            isPendingDelete -> latestMaxRedPx.value
            else -> rawDragPx.coerceAtLeast(0f)
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "red_zone_width"
    )

    val redZoneWidthDp = with(density) { animatedRedWidthPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.px()))
            .onSizeChanged { cardWidthPx = it.width.toFloat() }
            // 修复1：分两个阶段的手势处理，key = isPendingDelete，切换时重启手势检测
            .pointerInput(isPendingDelete) {
                if (!isPendingDelete) {
                    // ---- 正常状态：左滑显示红区 ----
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // 松手：若已滑超 30px 则进入 pending，否则弹回
                            if (rawDragPx > 30f) isPendingDelete = true
                            else rawDragPx = 0f
                        },
                        onDragCancel = { rawDragPx = 0f },
                        onHorizontalDrag = { _, delta ->
                            // delta < 0 = 向左滑
                            val maxRed = latestMaxRedPx.value
                            rawDragPx = (rawDragPx - delta).coerceIn(0f, maxRed)
                        }
                    )
                } else {
                    // ---- Pending 状态：右滑取消 ----
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, delta ->
                            // delta > 0 = 向右滑 → 取消
                            if (delta > 20f) {
                                isPendingDelete = false
                                rawDragPx = 0f
                            }
                        }
                    )
                }
            }
    ) {
        val noOp: () -> Unit = {}
        val safeToggle: () -> Unit = if (!isPendingDelete) onToggleComplete else noOp
        val safeLongClick: () -> Unit = if (!isPendingDelete) onEdit else noOp
        AnimatedTaskItem(
            task = task,
            onToggleComplete = safeToggle,
            onLongClick = safeLongClick
        )

        // 红区：叠加在任务右侧，宽度跟随拖拽
        if (animatedRedWidthPx > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(redZoneWidthDp)
                    // 只有 pending 状态下红区可点击（二次确认删除）
                    .then(
                        if (isPendingDelete) Modifier.clickable { onDelete() }
                        else Modifier
                    )
                    .background(
                        // pending 时纯红，拖拽中半透明红
                        if (isPendingDelete) Color(0xFFFF3B30)
                        else Color(0xFFFF3B30).copy(alpha = 0.75f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 只在 pending 状态且红区足够宽时显示删除图标
                if (isPendingDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "点击确认删除",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ===================== 修复3：完成动画（缩小 + 弹簧恢复） =====================

@Composable
fun AnimatedTaskItem(
    task: TaskEntity,
    onToggleComplete: () -> Unit,
    onLongClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    val verticalPadding by animateDpAsState(
        targetValue = if (task.isCompleted) 5.dp else 14.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "v_padding"
    )
    val cardBg by animateColorAsState(
        targetValue = if (task.isCompleted) Color(0xFFF0F0F0) else Color.White,
        label = "card_bg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg)
            .combinedClickable(onClick = onToggleComplete, onLongClick = onLongClick)
            .padding(horizontal = 14.dp, vertical = verticalPadding)
    ) {
        if (task.isCompleted) {
            // 完成状态：紧凑一行（约原来1/3高度）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, null, tint = primary, modifier = Modifier.size(13.dp))
                    Text(
                        text = task.title,
                        fontSize = 12.sp, color = Color(0xFF999999),
                        textDecoration = TextDecoration.LineThrough,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(task.date, fontSize = 11.sp, color = Color(0xFFBBBBBB))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFD6EAFF))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Done", fontSize = 10.sp, color = primary) }
                }
            }
        } else {
            // 完整状态
            FullTaskItemContent(task = task)
        }
    }
}

@Composable
fun FullTaskItemContent(task: TaskEntity) {
    val primary = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(8.px())) {
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
fun TaskList(
    tasks: List<TaskEntity>,
    onDeleteTask: (TaskEntity) -> Unit,
    onToggleComplete: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit
) {
    // 按时间排序（"HH:mm" 字符串直接字典序比较即可）
    val sortedTasks = remember(tasks) {
        tasks.sortedBy { it.date }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.px())
            .padding(top = 28.px()),
        verticalArrangement = Arrangement.spacedBy(12.px())
    ) {
        if (sortedTasks.isEmpty()) {
            Text(
                "暂无任务，点击 + 新增吧！",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp),
                color = Color.Gray
            )
        } else {
            for (task in sortedTasks) {
                key(task.id) {
                    SwipeableTaskItem(
                        task = task,
                        onDelete = { onDeleteTask(task) },
                        onToggleComplete = { onToggleComplete(task) },
                        onEdit = { onEditTask(task) }
                    )
                }
            }
        }
    }
}

// ===================== 顶部栏 =====================

@Composable
fun TodoListTopBar() {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.px()),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            Icon(
                painterResource(R.mipmap.left),
                null,
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.px())
            )
        },
        title = {
            Text(
                "今日任务",
                fontSize = 19.textPx(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
        },
        actions = {
            Image(painterResource(R.mipmap.notification), null, modifier = Modifier.size(24.px()))
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
            for (i in -2..2) {
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
            .padding(top = 32.px())
            .fillMaxWidth()
            .padding(horizontal = 4.px()),
        horizontalArrangement = Arrangement.spacedBy(12.px()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dates.forEach { date ->
            val isSelected = date.localDate.toEpochDay() == selectedDate
            val textColor = if (isSelected) Color.White else Color(0xFF333333)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(15.px()))
                    .background(if (isSelected) primary else Color.White)
                    .clickable { viewModel.setSelectedDate(date.localDate) }
                    .padding(vertical = 8.px(), horizontal = 20.px()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.px())
            ) {
                Text(date.month, fontSize = 11.textPx(), color = textColor)
                Text(
                    date.dayOfMonth,
                    fontSize = 19.textPx(),
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(date.dayOfWeek, fontSize = 11.textPx(), color = textColor)
            }
        }
    }
}

// ===================== 分类筛选行 =====================

val CustomShape = GenericShape { size, _ ->
    val (w, h) = size.width to size.height
    moveTo(0f, h / 2); cubicTo(0f, h / 8, 0f, 0f, w / 2, 0f); cubicTo(w, 0f, w, h / 8, w, h / 2)
    cubicTo(w, h - h / 8, w, h, w / 2, h); cubicTo(0f, h, 0f, h - h / 8, 0f, h / 2); close()
}

@Composable
fun CategoryRow(viewModel: TaskViewModel, selectedCategory: String) {
    val categories = remember { listOf("All", "To-do", "In Progress", "Done") }
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .padding(top = 32.px())
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.px())
    ) {
        Spacer(Modifier.width(22.px()))
        categories.fastForEachIndexed { _, category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(34.px())
                    .clip(CustomShape)
                    .background(if (isSelected) primary else Color(0xFFDDEEFF))
                    .clickable { viewModel.setSelectedCategory(category) }
                    .padding(vertical = 8.px(), horizontal = 24.px()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    category,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else primary
                )
            }
        }
        Spacer(Modifier.width(22.px()))
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
    lineTo(w - h / 3, 0f); quadraticTo(w, 0f, w, h / 3f); lineTo(w, h); lineTo(0f, h); close()
}

@Composable
fun TodoListBottomBar(onAddClick: () -> Unit = {}) {
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
                                if (currentIndex == index) 8.dp else 0.dp, CircleShape,
                                ambientColor = primary, spotColor = primary
                            )
                            .size(24.px())
                    )
                }
            }
        }
        Surface(
            onClick = onAddClick,
            modifier = Modifier
                .offset(0.dp, (-32).px())
                .align(Alignment.Center)
                .size(44.px()),
            shape = CircleShape, shadowElevation = 8.dp, color = primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(painterResource(list[2]), null, modifier = Modifier.size(24.px()))
            }
        }
    }
}