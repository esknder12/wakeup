package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmItem
import com.example.data.model.MissionType
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AlarmMissionScreen(
    alarm: AlarmItem,
    onDismiss: () -> Unit
) {
    var isMissionCompleted by remember { mutableStateOf(false) }
    var showWakeUpCheck by remember { mutableStateOf(false) }
    var wakeUpCountdown by remember { mutableStateOf(98) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (showWakeUpCheck) {
        // Wake Up Check modal screen
        WakeUpCheckDialog(
            countdown = wakeUpCountdown,
            onConfirmAwake = onDismiss
        )
        LaunchedEffect(Unit) {
            while (wakeUpCountdown > 0) {
                delay(1000)
                wakeUpCountdown--
            }
            onDismiss()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlarmyBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Ringing header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.scale(pulseScale)
        ) {
            Icon(
                imageVector = Icons.Default.AlarmOn,
                contentDescription = null,
                tint = AlarmyRed,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ALARM RINGING • NO SNOOZE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = AlarmyRed,
                    letterSpacing = 1.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = alarm.label,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(
            text = "Ringtone: ${alarm.ringtoneName}",
            style = MaterialTheme.typography.bodySmall,
            color = AlarmyBlue
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mission Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = AlarmySurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, AlarmyRed.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                when (alarm.missionType) {
                    MissionType.MATH -> MathMissionView(
                        targetCount = alarm.missionTargetCount,
                        onCompleted = {
                            if (alarm.isWakeUpCheckEnabled) showWakeUpCheck = true
                            else onDismiss()
                        }
                    )
                    MissionType.SHAKE -> ShakeMissionView(
                        targetShakes = alarm.missionTargetCount,
                        onCompleted = {
                            if (alarm.isWakeUpCheckEnabled) showWakeUpCheck = true
                            else onDismiss()
                        }
                    )
                    MissionType.MEMORY -> MemoryMissionView(
                        onCompleted = {
                            if (alarm.isWakeUpCheckEnabled) showWakeUpCheck = true
                            else onDismiss()
                        }
                    )
                    MissionType.QUOTE -> QuoteMissionView(
                        onCompleted = {
                            if (alarm.isWakeUpCheckEnabled) showWakeUpCheck = true
                            else onDismiss()
                        }
                    )
                    MissionType.NORMAL -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = AlarmyRed,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    if (alarm.isWakeUpCheckEnabled) showWakeUpCheck = true
                                    else onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AlarmyRed),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                Text(
                                    text = "DISMISS ALARM",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MathMissionView(
    targetCount: Int,
    onCompleted: () -> Unit
) {
    var solvedCount by remember { mutableStateOf(0) }
    var operand1 by remember { mutableStateOf(73) }
    var operand2 by remember { mutableStateOf(18) }
    var isAddition by remember { mutableStateOf(true) }
    var userInput by remember { mutableStateOf("") }
    var errorShake by remember { mutableStateOf(false) }

    fun generateNewProblem() {
        operand1 = Random.nextInt(25, 89)
        operand2 = Random.nextInt(11, 49)
        isAddition = Random.nextBoolean()
        userInput = ""
    }

    val correctAnswer = if (isAddition) operand1 + operand2 else operand1 + operand2

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MATH MISSION",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = AlarmyRed
            )
            Text(
                text = "${solvedCount} / $targetCount solved",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Equation display
        Surface(
            color = AlarmyCard,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$operand1 + $operand2",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 42.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "= ${if (userInput.isEmpty()) "?" else userInput}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = AlarmyRed
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Keypad
        val keys = listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "C", "0", "OK")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(keys.size) { index ->
                val key = keys[index]
                Button(
                    onClick = {
                        when (key) {
                            "C" -> userInput = ""
                            "OK" -> {
                                if (userInput == correctAnswer.toString()) {
                                    solvedCount++
                                    if (solvedCount >= targetCount) {
                                        onCompleted()
                                    } else {
                                        generateNewProblem()
                                    }
                                } else {
                                    userInput = ""
                                }
                            }
                            else -> {
                                if (userInput.length < 4) userInput += key
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (key) {
                            "OK" -> AlarmyRed
                            "C" -> AlarmyCardBorder
                            else -> AlarmyCard
                        }
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ShakeMissionView(
    targetShakes: Int,
    onCompleted: () -> Unit
) {
    var shakeCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SHAKE MISSION",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = AlarmyRed
        )
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "$shakeCount / $targetShakes",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 56.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Shake your device vigorously or tap the button below to get out of bed!",
            style = MaterialTheme.typography.bodyMedium,
            color = AlarmyTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Big interactive Shake Button
        Surface(
            modifier = Modifier
                .size(160.dp)
                .clickable {
                    shakeCount++
                    if (shakeCount >= targetShakes) {
                        onCompleted()
                    }
                },
            shape = CircleShape,
            color = AlarmyRed
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "SHAKE / TAP",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun MemoryMissionView(
    onCompleted: () -> Unit
) {
    val pattern = remember { listOf(0, 4, 8, 2) }
    var userClicks by remember { mutableStateOf<List<Int>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MEMORY GRID MISSION",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = AlarmyBlue
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the blue highlighted tiles in order!",
            style = MaterialTheme.typography.bodySmall,
            color = AlarmyTextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            items(9) { index ->
                val isTarget = pattern.contains(index)
                val isSelected = userClicks.contains(index)
                Surface(
                    modifier = Modifier
                        .height(80.dp)
                        .clickable {
                            if (!userClicks.contains(index)) {
                                val next = userClicks + index
                                userClicks = next
                                if (userClicks.size == pattern.size) {
                                    onCompleted()
                                }
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) AlarmyBlue else if (isTarget) AlarmyBlue.copy(alpha = 0.4f) else AlarmyCard
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteMissionView(
    onCompleted: () -> Unit
) {
    val quote = "Your time is limited, so don't waste it living someone else's life."
    var typed by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MOTIVATION QUOTE MISSION",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = AlarmyRed
        )
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = AlarmyCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = typed,
            onValueChange = { typed = it },
            placeholder = { Text("Type the quote above...") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCompleted,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AlarmyRed),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("I Am Ready to Conquer the Day")
        }
    }
}

@Composable
fun WakeUpCheckDialog(
    countdown: Int,
    onConfirmAwake: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD9000000))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wake Up Check",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D2A44)
                        )
                    )
                    Surface(
                        color = Color(0xFFE0F7FA),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "In Progress",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0097A7),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "$countdown",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1D2A44),
                        fontSize = 64.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tap the Button to Confirm\nYou're Awake",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF546E7A),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onConfirmAwake,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = "Yes, I am up!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
