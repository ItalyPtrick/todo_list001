@file:OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)

package com.zero.todo_list001.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.todo_list001.R
import com.zero.todo_list001.ui.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
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