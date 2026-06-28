package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlertNotification
import com.example.ui.AppViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.alerts.collectAsState()
    val profile by viewModel.coupleProfile.collectAsState()

    var editYourName by remember(profile) { mutableStateOf(profile?.userName ?: "Revan") }
    var editPartnerName by remember(profile) { mutableStateOf(profile?.partnerName ?: "Viona") }
    var editSpaceId by remember(profile) { mutableStateOf(profile?.spaceId ?: "SPACE-LOVE-48") }

    // Use simple date parameters for Anniversary
    var annivYear by remember(profile) {
        val cal = Calendar.getInstance().apply { timeInMillis = profile?.anniversaryLong ?: System.currentTimeMillis() }
        mutableStateOf(cal.get(Calendar.YEAR).toString())
    }
    var annivMonth by remember(profile) {
        val cal = Calendar.getInstance().apply { timeInMillis = profile?.anniversaryLong ?: System.currentTimeMillis() }
        mutableStateOf((cal.get(Calendar.MONTH) + 1).toString())
    }
    var annivDay by remember(profile) {
        val cal = Calendar.getInstance().apply { timeInMillis = profile?.anniversaryLong ?: System.currentTimeMillis() }
        mutableStateOf(cal.get(Calendar.DAY_OF_MONTH).toString())
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Screen Header
        Text(
            text = "Space Alerts & Settings ⚙️",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = NeoColors.BorderDark
        )

        // PART 1: Partner Activity Simulator (Crucial Interactivity)
        NeoCard(
            backgroundColor = NeoColors.AccentYellow,
            shadowOffset = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CellTower,
                        contentDescription = "FCM Sync Simulator",
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FCM Mock Sync Simulator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                }

                Text(
                    text = "No real-time Firebase backend connected yet? No problem! Use this console to simulate your partner Viona's actions on another device. Watch the database sync reactively!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.BorderDark.copy(alpha = 0.8f)
                )

                // Sim buttons grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeoButton(
                        onClick = { viewModel.simulatePartnerAction("upload_photo") },
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 2.dp
                    ) {
                        Text("Viona Uploads Photo 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    NeoButton(
                        onClick = { viewModel.simulatePartnerAction("add_schedule") },
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 2.dp
                    ) {
                        Text("Viona Adds Schedule 📅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeoButton(
                        onClick = { viewModel.simulatePartnerAction("complete_wishlist") },
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 2.dp
                    ) {
                        Text("Viona Checks Wishlist ✔️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    NeoButton(
                        onClick = { viewModel.simulatePartnerAction("add_wishlist") },
                        backgroundColor = Color.White,
                        textColor = NeoColors.BorderDark,
                        modifier = Modifier.weight(1f),
                        shadowOffsetMax = 3.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 2.dp
                    ) {
                        Text("Viona Adds Wishlist ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // PART 2: Space Settings & Profiles
        NeoCard(
            backgroundColor = Color.White,
            shadowOffset = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Profile Settings",
                        tint = NeoColors.PrimaryPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Duo Space Connection",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                }

                // Inputs
                Text("Your Profile Name:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = editYourName, onValueChange = { editYourName = it }, placeholder = "Your name")

                Text("Partner's Profile Name:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = editPartnerName, onValueChange = { editPartnerName = it }, placeholder = "Partner's name")

                Text("Shared Space ID Code:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = editSpaceId, onValueChange = { editSpaceId = it }, placeholder = "e.g., SPACE-LOVE-99")

                Text("Anniversary Date (YYYY - MM - DD):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1.5f)) {
                        NeoTextField(value = annivYear, onValueChange = { annivYear = it }, placeholder = "2023")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = annivMonth, onValueChange = { annivMonth = it }, placeholder = "02")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = annivDay, onValueChange = { annivDay = it }, placeholder = "14")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                NeoButton(
                    onClick = {
                        try {
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, annivYear.toIntOrNull() ?: 2023)
                                set(Calendar.MONTH, (annivMonth.toIntOrNull() ?: 2) - 1)
                                set(Calendar.DAY_OF_MONTH, annivDay.toIntOrNull() ?: 14)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                            }
                            viewModel.updateProfile(
                                yourName = editYourName,
                                partnerName = editPartnerName,
                                spaceId = editSpaceId,
                                anniversaryLong = cal.timeInMillis
                            )
                        } catch (e: Exception) {
                            viewModel.showToast("Invalid Anniversary Date values!")
                        }
                    },
                    backgroundColor = NeoColors.PrimaryPink,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, "Save Settings")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Profiles & Anniversary", fontWeight = FontWeight.Bold)
                }
            }
        }

        // PART 3: Notification Alerts Stream
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alert notifications",
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Notifications Log (${alerts.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                }

                if (alerts.isNotEmpty()) {
                    NeoButton(
                        onClick = { viewModel.clearAllAlerts() },
                        backgroundColor = Color.White,
                        textColor = NeoColors.PrimaryPink,
                        shadowOffsetMax = 2.dp,
                        cornerRadius = 8.dp,
                        borderWidth = 2.dp
                    ) {
                        Text("Clear Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(NeoColors.BorderDark)
            )

            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Logs are currently clean. Spark some partner actions above!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                alerts.forEach { alert ->
                    AlertItemRow(alert = alert, onDelete = { viewModel.deleteAlert(alert.id) })
                }
            }
        }
    }
}

@Composable
fun AlertItemRow(alert: AlertNotification, onDelete: () -> Unit) {
    val dateStr = remember(alert.timestamp) {
        val sdf = SimpleDateFormat("HH:mm a - dd MMM", Locale.getDefault())
        sdf.format(Date(alert.timestamp))
    }

    val icon = when (alert.type) {
        "memory_add" -> Icons.Default.PhotoLibrary
        "schedule_add" -> Icons.Default.CalendarMonth
        "wishlist_update" -> Icons.Default.CheckCircle
        "wishlist_add" -> Icons.Default.AddShoppingCart
        else -> Icons.Default.Notifications
    }

    val iconBg = when (alert.type) {
        "memory_add" -> NeoColors.PrimaryLight
        "schedule_add" -> NeoColors.AccentYellow
        "wishlist_update" -> NeoColors.AccentTurquoise
        else -> NeoColors.SecondaryLight
    }

    NeoCard(
        backgroundColor = Color.White,
        shadowOffset = 4.dp,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .background(iconBg, RoundedCornerShape(8.dp))
                    .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(8.dp))
                    .size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Description block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )
                Text(
                    text = alert.body,
                    fontSize = 13.sp,
                    color = NeoColors.BorderDark.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.BorderDark.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Delete specific log
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Alert",
                    tint = NeoColors.BorderDark.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
