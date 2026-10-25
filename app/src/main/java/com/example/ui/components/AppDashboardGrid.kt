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
import com.example.ui.theme.NbGreenPrimary

@Composable
fun AppDashboardGrid(
    onAppAction: (AppItem, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSixColumns by remember { mutableStateOf(false) }

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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF09111E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with 3/6 columns toggle
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

                // Grid Column Toggle
                FilledTonalButton(
                    onClick = { isSixColumns = !isSixColumns },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color(0xFFCBD5E1)
                    )
                ) {
                    Icon(
                        imageVector = if (isSixColumns) Icons.Default.ViewAgenda else Icons.Default.GridView,
                        contentDescription = "Toggle Grid Columns",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isSixColumns) "3-Col" else "6-Col", fontSize = 11.sp)
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
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF101A2B),
                    unfocusedContainerColor = Color(0xFF101A2B)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedCategory == null) 0 else selectedCategory!!.ordinal,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = NbGreenPrimary,
                divider = {}
            ) {
                AppCategory.values().forEach { cat ->
                    val isSelected = (selectedCategory == null && cat == AppCategory.ALL) || selectedCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (cat == AppCategory.ALL) null else cat
                        },
                        text = {
                            Text(
                                text = if (cat == AppCategory.ALL) "All (${AppRegistry.allApps.size})" else cat.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Grid (Exact visual match to screenshot)
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isSixColumns) 6 else 3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(410.dp)
            ) {
                items(filteredApps, key = { it.id }) { app ->
                    AppItemCard(
                        app = app,
                        isCompact = isSixColumns,
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
    isCompact: Boolean,
    onOpen: () -> Unit,
    onScrollDown: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F1A2A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = if (isCompact) 8.dp else 12.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon badge matching the app's brand styling in the screenshot
            Box(
                modifier = Modifier
                    .size(if (isCompact) 36.dp else 46.dp)
                    .clip(CircleShape)
                    .background(app.badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAppIcon(app.id),
                    contentDescription = app.name,
                    tint = app.iconColor,
                    modifier = Modifier.size(if (isCompact) 20.dp else 26.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 6.dp))

            Text(
                text = app.name,
                fontSize = if (isCompact) 10.sp else 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Optional Sub-label (e.g. "Android app only" for Clock in screenshot)
            if (app.subLabel != null) {
                Text(
                    text = app.subLabel,
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            if (!isCompact) {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick Scroll Action Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onScrollDown() }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Scroll",
                        tint = NbGreenPrimary,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Scroll",
                        fontSize = 9.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

fun getAppIcon(appId: String): ImageVector {
    return when (appId) {
        "whatsapp", "messenger", "telegram", "imo" -> Icons.Default.Chat
        "phone" -> Icons.Default.Call
        "sms" -> Icons.Default.Message
        "contacts" -> Icons.Default.Contacts
        "camera" -> Icons.Default.PhotoCamera
        "youtube" -> Icons.Default.PlayArrow
        "spotify" -> Icons.Default.MusicNote
        "netflix" -> Icons.Default.Movie
        "maps" -> Icons.Default.LocationOn
        "chrome" -> Icons.Default.Language
        "gmail" -> Icons.Default.Mail
        "calendar" -> Icons.Default.CalendarToday
        "photos" -> Icons.Default.PhotoLibrary
        "drive" -> Icons.Default.CloudQueue
        "calculator" -> Icons.Default.Calculate
        "clock" -> Icons.Default.Alarm
        "settings" -> Icons.Default.Settings
        "playstore" -> Icons.Default.Shop
        "files" -> Icons.Default.Folder
        "facebook" -> Icons.Default.ThumbUp
        "instagram" -> Icons.Default.CameraAlt
        "tiktok" -> Icons.Default.Audiotrack
        "twitter" -> Icons.Default.Close
        "linkedin" -> Icons.Default.Work
        "snapchat" -> Icons.Default.SentimentVerySatisfied
        "bkash", "nagad" -> Icons.Default.AccountBalanceWallet
        "daraz" -> Icons.Default.ShoppingBag
        "meet", "zoom" -> Icons.Default.Videocam
        "translate" -> Icons.Default.Translate
        "chatgpt", "gemini" -> Icons.Default.AutoAwesome
        else -> Icons.Default.Apps
    }
}
