package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Memory
import com.example.ui.AppViewModel
import com.example.ui.components.NeoCard
import com.example.ui.components.NeoColors
import com.example.ui.components.NeoTextField
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GalleryScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.memoryFilter.collectAsState()

    val filterOptions = listOf("All Memories", "Date Nights 🍷", "Trips ✈️")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Filters Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = "Our Gallery 🖼️",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark
            )

            // Search bar
            NeoTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = "Search memories...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = NeoColors.BorderDark
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Horizontal Filters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = activeFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) NeoColors.PrimaryLight else Color.White,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 3.dp,
                                color = NeoColors.BorderDark,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .weight(1f, fill = false)
                            .clickableNoRipple {
                                viewModel.setMemoryFilter(filter)
                            }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeoColors.BorderDark,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        // Memories List (Matches layout in mockups)
        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No memories found 😢",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark
                    )
                    Text(
                        text = "Tap 'Add Memory' on the dashboard to upload one!",
                        fontSize = 14.sp,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(memories, key = { it.id }) { memory ->
                    PolaroidMemoryCard(
                        memory = memory,
                        onDelete = { viewModel.deleteMemory(memory.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PolaroidMemoryCard(
    memory: Memory,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = remember(memory.dateLong) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(memory.dateLong))
    }

    // Select suitable emoji tag dynamically or mock based on title
    val emoji = when {
        memory.title.contains("anniversary", ignoreCase = true) -> "😍"
        memory.title.contains("mountain", ignoreCase = true) -> "🏕️"
        memory.title.contains("ice cream", ignoreCase = true) || memory.title.contains("treat", ignoreCase = true) -> "🍦"
        memory.title.contains("sushi", ignoreCase = true) || memory.title.contains("dinner", ignoreCase = true) -> "🍣"
        memory.memoryType.contains("trip", ignoreCase = true) -> "✈️"
        else -> "💖"
    }

    NeoCard(
        backgroundColor = if (memory.id == "mem_1") NeoColors.PrimaryMuted else Color.White,
        shadowOffset = 8.dp,
        modifier = modifier.fillMaxWidth(),
        onClick = { /* Tactile press feedback only */ }
    ) {
        Column {
            // Polaroid frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color.White)
                    .border(4.dp, NeoColors.BorderDark)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = memory.photoUrl,
                    contentDescription = memory.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, NeoColors.BorderDark)
                )

                // Delete Button overlay on image (Neobrutalist styled)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(NeoColors.AccentYellow, RoundedCornerShape(4.dp))
                        .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                        .size(36.dp)
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Memory",
                            tint = NeoColors.BorderDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = memory.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                    Text(
                        text = memory.storyBlog,
                        fontSize = 14.sp,
                        color = NeoColors.BorderDark.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(3.dp)
                    .background(NeoColors.BorderDark)
            )

            // Location and Date footer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location pin",
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = memory.location,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark.copy(alpha = 0.8f)
                    )
                }

                // Date label bubble
                Box(
                    modifier = Modifier
                        .background(NeoColors.SecondaryLight, RoundedCornerShape(20.dp))
                        .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark
                    )
                }
            }
        }
    }
}

// Simple modifier helper to allow click action without standard ripple effect
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
