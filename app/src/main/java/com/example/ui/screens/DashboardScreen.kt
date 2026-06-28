package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.coupleProfile.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()

    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var showAddWishlistDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Calculate time together dynamically
    val daysTogether = remember(profile) {
        profile?.let {
            val diffMs = System.currentTimeMillis() - it.anniversaryLong
            val days = diffMs / (24 * 60 * 60 * 1000)
            if (days < 0) 0 else days
        } ?: 365L
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Hey there, Lovebirds! 👋",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeoColors.BorderDark,
                lineHeight = 38.sp
            )
            Text(
                text = "Here's what's happening in your shared universe today.",
                fontSize = 16.sp,
                color = NeoColors.BorderDark.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Countdown Bento Widget
        NeoCard(
            backgroundColor = NeoColors.PrimaryLight,
            shadowOffset = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Heart graphic rotated decoration
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = NeoColors.PrimaryPink.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 10.dp)
                        .rotate(15f)
                )

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = NeoColors.PrimaryPink.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .rotate(-15f)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Time Together",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Days indicator box
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = daysTogether.toString(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = NeoColors.PrimaryPink
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Days",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.BorderDark.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Happy (almost) Anniversary! 🎉",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark
                    )
                }
            }
        }

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NeoButton(
                onClick = { showAddMemoryDialog = true },
                backgroundColor = NeoColors.AccentYellow,
                textColor = NeoColors.BorderDark,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AddAPhoto, "Add Memory")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Memory", fontWeight = FontWeight.Bold)
            }

            NeoButton(
                onClick = { showAddWishlistDialog = true },
                backgroundColor = NeoColors.PrimaryLight,
                textColor = NeoColors.BorderDark,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AddShoppingCart, "Add Wishlist")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Wishlist", fontWeight = FontWeight.Bold)
            }
        }

        // Quick Stats Grid
        val daysToAnniversary = remember(profile) {
            profile?.let {
                val cal = Calendar.getInstance()
                val currentYear = cal.get(Calendar.YEAR)
                val annivCal = Calendar.getInstance().apply { timeInMillis = it.anniversaryLong }
                annivCal.set(Calendar.YEAR, currentYear)
                if (annivCal.before(cal)) {
                    annivCal.add(Calendar.YEAR, 1)
                }
                val diffMs = annivCal.timeInMillis - cal.timeInMillis
                val days = diffMs / (24 * 60 * 60 * 1000)
                if (days < 0) 0 else days
            } ?: 12L
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Anniversary Card (Blue / Sky Blue)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clickable { viewModel.setTab("Calendar") }
            ) {
                // Shadow Layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(NeoColors.BorderDark, RoundedCornerShape(24.dp))
                )
                // Main Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeoColors.AccentTurquoise, RoundedCornerShape(24.dp))
                        .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ANNIVERSARY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$daysToAnniversary DAYS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                    Box(
                        modifier = Modifier
                            .background(NeoColors.BorderDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TO GO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Wishlist Items Card (Yellow / Accent Yellow)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clickable { viewModel.setTab("Wishlist") }
            ) {
                // Shadow Layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(NeoColors.BorderDark, RoundedCornerShape(24.dp))
                )
                // Main Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeoColors.AccentYellow, RoundedCornerShape(24.dp))
                        .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "WISHLIST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%02d ITEMS", wishlist.size),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                    Text(
                        text = "VIEW ALL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark,
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                }
            }
        }

        // Latest Activity Segment
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Latest from Partner",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(NeoColors.BorderDark)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid / Columns of simulated partner actions
            val partnerMemories = memories.filter { it.createdBy != (profile?.userId ?: "user_revan") }
            val partnerWishlist = wishlist.filter { it.isCompleted && it.createdBy != (profile?.userId ?: "user_revan") }

            if (partnerMemories.isEmpty() && partnerWishlist.isEmpty()) {
                // Show a standard default memory card
                PartnerMemoryPlaceholderCard(profile?.partnerName ?: "Partner") {
                    viewModel.setTab("Gallery")
                }
            } else {
                if (partnerMemories.isNotEmpty()) {
                    val latest = partnerMemories.first()
                    PartnerMemoryRealCard(latest, profile?.partnerName ?: "Partner") {
                        viewModel.setTab("Gallery")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (partnerWishlist.isNotEmpty()) {
                    val latestWish = partnerWishlist.first()
                    PartnerWishlistCard(latestWish, profile?.partnerName ?: "Partner") {
                        viewModel.setTab("Wishlist")
                    }
                } else {
                    // Show a simple simulated default update card
                    NeoCard(
                        backgroundColor = NeoColors.PrimaryMuted,
                        shadowOffset = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = NeoColors.PrimaryPink,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "WISHLIST UPDATED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeoColors.BorderDark.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "Sushi Date Night",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoColors.BorderDark
                                )
                                Text(
                                    text = "Added to 'Foodie Adventures'",
                                    fontSize = 14.sp,
                                    color = NeoColors.BorderDark.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Memory Dialog Form
    if (showAddMemoryDialog) {
        AddMemoryDialog(
            onDismiss = { showAddMemoryDialog = false },
            onSubmit = { title, blog, location, photo, type ->
                viewModel.addMemory(title, blog, location, System.currentTimeMillis(), photo, type)
                showAddMemoryDialog = false
            }
        )
    }

    // Add Wishlist Dialog Form
    if (showAddWishlistDialog) {
        AddWishlistDialog(
            onDismiss = { showAddWishlistDialog = false },
            onSubmit = { name, desc, cat, cost ->
                viewModel.addWishlistItem(name, desc, cat, cost)
                showAddWishlistDialog = false
            }
        )
    }
}

@Composable
fun PartnerMemoryPlaceholderCard(partnerName: String, onViewGallery: () -> Unit) {
    NeoCard(
        backgroundColor = NeoColors.PrimaryMuted,
        shadowOffset = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            // Polaroid-like Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Polaroid Outer Frame
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp)
                        .background(Color.White)
                        .border(4.dp, NeoColors.BorderDark)
                        .padding(8.dp)
                        .rotate(2f)
                ) {
                    Column {
                        // Beach image fallback / mock photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(NeoColors.AccentTurquoise)
                                .border(2.dp, NeoColors.BorderDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = NeoColors.BorderDark,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Polaroid Memories",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(NeoColors.BorderDark)
            )

            // Description below image
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = NeoColors.PrimaryPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW PHOTO UPLOADED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Beach Trip Memories",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )

                Text(
                    text = "\"Remember that crazy seagull? 🐦\"",
                    fontSize = 16.sp,
                    color = NeoColors.BorderDark.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                NeoButton(
                    onClick = onViewGallery,
                    backgroundColor = NeoColors.PrimaryPink,
                    textColor = Color.White,
                    shadowOffsetMax = 3.dp,
                    cornerRadius = 8.dp
                ) {
                    Text("View Gallery", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PartnerMemoryRealCard(memory: Memory, partnerName: String, onViewGallery: () -> Unit) {
    NeoCard(
        backgroundColor = NeoColors.PrimaryMuted,
        shadowOffset = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp)
                        .background(Color.White)
                        .border(4.dp, NeoColors.BorderDark)
                        .padding(8.dp)
                        .rotate(-2f)
                ) {
                    Column {
                        AsyncImage(
                            model = memory.photoUrl,
                            contentDescription = memory.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .border(2.dp, NeoColors.BorderDark)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = memory.location,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(NeoColors.BorderDark)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = NeoColors.PrimaryPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW PHOTO FROM $partnerName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = memory.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )

                Text(
                    text = "\"${memory.storyBlog}\"",
                    fontSize = 16.sp,
                    color = NeoColors.BorderDark.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                NeoButton(
                    onClick = onViewGallery,
                    backgroundColor = NeoColors.PrimaryPink,
                    textColor = Color.White,
                    shadowOffsetMax = 3.dp,
                    cornerRadius = 8.dp
                ) {
                    Text("View Gallery", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PartnerWishlistCard(item: WishlistItem, partnerName: String, onViewWishlist: () -> Unit) {
    NeoCard(
        backgroundColor = NeoColors.PrimaryMuted,
        shadowOffset = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = NeoColors.SecondaryMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WISHLIST UPDATED BY $partnerName",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeoColors.BorderDark.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.itemName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark
            )

            if (item.itemDescription.isNotEmpty()) {
                Text(
                    text = item.itemDescription,
                    fontSize = 14.sp,
                    color = NeoColors.BorderDark.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(NeoColors.AccentTurquoise, RoundedCornerShape(20.dp))
                            .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(item.category, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .background(NeoColors.AccentYellow, RoundedCornerShape(20.dp))
                            .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(item.itemCost, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                NeoButton(
                    onClick = onViewWishlist,
                    backgroundColor = NeoColors.BorderDark,
                    textColor = Color.White,
                    shadowOffsetMax = 2.dp,
                    cornerRadius = 8.dp,
                    borderWidth = 2.dp
                ) {
                    Text("Check Wishlist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Dialog Component for adding new memory
@Composable
fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, story: String, location: String, photo: String, type: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var story by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Date Nights 🍷") }

    val categories = listOf("Date Nights 🍷", "Trips ✈️", "General")

    Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            backgroundColor = Color.White,
            shadowOffset = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "New Memory 📸",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Memory Title:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = title, onValueChange = { title = it }, placeholder = "e.g., Beachside Dinner")

                Text("Story / Blog:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = story, onValueChange = { story = it }, placeholder = "Tell the lovely story...")

                Text("Location:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = location, onValueChange = { location = it }, placeholder = "e.g., Jimbaran, Bali")

                Text("Photo URL (Optional):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = photoUrl, onValueChange = { photoUrl = it }, placeholder = "Unsplash or path...")

                Text("Type:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeoColors.PrimaryPink else Color.White,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    2.dp,
                                    NeoColors.BorderDark,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedType = type }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else NeoColors.BorderDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeoButton(
                        onClick = onDismiss,
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    NeoButton(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onSubmit(title, story, location, photoUrl, selectedType)
                            }
                        },
                        backgroundColor = NeoColors.PrimaryPink,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog Component for adding new wishlist item
@Composable
fun AddWishlistDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, desc: String, category: String, cost: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Places") }
    var selectedCost by remember { mutableStateOf("$$") }

    val categories = listOf("Places", "Activities", "General")
    val costs = listOf("$", "$$", "$$$")

    Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            backgroundColor = Color.White,
            shadowOffset = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(
                text = "Add to Wishlist ✨",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Item / Destination Name:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = name, onValueChange = { name = it }, placeholder = "e.g., Roadtrip in Amalfi")

                Text("Description:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = desc, onValueChange = { desc = it }, placeholder = "What are we going to do there?")

                Text("Category:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeoColors.AccentTurquoise else Color.White,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    2.dp,
                                    NeoColors.BorderDark,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoColors.BorderDark
                            )
                        }
                    }
                }

                Text("Estimated Cost:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    costs.forEach { cost ->
                        val isSelected = selectedCost == cost
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NeoColors.AccentYellow else Color.White,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    3.dp,
                                    NeoColors.BorderDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCost = cost }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cost,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeoColors.BorderDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeoButton(
                        onClick = onDismiss,
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    NeoButton(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onSubmit(name, desc, selectedCategory, selectedCost)
                            }
                        },
                        backgroundColor = NeoColors.PrimaryPink,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
