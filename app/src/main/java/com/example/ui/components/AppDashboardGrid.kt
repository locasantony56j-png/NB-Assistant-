package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppItem
import com.example.model.AppRegistry
import com.example.ui.theme.NbBlackOutline
import com.example.ui.theme.NbBlackSurface
import com.example.ui.theme.NbBlackSurfaceVariant
import com.example.ui.theme.NbGreenPrimary

@Composable
fun AppDashboardGrid(
    onAppAction: (AppItem, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(selectedCategory, searchQuery) {
        AppRegistry.allApps.filter { app ->
            (selectedCategory == null || app.category == selectedCategory) &&
            (searchQuery.isBlank() || app.name.contains(searchQuery, ignoreCase = true) ||
             app.aliases.any { it.contains(searchQuery, ignoreCase = true) })
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NbBlackSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NbBlackOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header & Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Universal App Launcher & Deep Control",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Voice command or tap to launch and deep control",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 36+ supported apps…", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NbGreenPrimary,
                    unfocusedBorderColor = NbBlackOutline,
                    focusedContainerColor = NbBlackSurfaceVariant,
                    unfocusedContainerColor = NbBlackSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedCategory == null) 0 else selectedCategory!!.ordinal + 1,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = NbGreenPrimary,
                divider = {}
            ) {
                Tab(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    text = { Text("All (${AppRegistry.allApps.size})", fontSize = 12.sp) }
                )
                AppCategory.values().forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = { Text(cat.title, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Grid (fixed height container to avoid scroll nesting issues)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                items(filteredApps, key = { it.id }) { app ->
                    AppItemCard(
                        app = app,
                        onOpen = { onAppAction(app, "open") },
                        onScrollDown = { onAppAction(app, "scroll_down") }
                    )
                }
            }
        }
    }
}

@Composable
fun AppItemCard(
    app: AppItem,
    onOpen: () -> Unit,
    onScrollDown: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NbBlackSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, NbBlackOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon representation
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(app.category).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAppIcon(app.id),
                    contentDescription = app.name,
                    tint = getCategoryColor(app.category),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = app.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Scroll Action Chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF2D3748))
                    .clickable { onScrollDown() }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Scroll",
                    tint = NbGreenPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Scroll",
                    fontSize = 10.sp,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

fun getAppIcon(appId: String): ImageVector {
    return when (appId) {
        "whatsapp", "messenger", "telegram", "imo" -> Icons.Default.Chat
        "phone" -> Icons.Default.Phone
        "sms" -> Icons.Default.Message
        "contacts" -> Icons.Default.Contacts
        "camera" -> Icons.Default.CameraAlt
        "youtube" -> Icons.Default.PlayCircle
        "spotify" -> Icons.Default.MusicNote
        "netflix" -> Icons.Default.Movie
        "maps" -> Icons.Default.Map
        "chrome" -> Icons.Default.Language
        "gmail" -> Icons.Default.Email
        "calendar" -> Icons.Default.CalendarToday
        "photos" -> Icons.Default.Photo
        "drive" -> Icons.Default.Cloud
        "calculator" -> Icons.Default.Calculate
        "clock" -> Icons.Default.Alarm
        "settings" -> Icons.Default.Settings
        "playstore" -> Icons.Default.Shop
        "files" -> Icons.Default.Folder
        "facebook", "instagram", "tiktok", "twitter", "linkedin" -> Icons.Default.Public
        else -> Icons.Default.Apps
    }
}

fun getCategoryColor(category: AppCategory): Color {
    return when (category) {
        AppCategory.COMMUNICATION -> Color(0xFF25D366)
        AppCategory.SOCIAL -> Color(0xFF1877F2)
        AppCategory.ENTERTAINMENT -> Color(0xFFFF0000)
        AppCategory.AI_AND_GOOGLE -> Color(0xFF4285F4)
        AppCategory.TOOLS_PAYMENT -> Color(0xFFE91E63)
    }
}
