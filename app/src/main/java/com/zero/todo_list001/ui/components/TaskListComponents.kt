@file:OptIn(ExperimentalFoundationApi::class)

package com.zero.todo_list001.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.todo_list001.R
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.ui.util.px
import com.zero.todo_list001.ui.util.textPx

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
    onStartTask: (TaskEntity) -> Unit,
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
                        onStartTask = { onStartTask(task) },
                        onLongClick = { onLongPressTask(task) },
                        onToggleSelection = { onToggleSelection(task.id) }
                    )
                }
            }
        }
    }
}

// ===================== 滑动任务 =====================
private enum class RevealSide { NONE, EDIT, START }

@Composable
fun SwipeableTaskItem(
    task: TaskEntity,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onStartTask: () -> Unit,
    onLongClick: () -> Unit,
    onToggleSelection: () -> Unit
) {
    val density = LocalDensity.current
    var cardWidthPx by remember { mutableFloatStateOf(0f) }
    var isPendingEdit by remember { mutableStateOf(false) }
    var isPendingStart by remember { mutableStateOf(false) }
    val maxRevealPx = rememberUpdatedState(cardWidthPx * 0.25f)
    val directionThresholdPx = with(density) { 8.dp.toPx() } // 方向判定阈值8dp

    // 新状态机变量
    var revealSide by remember { mutableStateOf(RevealSide.NONE) }
    var revealPx by remember { mutableFloatStateOf(0f) } // 始终 >= 0
    var accumulatedDelta by remember { mutableFloatStateOf(0f) } // 仅用于NONE状态判定方向

    BackHandler(enabled = isPendingEdit || isPendingStart) {
        isPendingEdit = false
        isPendingStart = false
        revealSide = RevealSide.NONE
        revealPx = 0f
        accumulatedDelta = 0f
    }

    // 两个动画宽度变量，严格互斥
    val editZoneWidthPx by animateFloatAsState(
        targetValue = when {
            isPendingEdit -> maxRevealPx.value
            revealSide == RevealSide.EDIT -> revealPx.coerceIn(0f, maxRevealPx.value)
            else -> 0f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "edit_zone"
    )

    val startZoneWidthPx by animateFloatAsState(
        targetValue = when {
            isPendingStart -> maxRevealPx.value
            revealSide == RevealSide.START -> revealPx.coerceIn(0f, maxRevealPx.value)
            else -> 0f
        },
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "start_zone"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.px()))
            .onSizeChanged { cardWidthPx = it.width.toFloat() }
            .pointerInput(isPendingEdit, isPendingStart, isSelectionMode) {
                if (isSelectionMode) return@pointerInput
                if (!isPendingEdit && !isPendingStart) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            revealSide = RevealSide.NONE
                            revealPx = 0f
                            accumulatedDelta = 0f
                        },
                        onDragEnd = {
                            val autoOpenThreshold = maxRevealPx.value / 3 // iOS风格自动展开阈值
                            when (revealSide) {
                                RevealSide.EDIT -> { // 右滑显示左侧编辑蓝区
                                    if (revealPx > autoOpenThreshold) {
                                        isPendingEdit = true
                                    }
                                }
                                RevealSide.START -> { // 左滑显示右侧开始橙区
                                    if (revealPx > autoOpenThreshold) {
                                        isPendingStart = true
                                    }
                                }
                                RevealSide.NONE -> {}
                            }
                            // 手指抬起重置所有滑动状态
                            revealSide = RevealSide.NONE
                            revealPx = 0f
                            accumulatedDelta = 0f
                        },
                        onDragCancel = {
                            revealSide = RevealSide.NONE
                            revealPx = 0f
                            accumulatedDelta = 0f
                        },
                        onHorizontalDrag = { _, delta ->
                            when (revealSide) {
                                // 已锁定编辑模式（右滑，显示左侧蓝区）
                                RevealSide.EDIT -> {
                                    revealPx = if (delta > 0) {
                                        // 继续右滑：增加显示宽度
                                        (revealPx + delta).coerceAtMost(maxRevealPx.value)
                                    } else {
                                        // 向左回滑：减少显示宽度（收起）
                                        (revealPx + delta).coerceAtLeast(0f)
                                    }
                                }
                                // 已锁定开始模式（左滑，显示右侧橙区）
                                RevealSide.START -> {
                                    revealPx = if (delta < 0) {
                                        // 继续左滑：增加显示宽度
                                        (revealPx - delta).coerceAtMost(maxRevealPx.value)
                                    } else {
                                        // 向右回滑：减少显示宽度（收起）
                                        (revealPx - delta).coerceAtLeast(0f)
                                    }
                                }
                                // 未锁定，累计偏移判定方向
                                RevealSide.NONE -> {
                                    accumulatedDelta += delta
                                    when {
                                        // 累计右滑超过阈值，锁定编辑方向
                                        accumulatedDelta > directionThresholdPx -> {
                                            revealSide = RevealSide.EDIT
                                            revealPx = accumulatedDelta.coerceIn(0f, maxRevealPx.value)
                                            accumulatedDelta = 0f // 锁定后清零累计值
                                        }
                                        // 累计左滑超过阈值，锁定开始方向
                                        accumulatedDelta < -directionThresholdPx -> {
                                            revealSide = RevealSide.START
                                            revealPx = (-accumulatedDelta).coerceIn(0f, maxRevealPx.value)
                                            accumulatedDelta = 0f // 锁定后清零累计值
                                        }
                                        // 未超过阈值，继续累计
                                        else -> {
                                            // 不做操作，继续累计delta
                                        }
                                    }
                                }
                            }
                        }
                    )
                } else {
                    detectHorizontalDragGestures { _, delta ->
                        when {
                            // 编辑状态下向左滑动取消
                            isPendingEdit && delta < -20f -> {
                                isPendingEdit = false
                            }
                            // 开始状态下向右滑动取消
                            isPendingStart && delta > 20f -> {
                                isPendingStart = false
                            }
                        }
                    }
                }
            }
    ) {
        val noOp: () -> Unit = {}
        AnimatedTaskItem(
            task = task, isSelectionMode = isSelectionMode, isSelected = isSelected,
            onToggleComplete = if (!isPendingEdit && !isPendingStart) onToggleComplete else noOp,
            onLongClick = if (!isPendingEdit && !isPendingStart) onLongClick else noOp,
            onToggleSelection = onToggleSelection
        )

        // 编辑蓝区（左侧）
        if (editZoneWidthPx > 0f) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(with(density) { editZoneWidthPx.toDp() })
                        .then(if (isPendingEdit) Modifier.clickable {
                            isPendingEdit = false; onEdit()
                        } else Modifier)
                        .background(Color(0xFF4FC3F7)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconAlpha = (editZoneWidthPx / maxRevealPx.value).coerceIn(0f, 1f)
                    Icon(
                        Icons.Default.Edit,
                        "编辑",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(alpha = iconAlpha)
                    )
                }
            }
        }

        // 进行中橙区（右侧）
        if (startZoneWidthPx > 0f) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(with(density) { startZoneWidthPx.toDp() })
                        .then(if (isPendingStart) Modifier.clickable {
                            isPendingStart = false; onStartTask()
                        } else Modifier)
                        .background(Color(0xFFFF7D53)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconAlpha = (startZoneWidthPx / maxRevealPx.value).coerceIn(0f, 1f)
                    Icon(
                        Icons.Default.PlayArrow,
                        "开始任务",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(alpha = iconAlpha)
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

        // 多选模式勾选框 - 使用固定宽度避免布局测量开销
        Box(
            modifier = Modifier
                .width(if (isSelectionMode) 36.dp else 0.dp)
                .animateContentSize(
                    animationSpec = tween(350, easing = FastOutSlowInEasing),
                    finishedListener = { _, _ -> }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isSelectionMode) {
                // 勾选框Q弹切换：加入物理弹簧模型
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