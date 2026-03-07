package com.zero.todo_list001.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao // 定义抽象方法获取DAO实例

    companion object {
        @Volatile // 保证所有线程对该变量的可见性
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // 如果Instance不为空，则返回Instance；如果为空，则创建一个新的数据库实例
            return Instance ?: synchronized(this) { // 线程安全地创建实例
                Room.databaseBuilder(context, AppDatabase::class.java, "todo_database")
                    .fallbackToDestructiveMigration() // 简化数据库升级策略 (开发时常用，生产环境需谨慎)
                    .build()
                    .also { Instance = it } // 将新创建的实例赋值给Instance
            }
        }
    }
}