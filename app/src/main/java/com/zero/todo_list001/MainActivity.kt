package com.zero.todo_list001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zero.todo_list001.data.AppDatabase
import com.zero.todo_list001.data.TaskEntity
import com.zero.todo_list001.data.TaskRepository
import com.zero.todo_list001.ui.TaskViewModel
import com.zero.todo_list001.ui.TaskViewModelFactory
import com.zero.todo_list001.ui.theme.Todo_list001Theme
import com.zero.todo_list001.ui.util.px
import com.zero.todo_list001.ui.util.textPx
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todo_list001Theme {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = TaskRepository(database.taskDao())
                val viewModelFactory = TaskViewModelFactory(repository)
                
                TodoListScreen(viewModel = viewModel(factory = viewModelFactory))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTodoListScreen() {
    Todo_list001Theme {
        val database = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
        val repository = TaskRepository(database.taskDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        TodoListScreen(viewModel = viewModel(factory = viewModelFactory))
    }
}

@Composable
fun TodoListScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.mipmap.bg),
                contentScale = ContentScale.Crop
            ),
        containerColor = Color.Transparent,
        topBar = {
            TodoListTopBar()
        },
        bottomBar = {
            TodoListBottomBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DateRow(viewModel = viewModel, selectedDate = selectedDate)
            CategoryRow(viewModel = viewModel, selectedCategory = selectedCategory)
            TaskList(tasks = tasks)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListTopBar() {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.px()),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            Icon(
                painter = painterResource(R.mipmap.left),
                contentDescription = null,
                modifier = Modifier.size(24.px())
            )
        },
        title = {
            Text(
                text = "今日任务",
                fontSize = 19.textPx(),
                fontWeight = FontWeight.SemiBold
            )
        },
        actions = {
            Image(
                painter = painterResource(R.mipmap.notification),
                contentDescription = null,
                modifier = Modifier.size(24.px())
            )
        }
    )
}

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
            val local = Locale.getDefault()
            for (i in -2..2) {
                val day = today.plusDays(i.toLong())
                val dayInfo = DayInfo(
                    month = day.month.getDisplayName(TextStyle.SHORT, local),
                    dayOfMonth = String.format(local, "%02d", day.dayOfMonth),
                    dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.SHORT, local),
                    isToday = day.equals(today),
                    localDate = day
                )
                add(dayInfo)
            }
        }
    }

@Composable
fun DateRow(viewModel: TaskViewModel, selectedDate: Long) {
    val dates = dayInfoList

    Row(
        modifier = Modifier
            .padding(top = 32.px())
            .fillMaxWidth()
            .padding(horizontal = 4.px()),
        horizontalArrangement = Arrangement.spacedBy(12.px()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dates.fastForEach { date ->
            DateItem(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(15.px()))
                    .background(
                        color = if (date.localDate.toEpochDay() == selectedDate) Color(0xFF5F33E1) else Color.White
                    )
                    .clickable {
                        viewModel.setSelectedDate(date.localDate)
                    }
                    .padding(vertical = 8.px(), horizontal = 20.px()),
                dayInfo = date
            )
        }
    }
}

@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    dayInfo: DayInfo
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.px())
    ) {
        Text(
            text = dayInfo.month,
            fontSize = 11.textPx(),
            color = if (dayInfo.isToday) Color.White else Color.Black
        )
        Text(
            text = dayInfo.dayOfMonth,
            fontSize = 19.textPx(),
            fontWeight = FontWeight.SemiBold,
            color = if (dayInfo.isToday) Color.White else Color.Black
        )
        Text(
            text = dayInfo.dayOfWeek,
            fontSize = 11.textPx(),
            color = if (dayInfo.isToday) Color.White else Color.Black
        )
    }
}

// 自定义形状
val CustomShape = GenericShape { size, _ ->
    // width / 22 height / 8
    val (width, height) = size.width to size.height
    moveTo(0f, height / 2) // 移动到左边垂直中心
    // 左上角
    cubicTo(
        0f, height / 8, // 第一个控制点
        0f, 0f, // 第二个控制点
        width / 2, 0f // 终点
    )
    // 其他同理
    // 右上角
    cubicTo(
        width, 0f, // 第一个控制点
        width, height / 8, // 第二个控制点
        width, height / 2 // 终点
    )
    // 右下角
    cubicTo(
        width, height - height / 8, // 第一个控制点
        width, height, // 第二个控制点
        width / 2, height // 终点
    )
    // 左下角
    cubicTo(
        0f, height, // 第一个控制点
        0f, height - height / 8, // 第二个控制点
        0f, height / 2 // 终点
    )
    close()
}

@Composable
fun CategoryRow(viewModel: TaskViewModel, selectedCategory: String) {
    val categories = remember {
        listOf("All", "To do", "In progress", "Completed")
    }
    val currentIndex = categories.indexOf(selectedCategory)

    Row(
        modifier = Modifier
            .padding(top = 32.px())
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.px())
    ) {
        Spacer(Modifier.width(22.px()))
        categories.fastForEachIndexed { index, category ->
            val isSelected = category == selectedCategory
            CategoryItem(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(34.px())
                    .clip(CustomShape)
                    .background(
                        color = if (isSelected) Color(0xFF5F33E1) else Color(0xFFEDE8FF)
                    )
                    .clickable {
                        viewModel.setSelectedCategory(category)
                    }
                    .padding(vertical = 8.px(), horizontal = 24.px()),
                category = category,
                isSelected = isSelected
            )
        }
        Spacer(Modifier.width(22.px()))
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    category: String,
    isSelected: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF5F33E1)
        )
    }
}

@Composable
fun TaskList(tasks: List<TaskEntity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.px())
            .padding(top = 28.px()),
        verticalArrangement = Arrangement.spacedBy(16.px())
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = "No tasks for this day/category.",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 32.dp),
                color = Color.Gray
            )
        } else {
            tasks.fastForEach { task ->
                TaskItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.px()))
                        .background(Color.White)
                        .padding(16.px()),
                    task = task
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: TaskEntity
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.px())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.category,
                fontSize = 11.textPx(),
                color = Color(0xFF6E6A7C)
            )

            Box(
                modifier = Modifier
                    .size(24.px())
                    .clip(RoundedCornerShape(7.px()))
                    .background(
                        color = when (task.iconResId) {
                            R.mipmap.briefcase -> Color(0xFFFFE4F2)
                            R.mipmap.user -> Color(0xFFEDE4FF)
                            R.mipmap.book -> Color(0xFFFFE6D4)
                            else -> Color.White
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(task.iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(14.px())
                )
            }

        }
        Text(
            text = task.title,
            fontSize = 14.textPx(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.px())
            ) {
                Image(
                    painter = painterResource(R.mipmap.time),
                    contentDescription = null,
                    modifier = Modifier.size(14.px())
                )

                Text(
                    text = task.date,
                    fontSize = 11.textPx(),
                    color = Color(0xFFAB94FF)
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = when (task.status) {
                            "Done" -> Color(0xFFEDE8FF)
                            "In Progress" -> Color(0xFFFFE9E1)
                            "To-do" -> Color(0xFFE3F2FF)
                            else -> Color.White
                        }
                    )
                    .padding(horizontal = 6.px(), vertical = 2.px()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.status,
                    fontSize = 9.textPx(),
                    color = when (task.status) {
                        "Done" -> Color(0xFF5F33E1)
                        "In Progress" -> Color(0xFFFF7D53)
                        "To-do" -> Color(0xFF0087FF)
                        else -> Color.White
                    }
                )
            }
        }
    }
}

val CustomShape2 = GenericShape { size, _ ->
    val (width, height) = size.width to size.height
    val floatWidth = width / 5f
    moveTo(0f, height)
    lineTo(0f, height / 3)
    // 左上角圆角
    quadraticTo(
        0f, 0f,
        height / 3f, 0f
    )
    lineTo(width * 2 / 5 - floatWidth / 8, 0f)
    cubicTo(
        width * 2 / 5 + floatWidth / 8, 0f,
        width * 2 / 5 + floatWidth / 8, height / 2,
        width / 2, height / 2
    )
    cubicTo(
        width * 3 / 5 - floatWidth / 8, height / 2,
        width * 3 / 5 - floatWidth / 8, 0f,
        width * 3 / 5 + floatWidth / 8, 0f
    )
    lineTo(width - height / 3, 0f)
    quadraticTo(
        width, 0f,
        width, height / 3f
    )
    lineTo(width, height)
    lineTo(0f, height)
    close()
}

@Composable
fun TodoListBottomBar() {
    val list = remember {
        listOf(
            R.mipmap.home,
            R.mipmap.calendar,
            R.mipmap.add,
            R.mipmap.doc,
            R.mipmap.profile,
        )
    }
    var currentIndex by remember { mutableIntStateOf(0) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.px())
                .clip(CustomShape2)
                .background(Color(0xFFEEE9FF))
                .navigationBarsPadding()
        ) {
            list.fastForEachIndexed { index, icon ->
                if (index == 2) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                } else {
                    BottomItem(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                currentIndex = index
                            },
                        icon = icon,
                        isSelected = currentIndex == index
                    )
                }
            }
        }

        Surface(
            onClick = {},
            modifier = Modifier
                .offset(0.dp, (-32).px())
                .align(Alignment.Center)
                .size(44.px()),
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = Color(0xFF5F33E1)
        ) {
            BottomItem(
//                modifier = Modifier
//                    .offset(0.dp, (-32).px())
//                    .align(Alignment.Center)
////                .shadow(
////                    elevation = 50.dp,
////                    shape = CircleShape,
////                    ambientColor = Color(0xFFc2b2f0),
//////                    spotColor = Color(0xFFc2b2f0)
////                )
//                    .size(44.px())
//                    .clip(CircleShape)
//                    .background(
//                        color = Color(0xFF5F33E1)
//                    ),
                icon = list[2]
            )
        }

    }

}

@Composable
fun BottomItem(
    modifier: Modifier = Modifier,
    icon: Int,
    isSelected: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .shadow(
                    elevation = if (isSelected) 8.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF5F33E1),
                    spotColor = Color(0xFF5F33E1)
                )
                .size(24.px())
                .background(Color.Transparent)
        )
    }
}