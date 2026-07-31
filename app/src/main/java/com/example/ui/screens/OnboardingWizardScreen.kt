package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmItem
import com.example.data.model.AlarmySoundCatalog
import com.example.data.model.MissionDifficulty
import com.example.data.model.MissionType
import com.example.ui.theme.*

@Composable
fun OnboardingWizardScreen(
    onFinishWizard: (AlarmItem) -> Unit,
    onSkipToHome: () -> Unit
) {
    var wizardStep by remember { mutableStateOf(0) } // 0=Intro1, 1=Intro2, 2=Time(1/4), 3=Wallpaper(2/4), 4=Sound(3/4), 5=Mission(4/4)

    // Setup state
    var selectedHour by remember { mutableStateOf(7) }
    var selectedMinute by remember { mutableStateOf(0) }
    var isAm by remember { mutableStateOf(true) }
    var selectedWallpaper by remember { mutableStateOf("Gooooood Morningggggg") }
    var selectedSoundTitle by remember { mutableStateOf("Video sound • End of the World") }
    var volume by remember { mutableStateOf(0.95f) }
    var gentleWakeUp by remember { mutableStateOf(true) }
    var selectedMission by remember { mutableStateOf(MissionType.MATH) }

    val totalSetupSteps = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E17))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (wizardStep >= 2) {
                IconButton(onClick = { wizardStep-- }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            if (wizardStep >= 2) {
                // Progress indicator 1/4, 2/4, 3/4, 4/4
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val stepNum = wizardStep - 1
                    // Bar indicator
                    Row(
                        modifier = Modifier
                            .width(120.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF1D2A44))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(stepNum / 4f)
                                .background(Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "$stepNum/$totalSetupSteps",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AlarmyTextSecondary
                    )
                }
            }

            TextButton(onClick = onSkipToHome) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge.copy(color = AlarmyTextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (wizardStep) {
                0 -> IntroStepOneView()
                1 -> IntroStepTwoView()
                2 -> WizardStep1SetTimeView(
                    hour = selectedHour,
                    minute = selectedMinute,
                    isAm = isAm,
                    onHourChange = { selectedHour = it },
                    onMinuteChange = { selectedMinute = it },
                    onAmPmToggle = { isAm = it }
                )
                3 -> WizardStep2WallpaperView(
                    selectedWallpaper = selectedWallpaper,
                    onSelect = { selectedWallpaper = it }
                )
                4 -> WizardStep3SoundVolumeView(
                    selectedSoundTitle = selectedSoundTitle,
                    volume = volume,
                    gentleWakeUp = gentleWakeUp,
                    onSoundSelect = { selectedSoundTitle = it },
                    onVolumeChange = { volume = it },
                    onGentleToggle = { gentleWakeUp = it }
                )
                5 -> WizardStep4MissionView(
                    selectedMission = selectedMission,
                    onSelect = { selectedMission = it }
                )
            }
        }

        // Bottom page indicators and CTA Button
        Spacer(modifier = Modifier.height(12.dp))

        if (wizardStep < 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0..1) {
                    Box(
                        modifier = Modifier
                            .size(if (i == wizardStep) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (i == wizardStep) Color.White else Color(0xFF37474F))
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Button(
            onClick = {
                if (wizardStep < 5) {
                    wizardStep++
                } else {
                    // Finalize and create alarm
                    val finalHour24 = when {
                        isAm && selectedHour == 12 -> 0
                        !isAm && selectedHour < 12 -> selectedHour + 12
                        else -> selectedHour
                    }
                    val newAlarm = AlarmItem(
                        hour = finalHour24,
                        minute = selectedMinute,
                        label = "Wake Up • $selectedWallpaper",
                        isEnabled = true,
                        repeatDays = "Mon,Tue,Wed,Thu,Fri",
                        missionType = selectedMission,
                        missionTargetCount = selectedMission.defaultTarget,
                        missionDifficulty = MissionDifficulty.MEDIUM,
                        ringtoneName = selectedSoundTitle.substringAfter("• ").trim(),
                        isWakeUpCheckEnabled = true,
                        soundVolume = volume
                    )
                    onFinishWizard(newAlarm)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AlarmyRed),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (wizardStep == 0) "Next" else if (wizardStep == 1) "Get started" else if (wizardStep < 5) "Next" else "Save & Launch Alarmy",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun IntroStepOneView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy",
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "The most trusted\nalarm worldwide",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        BadgeCard(
            icon = Icons.Default.Public,
            title = "#1 Alarm App",
            subtitle = "in 97 countries"
        )
        Spacer(modifier = Modifier.height(16.dp))
        BadgeCard(
            icon = Icons.Default.Star,
            title = "4.8★",
            subtitle = "Rating"
        )
        Spacer(modifier = Modifier.height(16.dp))
        BadgeCard(
            icon = Icons.Default.Download,
            title = "100M+",
            subtitle = "Downloads"
        )
    }
}

@Composable
private fun IntroStepTwoView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "The only alarm listed in\nmedical journals",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Medical journal badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UniversityBadge("Harvard")
            UniversityBadge("Stanford")
            UniversityBadge("Oxford")
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Brain productivity diagram matching screenshot 2
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233050))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Morning productivity circle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1C2740),
                            modifier = Modifier.size(90.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E507A))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Morning\nProductivity",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AlarmyTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "2x",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AlarmyRed,
                        modifier = Modifier.size(70.dp)
                    )

                    // Goal achievement circle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1C2740),
                            modifier = Modifier.size(90.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E507A))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Goal\nAchievement",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AlarmyTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "+15%",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No more snoozing. Own your day with missions designed by neuroscientists.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AlarmyTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun WizardStep1SetTimeView(
    hour: Int,
    minute: Int,
    isAm: Boolean,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onAmPmToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Set your alarm time",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Time selector roller feel matching screenshot 3
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222F4D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Previous time faint
                val prevHour = if (hour == 1) 12 else hour - 1
                val nextHour = if (hour == 12) 1 else hour + 1
                val prevMin = if (minute == 0) 59 else minute - 1
                val nextMin = (minute + 1) % 60

                Text(
                    text = String.format("%02d   :   %02d", prevHour, prevMin),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = AlarmyTextSecondary.copy(alpha = 0.4f),
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active highlighted time row
                Surface(
                    color = Color(0xFF1D2638),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = String.format("%02d", hour),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 44.sp
                                ),
                                modifier = Modifier.clickable {
                                    val next = if (hour >= 12) 1 else hour + 1
                                    onHourChange(next)
                                }
                            )
                            Text(
                                text = "  :  ",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 38.sp
                                )
                            )
                            Text(
                                text = String.format("%02d", minute),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 44.sp
                                ),
                                modifier = Modifier.clickable {
                                    onMinuteChange((minute + 5) % 60)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // AM / PM toggle
                        Surface(
                            onClick = { onAmPmToggle(!isAm) },
                            color = AlarmyRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isAm) "a.m." else "p.m.",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AlarmyRed
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = String.format("%02d   :   %02d", nextHour, nextMin),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = AlarmyTextSecondary.copy(alpha = 0.4f),
                        fontSize = 24.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Tap numbers to adjust hour & minutes",
            style = MaterialTheme.typography.bodySmall,
            color = AlarmyTextSecondary
        )
    }
}

@Composable
private fun WizardStep2WallpaperView(
    selectedWallpaper: String,
    onSelect: (String) -> Unit
) {
    val trending = listOf(
        "Gooooood Morningggggg" to "🐹 Singing Guinea Pig",
        "Angelic Wake Up" to "🌟 Golden Dawn Capybara",
        "Kiwi Cat Alarm" to "🥝 Spinning Meme Cat",
        "Hamster Scream" to "🐹 Loud Wake Up"
    )
    val motivation = listOf(
        "Wake up you lazy" to "💪 Discipline Fist",
        "Stick to the plan" to "🔥 Zero Excuses Text",
        "Wake up now" to "📝 Note: You have 2 choices",
        "Into Space" to "🚀 Nebula Horizon"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose your alarm wallpaper",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Trending
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💖 Trending", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
        }
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(trending.size) { i ->
                val item = trending[i]
                WallpaperCard(
                    title = item.first,
                    subtitle = item.second,
                    isSelected = selectedWallpaper == item.first,
                    onClick = { onSelect(item.first) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Motivation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥 Daily Motivation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
        }
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(motivation.size) { i ->
                val item = motivation[i]
                WallpaperCard(
                    title = item.first,
                    subtitle = item.second,
                    isSelected = selectedWallpaper == item.first,
                    onClick = { onSelect(item.first) }
                )
            }
        }
    }
}

@Composable
private fun WallpaperCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(190.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) AlarmyRed.copy(alpha = 0.2f) else Color(0xFF1A2234)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) AlarmyRed else Color(0xFF2C3954)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (isSelected) {
                    Surface(
                        shape = CircleShape,
                        color = AlarmyRed,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.End)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Column {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(color = AlarmyTextSecondary, fontSize = 10.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "♪ $title",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun WizardStep3SoundVolumeView(
    selectedSoundTitle: String,
    volume: Float,
    gentleWakeUp: Boolean,
    onSoundSelect: (String) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onGentleToggle: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0=Currently Using, 1=Trending, 2=Loud
    val trendingSounds = listOf(
        "Wake up you lazy",
        "Gooooood Morningggggg",
        "You're gonna be late",
        "It's the First of the Month",
        "Fever Dream"
    )
    val loudSounds = listOf(
        "End of the World",
        "Disaster Alert",
        "Cock a doodle doo",
        "Heartbeat Warning"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose your alarm sound",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tabs row matching screenshot 4
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabPill("Currently Using", selectedTab == 0) { selectedTab = 0 }
            TabPill("💖 Trending", selectedTab == 1) { selectedTab = 1 }
            TabPill("💥 Loud", selectedTab == 2) { selectedTab = 2 }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2C)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26344E))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val itemsToShow = when (selectedTab) {
                    1 -> trendingSounds
                    2 -> loudSounds
                    else -> listOf("Video sound • End of the World", "Video sound • Squid Game", "Video sound • Motivation")
                }
                itemsToShow.forEach { itemText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSoundSelect(itemText) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSoundTitle == itemText,
                            onClick = { onSoundSelect(itemText) },
                            colors = RadioButtonDefaults.colors(selectedColor = AlarmyBlue)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = itemText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedSoundTitle == itemText) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        )
                    }
                    Divider(color = Color(0xFF202A40), thickness = 0.8.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Set the volume card matching screenshot 6
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2C)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26344E))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Set the volume",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Volume", style = MaterialTheme.typography.bodySmall, color = AlarmyTextSecondary)
                    Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White))
                }
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color(0xFF37474F)
                    )
                )

                Divider(color = Color(0xFF202A40), thickness = 0.8.dp, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gentle wake-up",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Gradually increase for 30 seconds",
                            style = MaterialTheme.typography.bodySmall,
                            color = AlarmyTextSecondary
                        )
                    }
                    Switch(
                        checked = gentleWakeUp,
                        onCheckedChange = onGentleToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AlarmyBlue
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TabPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color(0xFF1E283D),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.White
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun WizardStep4MissionView(
    selectedMission: MissionType,
    onSelect: (MissionType) -> Unit
) {
    val missions = listOf(
        MissionType.MATH to "Solve arithmetic equations to wake up your brain",
        MissionType.MEMORY to "Find & repeat color tile pattern on grid",
        MissionType.QUOTE to "Typing motivation quotes to start with purpose",
        MissionType.SHAKE to "Shake your device vigorously to get moving",
        MissionType.NORMAL to "Off • Basic slide or tap to dismiss"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose a wake-up mission",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        missions.forEach { (mType, desc) ->
            val isSelected = selectedMission == mType
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(mType) }
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AlarmyRed.copy(alpha = 0.15f) else Color(0xFF141C2C)
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) AlarmyRed else Color(0xFF26344E)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) AlarmyRed else Color(0xFF1D2A44),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = mType.getIcon(),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mType.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = AlarmyTextSecondary
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = AlarmyRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C2C)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26344E)),
        modifier = Modifier.width(260.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlarmyTextSecondary
                )
            }
        }
    }
}

@Composable
private fun UniversityBadge(name: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1A2338),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D3C5C))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.School, contentDescription = null, tint = AlarmyBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }
    }
}
