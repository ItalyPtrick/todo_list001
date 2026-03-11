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
