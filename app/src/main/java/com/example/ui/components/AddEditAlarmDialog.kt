package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AlarmItem
import com.example.data.model.AlarmySoundCatalog
import com.example.data.model.MissionDifficulty
import com.example.data.model.MissionType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmDialog(
    alarm: AlarmItem?,
    onDismiss: () -> Unit,
    onSave: (AlarmItem) -> Unit
) {
    val initial = alarm ?: AlarmItem(
        hour = 6,
        minute = 30,
        label = "Morning Study Session",
        missionType = MissionType.MATH,
        missionTargetCount = 3,
        missionDifficulty = MissionDifficulty.MEDIUM,
        ringtoneName = "End of the World",
        isWakeUpCheckEnabled = true
    )

    var hour by remember { mutableStateOf(initial.hour) }
    var minute by remember { mutableStateOf(initial.minute) }
    var label by remember { mutableStateOf(initial.label) }
    var missionType by remember { mutableStateOf(initial.missionType) }
    var targetCount by remember { mutableStateOf(initial.missionTargetCount) }
    var difficulty by remember { mutableStateOf(initial.missionDifficulty) }
    var ringtoneName by remember { mutableStateOf(initial.ringtoneName) }
    var wakeUpCheck by remember { mutableStateOf(initial.isWakeUpCheckEnabled) }
    var volumeButtonLock by remember { mutableStateOf(initial.isVolumeButtonLockEnabled) }
    var repeatDays by remember { mutableStateOf(initial.repeatDays) }

    val allRingtones = remember { AlarmySoundCatalog.getAlarmRingtones() }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(26.dp),
            color = AlarmySurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AlarmyCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (alarm == null) "New Niqu ንቁ Mission" else "Edit Alarm",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AlarmyTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Time Selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ALARM TIME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmyTextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Hour dial buttons
                                CounterButton(label = "-", onClick = { hour = (hour + 23) % 24 })
                                Spacer(modifier = Modifier.width(12.dp))
                                val amPmHour = when {
                                    hour == 0 -> 12
                                    hour > 12 -> hour - 12
                                    else -> hour
                                }
                                Text(
                                    text = String.format("%02d : %02d", amPmHour, minute),
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 42.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hour < 12) "AM" else "PM",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AlarmyRed
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                CounterButton(label = "+", onClick = { hour = (hour + 1) % 24 })
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { minute = (minute + 5) % 60 },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlarmySurface)
                                ) { Text("+5 min", style = MaterialTheme.typography.labelSmall) }
                                Button(
                                    onClick = { minute = 0 },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlarmySurface)
                                ) { Text("00 min", style = MaterialTheme.typography.labelSmall) }
                                Button(
                                    onClick = { minute = 30 },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlarmySurface)
                                ) { Text("30 min", style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    }

                    // Repeat Days Pill selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "REPEAT DAYS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmyTextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val currentDays = repeatDays.split(",").map { it.trim() }
                                daysOfWeek.forEach { day ->
                                    val isSelected = currentDays.contains(day)
                                    Surface(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clickable {
                                                val newList = if (isSelected) {
                                                    currentDays - day
                                                } else {
                                                    currentDays + day
                                                }
                                                repeatDays = newList.filter { it.isNotEmpty() }.joinToString(",")
                                            },
                                        shape = CircleShape,
                                        color = if (isSelected) AlarmyRed else AlarmySurface
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = day.take(1),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else AlarmyTextSecondary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Label Input
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Alarm Label / Study Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AlarmyRed
                        )
                    )

                    // Mission Type Selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "WAKE UP MISSION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmyTextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(MissionType.values()) { mType ->
                                    val isSelected = mType == missionType
                                    Surface(
                                        modifier = Modifier.clickable {
                                            missionType = mType
                                            targetCount = mType.defaultTarget
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) AlarmyRed else AlarmySurface
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = mType.getIcon(),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = mType.title,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            if (missionType != MissionType.NORMAL) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Target count: $targetCount",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CounterButton(label = "-") {
                                            if (targetCount > 1) targetCount--
                                        }
                                        CounterButton(label = "+") {
                                            if (targetCount < 30) targetCount++
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Ringtone Selector from scraped alar.my catalog
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "RINGTONE (ALAR.MY CATALOG)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AlarmyTextSecondary
                                    )
                                )
                                Text(
                                    text = ringtoneName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AlarmyBlue
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(allRingtones) { sound ->
                                    val isSelected = sound.title == ringtoneName
                                    Surface(
                                        modifier = Modifier.clickable { ringtoneName = sound.title },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) AlarmyBlue else AlarmySurface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (sound.isLoud) AlarmyRed else Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = sound.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Wake Up Check Toggle
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Wake Up Check",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Confirms you're awake 5 mins after alarm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AlarmyTextSecondary
                                )
                            }
                            Switch(
                                checked = wakeUpCheck,
                                onCheckedChange = { wakeUpCheck = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AlarmyRed
                                )
                            )
                        }
                    }

                    // Volume Button Lock Toggle
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = AlarmyRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Volume Button Lock",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                                Text(
                                    text = "Volume keys do nothing while ringing - only the mission stops it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AlarmyTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = volumeButtonLock,
                                onCheckedChange = { volumeButtonLock = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AlarmyRed
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            val updated = initial.copy(
                                hour = hour,
                                minute = minute,
                                label = label.ifBlank { "Study Session" },
                                missionType = missionType,
                                missionTargetCount = targetCount,
                                missionDifficulty = difficulty,
                                ringtoneName = ringtoneName,
                                isWakeUpCheckEnabled = wakeUpCheck,
                                isVolumeButtonLockEnabled = volumeButtonLock,
                                repeatDays = repeatDays.ifBlank { "Everyday" }
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AlarmyRed),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Save Mission")
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterButton(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(38.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = AlarmySurface
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}
