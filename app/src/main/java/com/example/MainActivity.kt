package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
import com.example.ui.components.NeoColors
import com.example.ui.screens.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database, DAO and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.appDao())

        // ViewModel initialization with our Factory
        val viewModel: AppViewModel by viewModels {
            AppViewModelFactory(repository)
        }

        setContent {
            // Apply Material 3 theme wrapper
            MaterialTheme {
                AppShell(viewModel)
            }
        }
    }
}

@Composable
fun AppShell(viewModel: AppViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val toastMessage by viewModel.toastNotification.collectAsState()
    val profile by viewModel.coupleProfile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoColors.Background)
    ) {
        // Safe drawing wrapper
        Scaffold(
            topBar = {
                TopAppBarComponent(
                    spaceId = profile?.spaceId ?: "SPACE-LOVE-48",
                    userName = profile?.userName ?: "Revan",
                    partnerName = profile?.partnerName ?: "Viona",
                    userAvatar = profile?.userAvatar ?: "🦊",
                    partnerAvatar = profile?.partnerAvatar ?: "🐰",
                    onProfileClick = { viewModel.setTab("Alerts") }
                )
            },
            bottomBar = {
                BottomNavBarComponent(
                    activeTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            },
            containerColor = NeoColors.Background,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Screen Navigation Router
                when (currentTab) {
                    "Dashboard" -> DashboardScreen(viewModel = viewModel)
                    "Gallery" -> GalleryScreen(viewModel = viewModel)
                    "Calendar" -> CalendarScreen(viewModel = viewModel)
                    "Wishlist" -> WishlistScreen(viewModel = viewModel)
                    "Alerts" -> AlertsScreen(viewModel = viewModel)
                }
            }
        }

        // Inside-App Custom Floating Toast Notification (Simulated Push banner)
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
                .zIndex(999f)
        ) {
            toastMessage?.let { msg ->
                NeoCard(
                    backgroundColor = NeoColors.AccentYellow,
                    shadowOffset = 6.dp,
                    cornerRadius = 8.dp,
                    borderWidth = 3.dp,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(NeoColors.PrimaryPink, CircleShape)
                                .border(1.5.dp, NeoColors.BorderDark, CircleShape)
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = msg,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = NeoColors.BorderDark,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.dismissToast() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close alert toast",
                                tint = NeoColors.BorderDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBarComponent(
    spaceId: String,
    userName: String = "Revan",
    partnerName: String = "Viona",
    userAvatar: String = "🦊",
    partnerAvatar: String = "🐰",
    onProfileClick: () -> Unit
) {
    // Elegant, transparent background header from Artistic Flair Design
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Text(
                    text = "SPACE ID: ${spaceId.uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.PrimaryPink,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "EternalMoments",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = NeoColors.BorderDark,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            // Overlapping Circle Avatars with thin black border and drop shadows
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(NeoColors.AccentTurquoise, CircleShape)
                        .border(3.dp, NeoColors.BorderDark, CircleShape)
                        .zIndex(2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userAvatar,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Overlapping Partner Avatar
                Box(
                    modifier = Modifier
                        .offset(x = (-12).dp)
                        .size(44.dp)
                        .background(NeoColors.PrimaryContainer, CircleShape)
                        .border(3.dp, NeoColors.BorderDark, CircleShape)
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = partnerAvatar,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBarComponent(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        TabItem("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
        TabItem("Gallery", Icons.Default.PhotoLibrary, Icons.Outlined.PhotoLibrary),
        TabItem("Calendar", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
        TabItem("Wishlist", Icons.Default.Favorite, Icons.Outlined.FavoriteBorder),
        TabItem("Alerts", Icons.Default.Notifications, Icons.Outlined.Notifications)
    )

    // Bottom Navigation Bar styled as floating card from Artistic Flair Design
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
    ) {
        // Shadow Layer (placed behind with offset)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .offset(x = 4.dp, y = 4.dp)
                .background(NeoColors.BorderDark, RoundedCornerShape(24.dp))
        )
        // Main Navigation Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(4.dp, NeoColors.BorderDark, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isActive = activeTab == tab.name

                if (isActive) {
                    // Active Tab item styled as the button from Artistic Flair design HTML
                    Box(
                        modifier = Modifier
                            .padding(bottom = 3.dp, end = 3.dp)
                            .clickable { onTabSelected(tab.name) }
                    ) {
                        // Tiny Shadow Layer for active button
                        Box(
                            modifier = Modifier
                                .size(width = 54.dp, height = 48.dp)
                                .offset(x = 3.dp, y = 3.dp)
                                .background(NeoColors.BorderDark, RoundedCornerShape(12.dp))
                        )
                        // Button Face
                        Column(
                            modifier = Modifier
                                .size(width = 54.dp, height = 48.dp)
                                .background(NeoColors.PrimaryContainer, RoundedCornerShape(12.dp))
                                .border(3.dp, NeoColors.BorderDark, RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tab.activeIcon,
                                contentDescription = tab.name,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tab.name,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Inactive Tab item (simple elegant icon)
                    Column(
                        modifier = Modifier
                            .size(width = 54.dp, height = 48.dp)
                            .clickable { onTabSelected(tab.name) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.inactiveIcon,
                            contentDescription = tab.name,
                            tint = NeoColors.BorderDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = tab.name,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.BorderDark.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

data class TabItem(
    val name: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
)
