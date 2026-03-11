@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.zero.todo_list001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zero.todo_list001.data.AppDatabase
import com.zero.todo_list001.data.TaskRepository
import com.zero.todo_list001.ui.TaskViewModelFactory
import com.zero.todo_list001.ui.theme.Todo_list001Theme
import com.zero.todo_list001.ui.screens.TodoListScreen

// ===================== Activity =====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Todo_list001Theme {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = TaskRepository(database.taskDao(), database.categoryDao())
                val factory = TaskViewModelFactory(repository)
                TodoListScreen(viewModel = viewModel(factory = factory))
            }
        }
    }
}
