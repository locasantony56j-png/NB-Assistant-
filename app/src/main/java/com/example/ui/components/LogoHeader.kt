package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.VoiceMode
import com.example.ui.theme.NbBlackSurface
import com.example.ui.theme.NbGreenGlow
import com.example.ui.theme.NbGreenPrimary

@Composable
fun LogoHeader(
    voiceMode: VoiceMode,
    isContinuousModeActive: Boolean,
    onEmergencyStop: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NbBlackSurface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & App Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(NbGreenPrimary, NbGreenGlow)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nb_logo),
                        contentDescription = "NB Assistant Logo",
                        modifier = Modifier.size(38.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "NB Assistant",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NbGreenPrimary.copy(alpha = 0.2f))
                                .border(0.5.dp, NbGreenPrimary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "AI v2.0",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NbGreenPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    when (voiceMode) {
                                        VoiceMode.LISTENING, VoiceMode.CONTINUOUS_ACTIVE -> NbGreenPrimary
                                        VoiceMode.PROCESSING -> Color(0xFF40C4FF)
                                        VoiceMode.SPEAKING -> Color(0xFFFFB300)
                                        VoiceMode.SELF_HEALING -> Color(0xFFE040FB)
                                        VoiceMode.ERROR -> Color(0xFFFF5252)
                                        VoiceMode.IDLE -> Color.Gray
                                    }
                                )
                        )
                        Text(
                            text = when (voiceMode) {
                                VoiceMode.CONTINUOUS_ACTIVE -> "Continuous Mode Active"
                                VoiceMode.LISTENING -> "Listening…"
                                VoiceMode.PROCESSING -> "Fast Executing…"
                                VoiceMode.SPEAKING -> "Speaking…"
                                VoiceMode.SELF_HEALING -> "Auto-Healing…"
                                VoiceMode.ERROR -> "Reconnecting…"
                                VoiceMode.IDLE -> if (isContinuousModeActive) "Continuous Ready" else "Ready"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
            }

            // Quick Actions: Diagnostics & Emergency Stop
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onOpenDiagnostics,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Diagnostics & Self-Healing",
                        tint = NbGreenPrimary
                    )
                }

                AnimatedVisibility(visible = voiceMode != VoiceMode.IDLE) {
                    FilledTonalButton(
                        onClick = onEmergencyStop,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.25f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Stop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
