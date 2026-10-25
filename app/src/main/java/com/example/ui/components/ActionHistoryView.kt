package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionLogItem
import com.example.model.ActionStatus
import com.example.ui.theme.NbBlackOutline
import com.example.ui.theme.NbBlackSurface
import com.example.ui.theme.NbBlackSurfaceVariant
import com.example.ui.theme.NbGreenPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActionHistoryView(
    actionLogs: List<ActionLogItem>,
    modifier: Modifier = Modifier
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = NbGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Live Execution & Automation Log",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (actionLogs.isEmpty()) {
                Text(
                    text = "No voice actions executed yet. Say \"NB, YouTube-e gaan chalao\" or \"Volume barao\".",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actionLogs.take(5).forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NbBlackSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = when (item.status) {
                                            ActionStatus.SUCCESS, ActionStatus.HEALED -> NbGreenPrimary
                                            ActionStatus.NEEDS_USER -> Color(0xFFFFB300)
                                            ActionStatus.FAILED -> Color(0xFFFF5252)
                                            ActionStatus.CANCELLED -> Color.Gray
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Column {
                                        Text(
                                            text = item.command,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${item.targetApp} • ${item.resultMessage}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${item.latencyMs}ms",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.latencyMs <= 2000) NbGreenPrimary else Color(0xFFFFB300)
                                    )
                                    Text(
                                        text = SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(Date(item.timestamp)),
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
