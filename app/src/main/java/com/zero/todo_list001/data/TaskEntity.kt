package com.zero.todo_list001.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tasks") // 定义表名为 "tasks"
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) // 主键，自动生成
    val id: Int = 0, // 给每个任务一个唯一的ID
    val title: String,
    val category: String, // 例如 "To do", "In progress", "Completed"
    val status: String,   // 例如 "Done", "In Progress", "To-do"
    val date: String, // 存储为字符串，或者考虑用LocalDate等类型（需要TypeConverter）
    val iconResId: Int, // 存储R.mipmap.briefcase等资源ID
    val taskDate: Long = LocalDate.now().toEpochDay(), // 任务所属的日期，以Epoch Day存储，便于过滤
    val isCompleted: Boolean = false // 新增一个完成状态，方便UI显示和过滤
)

// 考虑到你的UI目前是直接使用 `Task` data class，你可以继续使用 `Task`，
// 但为了保持 Room Entity 的职责单一，我们可以定义一个独立的 Entity，
// 然后在 Repository 或 ViewModel 中进行 Entity 到 UI Model 的转换。
// 这样你的UI代码 `Task` data class 就不需要引入Room的注解。

// 为了简单起见，我们暂时直接使用 `Task` 这个名字作为 Entity，
// 但更规范的做法是定义 `TaskEntity` 和一个独立的 `Task` (用于UI)。
// 假设我们现在重命名并扩展你已有的 `Task` 为 `TaskEntity`。
// 如果你想保持 `Task` 不变，可以创建一个新的 `TaskEntity` 并进行转换。

// 为了避免修改太多现有UI代码，我们让 TaskEntity 与当前的 Task 保持字段一致，并增加 id 和 taskDate
// 删掉原来的 `Task` data class，使用这个新的 `TaskEntity`