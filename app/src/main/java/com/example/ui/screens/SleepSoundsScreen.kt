package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmySound
import com.example.data.model.AlarmySoundCatalog
import com.example.ui.theme.*

@Composable
fun SleepSoundsScreen(
    activeSound: AlarmySound?,
    isPlaying: Boolean,
    onSoundClick: (AlarmySound) -> Unit,
    onStopClick: () -> Unit
) {
    val sleepSounds = remember { AlarmySoundCatalog.getSleepSounds() }
    var bedtimeReminderEnabled by remember { mutableStateOf(true) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(AlarmyBackground),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Sleep Tracking Card: "Track Your Sleep & Detect Snoring"
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AlarmySurface),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlarmyCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = AlarmyBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Track Your Sleep & Detect Snoring",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Surface(
                            color = AlarmyBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "LAST NIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AlarmyBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Net sleep time",
                                style = MaterialTheme.typography.bodySmall,
                                color = AlarmyTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "7h 39m",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SleepStageLegend("Awake", Color(0xFF80DEEA), "5%")
                            SleepStageLegend("REM", Color(0xFF4DD0E1), "26%")
                            SleepStageLegend("Light", Color(0xFF5C6BC0), "45%")
                            SleepStageLegend("Deep", Color(0xFF3949AB), "24%")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated sleep stage timeline
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AlarmyCard)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.05f)
                                .fillMaxHeight()
                                .background(Color(0xFF80DEEA))
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.26f)
                                .fillMaxHeight()
                                .background(Color(0xFF4DD0E1))
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.45f)
                                .fillMaxHeight()
                                .background(Color(0xFF5C6BC0))
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.24f)
                                .fillMaxHeight()
                                .background(Color(0xFF3949AB))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Snoring analysis bar
                    Surface(
                        color = AlarmyCard,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = AlarmyRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Snore analysis",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AlarmyTextSecondary
                                    )
                                    Text(
                                        text = "less than 10 min (Low)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = "Play recording",
                                tint = AlarmyBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bedtime Reminder Notification Banner
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AlarmyCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlarmyCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Get Notified To Build Better Sleep Habits",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Bedtime reminder at 11:30 PM • Sleep analysis ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = AlarmyTextSecondary
                        )
                    }
                    Switch(
                        checked = bedtimeReminderEnabled,
                        onCheckedChange = { bedtimeReminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AlarmyBlue
                        )
                    )
                }
            }
        }

        // Section Title: "Discover Diverse Sleep Sounds"
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISCOVER DIVERSE SLEEP SOUNDS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AlarmyTextSecondary,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "${sleepSounds.size} Sounds",
                    style = MaterialTheme.typography.labelMedium,
                    color = AlarmyBlue
                )
            }
        }

        // Sleep Sounds Grid
        items(sleepSounds, key = { it.id }) { sound ->
            val isCurrentSoundPlaying = isPlaying && activeSound?.id == sound.id
            SleepSoundGridCard(
                sound = sound,
                isPlaying = isCurrentSoundPlaying,
                onClick = { onSoundClick(sound) }
            )
        }

        // Bottom padding for player bar
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SleepStageLegend(label: String, color: Color, percentage: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = AlarmyTextSecondary
            )
        }
        Text(
            text = percentage,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )
    }
}

@Composable
fun SleepSoundGridCard(
    sound: AlarmySound,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) AlarmyBlue.copy(alpha = 0.2f) else AlarmyCard
        ),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 2.dp else 1.dp,
            color = if (isPlaying) AlarmyBlue else AlarmyCardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = if (isPlaying) AlarmyBlue else AlarmySurface,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = if (isPlaying) Color.White else AlarmyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isPlaying) {
                    Surface(
                        color = AlarmyBlue,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PLAYING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = sound.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sound.description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AlarmyTextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 2
                )
            }
        }
    }
}
