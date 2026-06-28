package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Schedule
import com.example.ui.AppViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsState()
    val selectedDate by viewModel.selectedCalendarDate.collectAsState()

    var showAddEventDialog by remember { mutableStateOf(false) }

    // Calendar logic states
    var currentMonthYearCalendar by remember { mutableStateOf(Calendar.getInstance().apply {
        // Set to October 2026 by default to match mockup, but allow scrolling
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.OCTOBER)
    }) }

    val monthName = remember(currentMonthYearCalendar) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        sdf.format(currentMonthYearCalendar.time)
    }

    val daysInMonth = remember(currentMonthYearCalendar) {
        currentMonthYearCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val firstDayOfWeek = remember(currentMonthYearCalendar) {
        val tempCal = Calendar.getInstance().apply {
            time = currentMonthYearCalendar.time
            set(Calendar.DAY_OF_MONTH, 1)
        }
        tempCal.get(Calendar.DAY_OF_WEEK)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar Section Card
        NeoCard(
            backgroundColor = Color.White,
            shadowOffset = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            // Calendar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev button
                IconButton(
                    onClick = {
                        val newCal = Calendar.getInstance().apply {
                            time = currentMonthYearCalendar.time
                            add(Calendar.MONTH, -1)
                        }
                        currentMonthYearCalendar = newCal
                    },
                    modifier = Modifier
                        .background(NeoColors.PrimaryMuted, RoundedCornerShape(4.dp))
                        .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, "Prev Month")
                }

                Text(
                    text = monthName.uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Next button
                IconButton(
                    onClick = {
                        val newCal = Calendar.getInstance().apply {
                            time = currentMonthYearCalendar.time
                            add(Calendar.MONTH, 1)
                        }
                        currentMonthYearCalendar = newCal
                    },
                    modifier = Modifier
                        .background(NeoColors.PrimaryMuted, RoundedCornerShape(4.dp))
                        .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, "Next Month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days of Week labels
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid rendering
            val totalCells = daysInMonth + (firstDayOfWeek - 1)
            val rows = (totalCells + 6) / 7

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (c in 0..6) {
                            val cellIndex = r * 7 + c
                            val dayNumber = cellIndex - (firstDayOfWeek - 2)

                            if (dayNumber in 1..daysInMonth) {
                                // Check events on this day
                                val isSelected = selectedDate.get(Calendar.DAY_OF_MONTH) == dayNumber &&
                                        selectedDate.get(Calendar.MONTH) == currentMonthYearCalendar.get(Calendar.MONTH) &&
                                        selectedDate.get(Calendar.YEAR) == currentMonthYearCalendar.get(Calendar.YEAR)

                                val dayEvents = schedules.filter {
                                    val evCal = Calendar.getInstance().apply { timeInMillis = it.eventDateLong }
                                    evCal.get(Calendar.DAY_OF_MONTH) == dayNumber &&
                                            evCal.get(Calendar.MONTH) == currentMonthYearCalendar.get(Calendar.MONTH) &&
                                            evCal.get(Calendar.YEAR) == currentMonthYearCalendar.get(Calendar.YEAR)
                                }

                                val hasAnniversary = dayEvents.any { it.eventType.contains("Milestone", ignoreCase = true) }
                                val hasDateNight = dayEvents.any { it.eventType.contains("Food", ignoreCase = true) || it.eventTitle.contains("Date", ignoreCase = true) }

                                val isToday = Calendar.getInstance().let {
                                    it.get(Calendar.DAY_OF_MONTH) == dayNumber &&
                                            it.get(Calendar.MONTH) == currentMonthYearCalendar.get(Calendar.MONTH) &&
                                            it.get(Calendar.YEAR) == currentMonthYearCalendar.get(Calendar.YEAR)
                                }

                                // Style cell based on type
                                val cellBg = when {
                                    hasAnniversary -> NeoColors.AccentYellow
                                    hasDateNight -> NeoColors.PrimaryPink
                                    isToday -> NeoColors.PrimaryLight
                                    isSelected -> NeoColors.AccentTurquoise
                                    else -> NeoColors.PrimaryMuted
                                }

                                val cellTextColor = when {
                                    hasDateNight -> Color.White
                                    else -> NeoColors.BorderDark
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(cellBg, RoundedCornerShape(4.dp))
                                        .border(
                                            width = if (isSelected) 3.dp else 2.dp,
                                            color = if (isSelected) NeoColors.PrimaryPink else NeoColors.BorderDark,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            val newSelected = Calendar.getInstance().apply {
                                                time = currentMonthYearCalendar.time
                                                set(Calendar.DAY_OF_MONTH, dayNumber)
                                            }
                                            viewModel.setSelectedCalendarDate(newSelected)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || isToday || hasAnniversary) FontWeight.Black else FontWeight.Bold,
                                        color = cellTextColor
                                    )

                                    // Event Badges
                                    if (hasDateNight) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Heart badge",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .size(8.dp)
                                                .align(Alignment.BottomEnd)
                                                .offset(x = (-2).dp, y = (-2).dp)
                                        )
                                    } else if (hasAnniversary) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Star badge",
                                            tint = NeoColors.PrimaryPink,
                                            modifier = Modifier
                                                .size(10.dp)
                                                .align(Alignment.TopStart)
                                                .offset(x = 2.dp, y = 2.dp)
                                        )
                                    }
                                }
                            } else {
                                // Empty cell
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Countdown contextual banner (e.g. "3 DAYS LEFT! Our 5th Anniversary")
        val anniversaryEvent = schedules.firstOrNull { it.eventType.contains("Milestone", ignoreCase = true) }
        anniversaryEvent?.let {
            val daysLeft = ((it.eventDateLong - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()
            if (daysLeft in 0..30) {
                NeoCard(
                    backgroundColor = NeoColors.PrimaryPink,
                    shadowOffset = 6.dp,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cake,
                            contentDescription = "Anniversary",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = if (daysLeft == 0) "TODAY IS THE DAY! 🥳" else "$daysLeft DAYS LEFT! 🎂",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = it.eventTitle,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Upcoming Schedules Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Schedules 🗓️",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark
            )

            // Add Event FAB-like Button
            NeoButton(
                onClick = { showAddEventDialog = true },
                backgroundColor = NeoColors.AccentTurquoise,
                textColor = NeoColors.BorderDark,
                shadowOffsetMax = 3.dp,
                cornerRadius = 8.dp,
                borderWidth = 3.dp
            ) {
                Icon(Icons.Default.Add, "Add Event")
            }
        }

        // Schedules List
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No schedules added yet.",
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.BorderDark.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(schedules, key = { it.id }) { schedule ->
                    ScheduleItemCard(
                        schedule = schedule,
                        onDelete = { viewModel.deleteSchedule(schedule.id) }
                    )
                }
            }
        }
    }

    // Add Event Dialog Form
    if (showAddEventDialog) {
        AddEventDialog(
            onDismiss = { showAddEventDialog = false },
            onSubmit = { title, dateLong, type ->
                viewModel.addSchedule(title, dateLong, type)
                showAddEventDialog = false
            }
        )
    }
}

@Composable
fun ScheduleItemCard(
    schedule: Schedule,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateCal = remember(schedule.eventDateLong) {
        Calendar.getInstance().apply { timeInMillis = schedule.eventDateLong }
    }

    val monthShort = remember(dateCal) {
        SimpleDateFormat("MMM", Locale.getDefault()).format(dateCal.time).uppercase()
    }

    val dayStr = remember(dateCal) {
        SimpleDateFormat("dd", Locale.getDefault()).format(dateCal.time)
    }

    val timeStr = remember(dateCal) {
        val hasTime = dateCal.get(Calendar.HOUR_OF_DAY) != 0 || dateCal.get(Calendar.MINUTE) != 0
        if (hasTime) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateCal.time)
        } else {
            "All Day"
        }
    }

    val colorBg = when {
        schedule.eventType.contains("Milestone", ignoreCase = true) -> NeoColors.AccentYellow
        schedule.eventType.contains("Food", ignoreCase = true) -> NeoColors.PrimaryLight
        schedule.eventType.contains("Travel", ignoreCase = true) -> NeoColors.AccentTurquoise
        else -> Color.White
    }

    NeoCard(
        backgroundColor = colorBg,
        shadowOffset = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Date block (like in mockup)
            Column(
                modifier = Modifier
                    .background(NeoColors.BorderDark, RoundedCornerShape(6.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = monthShort,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = dayStr,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 26.sp
                )
            }

            // Central info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.eventTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time icon",
                        tint = NeoColors.BorderDark.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeStr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark.copy(alpha = 0.7f)
                    )
                }

                // Small pill tag inside event
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = schedule.eventType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.BorderDark
                    )
                }
            }

            // Trash can icon
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Event",
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Dialog to add event schedule
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, dateLong: Long, type: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2026") }
    var month by remember { mutableStateOf("10") } // Default Oct
    var day by remember { mutableStateOf("3") }
    var hour by remember { mutableStateOf("19") }
    var minute by remember { mutableStateOf("00") }
    var selectedType by remember { mutableStateOf("Food") }

    val categories = listOf("Food", "Travel", "Milestone", "General")

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
                text = "Add Schedule 📅",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Event Name:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                NeoTextField(value = title, onValueChange = { title = it }, placeholder = "e.g., Dinner Date")

                Text("Date (YYYY - MM - DD):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1.5f)) {
                        NeoTextField(value = year, onValueChange = { year = it }, placeholder = "2026")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = month, onValueChange = { month = it }, placeholder = "10")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = day, onValueChange = { day = it }, placeholder = "03")
                    }
                }

                Text("Time (Hour : Minute) (Optional):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = hour, onValueChange = { hour = it }, placeholder = "19")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        NeoTextField(value = minute, onValueChange = { minute = it }, placeholder = "00")
                    }
                }

                Text("Category Type:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                .padding(horizontal = 10.dp, vertical = 6.dp)
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
                                // Calculate time Long
                                try {
                                    val cal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, year.toIntOrNull() ?: 2026)
                                        set(Calendar.MONTH, (month.toIntOrNull() ?: 10) - 1)
                                        set(Calendar.DAY_OF_MONTH, day.toIntOrNull() ?: 1)
                                        set(Calendar.HOUR_OF_DAY, hour.toIntOrNull() ?: 0)
                                        set(Calendar.MINUTE, minute.toIntOrNull() ?: 0)
                                        set(Calendar.SECOND, 0)
                                    }
                                    onSubmit(title, cal.timeInMillis, selectedType)
                                } catch (e: Exception) {
                                    // Fallback to today
                                    onSubmit(title, System.currentTimeMillis(), selectedType)
                                }
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
