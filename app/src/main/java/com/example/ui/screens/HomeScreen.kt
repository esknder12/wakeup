package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AlarmItem
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    alarms: List<AlarmItem>,
    nextAlarmText: String,
    onAddAlarmClick: () -> Unit,
    onEditAlarmClick: (AlarmItem) -> Unit,
    onToggleAlarm: (AlarmItem) -> Unit,
    onTestMissionClick: (AlarmItem) -> Unit,
    onOpenTopperRoutine: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlarmyBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Brand wordmark
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Image(
                    painter = painterResource(id = R.drawable.nequ_wordmark),
                    contentDescription = "NEQU",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .height(28.dp)
                )
            }

            // Next Alarm Header Pill
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = AlarmyCard,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlarmyCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = AlarmyRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = nextAlarmText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Topper Routine quick pill
                    Surface(
                        onClick = onOpenTopperRoutine,
                        color = AlarmyRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlarmyRed.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AlarmyRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PW / Allen Routine",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmyRed
                                )
                            )
                        }
                    }
                }
            }

            // Hero Banner: "No Misses, No Snooze, No Oversleep. NEQU Prevails."
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onOpenTopperRoutine() },
                    colors = CardDefaults.cardColors(containerColor = AlarmySurface),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = AlarmyRed,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "NO SNOOZE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "India's #1 Study Clock",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AlarmyTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Misses. No Snooze.\nNo Oversleep.",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        lineHeight = 24.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "NEQU Prevails with Math & Shake missions.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AlarmyTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            // Hero graphic thumbnail
                            Image(
                                painter = painterResource(id = R.drawable.morning_study_hero_1785481407502),
                                contentDescription = "Morning study routine",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Alarms List Section Header
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR ALARMS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AlarmyTextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "${alarms.count { it.isEnabled }} active",
                        style = MaterialTheme.typography.labelMedium,
                        color = AlarmyRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Alarms items
            if (alarms.isEmpty()) {
                item {
                    EmptyAlarmsView(onAddAlarmClick)
                }
            } else {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmCard(
                        alarm = alarm,
                        onCardClick = { onEditAlarmClick(alarm) },
                        onToggle = { onToggleAlarm(alarm) },
                        onTestMission = { onTestMissionClick(alarm) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmItem,
    onCardClick: () -> Unit,
    onToggle: () -> Unit,
    onTestMission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled) AlarmyCard else AlarmyCard.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (alarm.isEnabled) AlarmyCardBorder else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Time and Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = alarm.getFormattedTime(),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (alarm.isEnabled) Color.White else AlarmyTextSecondary,
                                fontSize = 34.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${alarm.label} • ${alarm.getRepeatSubtitle()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (alarm.isEnabled) AlarmyTextSecondary else AlarmyTextSecondary.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AlarmyRed,
                        uncheckedThumbColor = AlarmyTextSecondary,
                        uncheckedTrackColor = AlarmySurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mission Badge, Ringtone chip, and Test Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Mission pill
                    Surface(
                        color = AlarmySurface,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = alarm.missionType.getIcon(),
                                contentDescription = null,
                                tint = AlarmyRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${alarm.missionType.title} (${alarm.missionTargetCount})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // Ringtone chip
                    Surface(
                        color = AlarmyBlue.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = AlarmyBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = alarm.ringtoneName,
                                style = MaterialTheme.typography.labelSmall,
                                color = AlarmyBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Test Mission button
                Button(
                    onClick = onTestMission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlarmyRed.copy(alpha = 0.15f),
                        contentColor = AlarmyRed
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Test Mission",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun EmptyAlarmsView(onAddAlarmClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AlarmOff,
            contentDescription = null,
            tint = AlarmyTextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No alarms created yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tap + to set a mission alarm or start your PW / Allen study routine",
            style = MaterialTheme.typography.bodySmall,
            color = AlarmyTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddAlarmClick,
            colors = ButtonDefaults.buttonColors(containerColor = AlarmyRed)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Your First Alarm")
        }
    }
}
