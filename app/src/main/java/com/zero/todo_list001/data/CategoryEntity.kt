package com.zero.todo_list001.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val colorValue: Long // 存储颜色，如 0xFF5B8DEFL
)