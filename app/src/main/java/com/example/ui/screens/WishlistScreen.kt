package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.WishlistItem
import com.example.ui.AppViewModel
import com.example.ui.components.*

@Composable
fun WishlistScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val wishlist by viewModel.wishlist.collectAsState()
    val activeFilter by viewModel.wishlistFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Places", "Activities")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header with filter options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Our Wishlist 💖",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                    Text(
                        text = "Dreams to chase, places to see, and pizza to eat together.",
                        fontSize = 15.sp,
                        color = NeoColors.BorderDark.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Categories
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { filter ->
                    val isSelected = activeFilter == filter
                    val bg = when (filter) {
                        "Places" -> NeoColors.AccentTurquoise
                        "Activities" -> NeoColors.PrimaryLight
                        else -> NeoColors.SecondaryLight
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) bg else Color.White,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 3.dp,
                                color = NeoColors.BorderDark,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .clickableNoRipple {
                                viewModel.setWishlistFilter(filter)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeoColors.BorderDark
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Leave space for Floating action button
        ) {
            // Section 1: Places to Visit (if all/Places selected)
            if (activeFilter == "All" || activeFilter == "Places") {
                val places = wishlist.filter { it.category == "Places" }
                if (places.isNotEmpty()) {
                    item {
                        WishlistSectionHeader(title = "Places to Visit", icon = Icons.Default.FlightTakeoff, count = places.size)
                    }

                    items(places, key = { it.id }) { item ->
                        WishlistItemCard(
                            item = item,
                            onCompletedChange = { checked -> viewModel.toggleWishlistItem(item.id, checked) },
                            onDelete = { viewModel.deleteWishlistItem(item.id) }
                        )
                    }
                }
            }

            // Section 2: Things to Try / Activities
            if (activeFilter == "All" || activeFilter == "Activities") {
                val activities = wishlist.filter { it.category == "Activities" }
                if (activities.isNotEmpty()) {
                    item {
                        WishlistSectionHeader(title = "Things to Try", icon = Icons.Default.Star, count = activities.size)
                    }

                    items(activities, key = { it.id }) { item ->
                        WishlistItemCard(
                            item = item,
                            onCompletedChange = { checked -> viewModel.toggleWishlistItem(item.id, checked) },
                            onDelete = { viewModel.deleteWishlistItem(item.id) }
                        )
                    }
                }
            }

            // Section 3: General Other Wishlist Items
            val others = wishlist.filter { it.category != "Places" && it.category != "Activities" }
            if (others.isNotEmpty() && activeFilter == "All") {
                item {
                    WishlistSectionHeader(title = "Other Fun Ideas", icon = Icons.Default.Favorite, count = others.size)
                }

                items(others, key = { it.id }) { item ->
                    WishlistItemCard(
                        item = item,
                        onCompletedChange = { checked -> viewModel.toggleWishlistItem(item.id, checked) },
                        onDelete = { viewModel.deleteWishlistItem(item.id) }
                    )
                }
            }

            // Next Big Goal Widget
            item {
                Spacer(modifier = Modifier.height(16.dp))
                NextBigGoalCard()
            }
        }
    }

    // Floating Add Button for Wishlist
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        NeoButton(
            onClick = { showAddDialog = true },
            backgroundColor = NeoColors.PrimaryPink,
            textColor = Color.White,
            shadowOffsetMax = 6.dp,
            cornerRadius = 50.dp,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add wishlist item",
                modifier = Modifier.size(32.dp)
            )
        }
    }

    if (showAddDialog) {
        AddWishlistDialog(
            onDismiss = { showAddDialog = false },
            onSubmit = { name, desc, cat, cost ->
                viewModel.addWishlistItem(name, desc, cat, cost)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WishlistSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeoColors.BorderDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark
            )
        }

        Box(
            modifier = Modifier
                .background(NeoColors.PrimaryMuted, RoundedCornerShape(12.dp))
                .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("$count Items", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WishlistItemCard(
    item: WishlistItem,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val opacity = if (item.isCompleted) 0.65f else 1f
    val isActivity = item.category == "Activities"

    NeoCard(
        backgroundColor = if (isActivity) NeoColors.PrimaryLight else NeoColors.AccentTurquoise,
        shadowOffset = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Checkbox
            NeoCheckbox(
                checked = item.isCompleted,
                onCheckedChange = onCompletedChange
            )

            // Content info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.itemName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                )

                if (item.itemDescription.isNotEmpty()) {
                    Text(
                        text = item.itemDescription,
                        fontSize = 14.sp,
                        color = NeoColors.BorderDark.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 2.dp),
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Small Category Label
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.BorderDark
                        )
                    }

                    // Cost tier label
                    Box(
                        modifier = Modifier
                            .background(NeoColors.AccentYellow, RoundedCornerShape(12.dp))
                            .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.itemCost,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = NeoColors.BorderDark
                        )
                    }
                }
            }

            // Image Thumbnail (Polaroid Frame) on Right (if provided and is place)
            if (item.photoUrl.isNotEmpty() && !item.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White)
                        .border(3.dp, NeoColors.BorderDark)
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = item.photoUrl,
                        contentDescription = item.itemName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, NeoColors.BorderDark)
                    )
                }
            }

            // Small discrete Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete item",
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NextBigGoalCard() {
    NeoCard(
        backgroundColor = NeoColors.AccentYellow,
        shadowOffset = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative shapes
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-20).dp)
                    .size(60.dp)
                    .background(NeoColors.PrimaryPink, RoundedCornerShape(30.dp))
                    .border(3.dp, NeoColors.BorderDark, RoundedCornerShape(30.dp))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 30.dp)
                    .size(80.dp)
                    .background(NeoColors.AccentTurquoise, RoundedCornerShape(12.dp))
                    .border(3.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                    .rotate(12f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(3.dp, NeoColors.BorderDark, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .rotate(-1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.HotelClass,
                    contentDescription = null,
                    tint = NeoColors.PrimaryPink,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Next Big Goal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )

                Text(
                    text = "Save up for the Europe Trip!",
                    fontSize = 14.sp,
                    color = NeoColors.BorderDark.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar (Neobrutalist style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(NeoColors.PrimaryMuted, RoundedCornerShape(12.dp))
                        .border(3.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                ) {
                    // Filled part
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.45f) // 45% progress
                            .background(NeoColors.PrimaryPink)
                            .border(
                                3.dp, NeoColors.BorderDark,
                                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            )
                    )
                }

                Text(
                    text = "45% there",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
