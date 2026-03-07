package com.zero.todo_list001.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 科技感配色方案 - 深灰 + 冷蓝（手机不支持莫奈取色时使用）
private val TechColdBlueScheme = darkColorScheme(
    primary          = Color(0xFF2196F3),   // 冷蓝主色
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF1A3A5C),
    onPrimaryContainer = Color(0xFFBBDDFF),
    secondary        = Color(0xFF03DAC6),   // 青色点缀
    onSecondary      = Color.Black,
    background       = Color(0xFF121218),
    onBackground     = Color(0xFFE8E8F0),
    surface          = Color(0xFF1E1E28),
    onSurface        = Color(0xFFE8E8F0),
    surfaceVariant   = Color(0xFF252535),
    onSurfaceVariant = Color(0xFFBBBBCC),
    outline          = Color(0xFF44445A),
    error            = Color(0xFFFF5252),
    onError          = Color.White,
)

@Composable
fun Todo_list001Theme(
    darkTheme: Boolean = true,     // 默认深色，科技感
    dynamicColor: Boolean = true,  // Android 12+ 莫奈取色
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> TechColdBlueScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}