@file:OptIn(ExperimentalMaterial3Api::class)

package com.zero.todo_list001.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zero.todo_list001.R
import com.zero.todo_list001.ui.TaskViewModel
import com.zero.todo_list001.ui.util.px

// ===================== 顶部栏 =====================

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

// ===================== 多选模式顶部栏 =====================

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

// ===================== 底部导航栏及形状 =====================

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