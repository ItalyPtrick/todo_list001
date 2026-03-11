@file:OptIn(ExperimentalMaterial3Api::class)

package com.zero.todo_list001.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.todo_list001.R
import com.zero.todo_list001.data.CategoryEntity
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.ui.TaskViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import com.zero.todo_list001.ui.components.WheelPicker

// ===================== 状态枚举 =====================

private enum class DialogView {
    MAIN, TIME_PICKER, STATUS_PICKER, ADD_PROJECT, DELETE_PROJECT_CONFIRM
}

// ===================== 任务表单对话框（新增/编辑两用） =====================

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