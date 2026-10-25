package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LatencyMetrics
import com.example.model.VoiceMode
import com.example.ui.theme.*

@Composable
fun VoiceControlCard(
    voiceMode: VoiceMode,
    isContinuousModeEnabled: Boolean,
    interimText: String,
    lastReply: String,
    latencyMetrics: LatencyMetrics,
    onToggleListening: () -> Unit,
    onToggleContinuousMode: (Boolean) -> Unit,
    onSampleCommandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = voiceMode == VoiceMode.LISTENING || voiceMode == VoiceMode.CONTINUOUS_ACTIVE

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLive) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NbBlackSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(NbGreenPrimary.copy(alpha = 0.45f), Color.Transparent)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interactive Glowing Orb
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        NbGreenPrimary.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(16.dp, CircleShape, spotColor = NbGreenPrimary)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NbGreenPrimary, NbGreenDark, Color(0xFF00381B))
                            )
                        )
                        .border(2.dp, NbGreenGlow, CircleShape)
                        .clickable { onToggleListening() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLive) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Microphone",
                        tint = Color.Black,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            // Status label
            Text(
                text = when (voiceMode) {
                    VoiceMode.CONTINUOUS_ACTIVE -> "Active Listening (Say commands anytime)"
                    VoiceMode.LISTENING -> "Listening…"
                    VoiceMode.PROCESSING -> "Processing command…"
                    VoiceMode.SPEAKING -> "Speaking…"
                    VoiceMode.SELF_HEALING -> "Auto-Healing Session…"
                    VoiceMode.ERROR -> "Reconnecting…"
                    VoiceMode.IDLE -> if (isContinuousModeEnabled) "Tap Orb or Say \"NB\"" else "Tap Orb to Speak"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLive) NbGreenPrimary else Color(0xFFE2E8F0)
            )

            // Live speech transcript
            if (interimText.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NbBlackSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "“$interimText”",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Assistant Reply Bubble
            if (lastReply.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1B2A20),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(NbGreenPrimary.copy(alpha = 0.5f), Color.Transparent))
                    ),
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = NbGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = lastReply,
                            fontSize = 14.sp,
                            color = Color(0xFFE6FFFA),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 2-Second Latency Badge Chip
            Row(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F1A14))
                    .border(1.dp, NbGreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NbGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Total Latency: ${latencyMetrics.totalLatencyMs}ms (Target: < 2000ms)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NbGreenGlow
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = NbBlackOutline)
            Spacer(modifier = Modifier.height(14.dp))

            // Continuous Voice Mode Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Continuous Voice Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Say \"NB\" once, then speak commands naturally without repeating \"NB\" each time.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = isContinuousModeEnabled,
                    onCheckedChange = onToggleContinuousMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NbGreenPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick command recommendation chips
            val sampleCommands = listOf(
                "YouTube-এ গান চালাও",
                "Volume বাড়াও",
                "নিচে scroll করো",
                "নতুন notification বলো",
                "WhatsApp খোলো",
                "একদম নিচে যাও"
            )

            Text(
                text = "Quick Voice Commands (Bangla & Banglish):",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sampleCommands) { cmd ->
                    SuggestionChip(
                        onClick = { onSampleCommandClick(cmd) },
                        label = { Text(text = cmd, fontSize = 12.sp, color = Color(0xFFE2E8F0)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = NbBlackSurfaceVariant
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = NbBlackOutline
                        )
                    )
                }
            }
        }
    }
}
