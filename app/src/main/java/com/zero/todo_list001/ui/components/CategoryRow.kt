package com.zero.todo_list001.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zero.todo_list001.ui.TaskViewModel

// 特殊形状定义
val CustomShape = GenericShape { size, _ ->
    val (w, h) = size.width to size.height
    moveTo(0f, h / 2); cubicTo(0f, h / 8, 0f, 0f, w / 2, 0f); cubicTo(w, 0f, w, h / 8, w, h / 2)
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