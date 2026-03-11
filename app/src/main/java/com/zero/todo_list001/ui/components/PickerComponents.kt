package com.zero.todo_list001.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import kotlin.math.abs

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
    val snapBehavior = rememberSnapFlingBehavior(listState)
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