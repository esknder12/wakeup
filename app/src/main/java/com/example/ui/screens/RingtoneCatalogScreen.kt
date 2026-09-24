package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmySound
import com.example.data.model.AlarmySoundCatalog
import com.example.data.model.SoundCategory
import com.example.ui.theme.*

@Composable
fun RingtoneCatalogScreen(
    previewSoundId: String?,
    onPreviewClick: (AlarmySound) -> Unit,
    onSelectSound: (AlarmySound) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<SoundCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSounds = remember(selectedCategory, searchQuery) {
        AlarmySoundCatalog.allSounds.filter { sound ->
            val matchCat = selectedCategory == null || sound.category == selectedCategory
            val matchSearch = searchQuery.isBlank() ||
                    sound.title.contains(searchQuery, ignoreCase = true) ||
                    sound.description.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlarmyBackground)
            .padding(top = 12.dp)
    ) {
        // Banner header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AlarmySurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = AlarmyRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Niqu ንቁ Ringtones Catalog",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Loud Ringtones That Hit Your Eardrums & Motivational Wake-Up Calls",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AlarmyTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AlarmyRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "NIQU/EN/RINGTONES",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AlarmyRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${AlarmySoundCatalog.allSounds.size} Official Sounds",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlarmyBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search End of the World, Squid Game, Motivation...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = AlarmyTextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = AlarmyTextSecondary)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AlarmyRed,
                unfocusedBorderColor = AlarmyCardBorder,
                focusedContainerColor = AlarmyCard,
                unfocusedContainerColor = AlarmyCard,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Pill Filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryPill(
                    label = "All Sounds",
                    isSelected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
            }
            items(SoundCategory.values()) { category ->
                CategoryPill(
                    label = category.title,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sound List
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSounds, key = { it.id }) { sound ->
                AlarmySoundCard(
                    sound = sound,
                    isPlaying = previewSoundId == sound.id,
                    onPreviewClick = { onPreviewClick(sound) },
                    onSelectSound = { onSelectSound(sound) }
                )
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) AlarmyRed else AlarmySurface,
        shape = RoundedCornerShape(20.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AlarmyCardBorder) else null
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = if (isSelected) Color.White else AlarmyTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun AlarmySoundCard(
    sound: AlarmySound,
    isPlaying: Boolean,
    onPreviewClick: () -> Unit,
    onSelectSound: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) AlarmyRed.copy(alpha = 0.15f) else AlarmyCard
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 1.5.dp else 1.dp,
            color = if (isPlaying) AlarmyRed else AlarmyCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Preview Button
            Surface(
                onClick = onPreviewClick,
                shape = CircleShape,
                color = if (isPlaying) AlarmyRed else AlarmyBlue.copy(alpha = 0.2f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Stop Preview" else "Play Preview",
                        tint = if (isPlaying) Color.White else AlarmyBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sound.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (sound.isLoud) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = AlarmyRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Loud",
                                    tint = AlarmyRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Loud",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = AlarmyRed
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sound.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlarmyTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sound.category.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AlarmyBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = " • ${sound.duration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlarmyTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Select button
            IconButton(
                onClick = onSelectSound,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Select Ringtone",
                    tint = if (isPlaying) AlarmyRed else AlarmyTextSecondary
                )
            }
        }
    }
}
