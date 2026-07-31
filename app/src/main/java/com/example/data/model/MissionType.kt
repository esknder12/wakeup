package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.ui.graphics.vector.ImageVector

enum class MissionType(
    val title: String,
    val subtitle: String,
    val defaultTarget: Int
) {
    MATH(
        title = "Math Problems",
        subtitle = "Solve arithmetic equations to wake up your brain",
        defaultTarget = 3
    ),
    SHAKE(
        title = "Shake Phone",
        subtitle = "Shake your device vigorously to get moving",
        defaultTarget = 15
    ),
    MEMORY(
        title = "Memory Grid",
        subtitle = "Memorize & repeat the blue tile pattern",
        defaultTarget = 2
    ),
    QUOTE(
        title = "Motivation Quote",
        subtitle = "Type an inspiring quote to start with purpose",
        defaultTarget = 1
    ),
    NORMAL(
        title = "Normal Alarm",
        subtitle = "Basic slide or tap to dismiss",
        defaultTarget = 1
    );

    fun getIcon(): ImageVector = when (this) {
        MATH -> Icons.Default.Calculate
        SHAKE -> Icons.Default.Vibration
        MEMORY -> Icons.Default.GridOn
        QUOTE -> Icons.Default.FormatQuote
        NORMAL -> Icons.Default.Notifications
    }
}

enum class MissionDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}
