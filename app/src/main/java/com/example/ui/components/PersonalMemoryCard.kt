package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.model.MemoryStore
import com.example.ui.theme.NbBlackOutline
import com.example.ui.theme.NbBlackSurface
import com.example.ui.theme.NbBlackSurfaceVariant
import com.example.ui.theme.NbGreenPrimary

@Composable
fun PersonalMemoryCard(
    onCalculateSample: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Routine, 1: Memory & Preferences, 2: Offline Math

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NbBlackSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NbBlackOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Personal Memory & Productivity",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Daily routine schedules, non-sensitive memory, and offline calculator",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NbGreenPrimary,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Daily Schedule", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Memory", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Offline Math", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Daily Routines
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MemoryStore.defaultRoutines.forEach { routine ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NbBlackSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column {
                                            Text(
                                                text = routine.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = routine.days,
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Text(
                                        text = routine.timeLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NbGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // User Preferences & Memory
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MemoryStore.defaultPreferences.forEach { pref ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NbBlackSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = pref.key,
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                    Text(
                                        text = pref.value,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Offline Math & Calculations
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Spoken Math Engine (< 1ms execution):",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        val mathSamples = listOf(
                            "২৫০ গুণ ৪ কত?",
                            "১০০০ থেকে ৩৫০ বাদ দাও",
                            "১৫০ যোগ ৮০ কত",
                            "১০০ ভাগ ৪"
                        )
                        mathSamples.forEach { sample ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NbBlackSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sample,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    OutlinedButton(
                                        onClick = { onCalculateSample(sample) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NbGreenPrimary)
                                    ) {
                                        Text(text = "Calculate", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
