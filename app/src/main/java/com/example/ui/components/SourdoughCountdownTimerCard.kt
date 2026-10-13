package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BakingStageType
import com.example.data.model.MachineState
import com.example.data.model.MachineTelemetry
import com.example.data.model.StepCompletionAlert
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.BreadWarmWheat
import com.example.ui.theme.KneadGreen
import com.example.ui.theme.ProofingBlue

/**
 * High-precision countdown timer component for sourdough proofing and baking stages.
 * Alerts the user with sound, vibration, and push notification when a step is complete.
 */
@Composable
fun SourdoughCountdownTimerCard(
    telemetry: MachineTelemetry,
    onExtendStage: (Int) -> Unit,
    onSkipStage: () -> Unit,
    onDismissStepAlert: () -> Unit,
    onProceedNextStep: () -> Unit,
    onTestStepAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentStage = telemetry.currentStage
    val isProofing = telemetry.isCurrentStageProofing
    val isBaking = telemetry.isCurrentStageBaking

    val animatedProgress by animateFloatAsState(
        targetValue = telemetry.stageProgressFraction,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "timer_progress"
    )

    val remainingMins = telemetry.stageRemainingSeconds / 60
    val remainingSecs = telemetry.stageRemainingSeconds % 60
    val timeFormatted = com.example.ui.util.FastTimeFormatter.formatMmSs(remainingMins, remainingSecs)

    val themeColor = when {
        isProofing -> ProofingBlue
        isBaking -> BakeFireOrange
        telemetry.motorRpm > 0 -> KneadGreen
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sourdough_countdown_timer_card"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Step Complete Alert Banner (if a step just completed)
        StepCompletionAlertBanner(
            alert = telemetry.activeStepCompletionAlert,
            onDismiss = onDismissStepAlert,
            onProceed = onProceedNextStep,
            onExtend = { onExtendStage(10) }
        )

        // Main Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with stage category badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = themeColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when {
                                        isProofing -> Icons.Default.HourglassBottom
                                        isBaking -> Icons.Default.Whatshot
                                        else -> Icons.Default.Alarm
                                    },
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = when {
                                        isProofing -> "SOURDOUGH PROOFING"
                                        isBaking -> "ARTISAN CRUST BAKE"
                                        telemetry.motorRpm > 0 -> "KNEAD & DEVELOP"
                                        else -> "EQUALIZATION"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = themeColor
                                )
                            }
                        }
                    }

                    // Temperature badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Chamber Temp",
                                tint = themeColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${telemetry.chamberTempF.toInt()}°F (Target ${currentStage?.targetTempF ?: 75}°F)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Circular Progress Timer Centerpiece
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(175.dp)
                            .testTag("circular_countdown_timer")
                    ) {
                        // Background circular track
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(175.dp),
                            color = themeColor.copy(alpha = 0.12f),
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Active animated countdown progress
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(175.dp),
                            color = themeColor,
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round
                        )

                        // Central Digital Countdown Display
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (telemetry.state == MachineState.PAUSED) "PAUSED" else "STEP COUNTDOWN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = if (telemetry.state == MachineState.PAUSED) AlertBerryRed else themeColor
                            )
                            Text(
                                text = "${currentStage?.durationMinutes ?: 0} min total",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Stage Name & Guidance
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = currentStage?.title ?: "No Active Step",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (currentStage?.type) {
                            BakingStageType.REST_AUTOLYSE -> "Enzymes breaking down starches; gluten developing naturally."
                            BakingStageType.RISE_1 -> "Bulk lactic fermentation: check for domed top and airy bubbles."
                            BakingStageType.RISE_2 -> "Intermediate proofing relaxing gluten strands."
                            BakingStageType.RISE_3 -> "Final proof: dough should spring back slowly when gently poked."
                            BakingStageType.BAKE -> "Upper lid heater and base heater actively creating blistered crust."
                            else -> "Step in progress • You will be alerted upon completion."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Proofing Extension & Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Extend proofing by +5 min
                    OutlinedButton(
                        onClick = { onExtendStage(5) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("extend_proof_5m_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+5 min", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Extend proofing by +15 min
                    OutlinedButton(
                        onClick = { onExtendStage(15) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("extend_proof_15m_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+15 min", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    // Skip / Next Step
                    OutlinedButton(
                        onClick = onSkipStage,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("skip_next_step_btn")
                    ) {
                        Text("Next", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    // Test Alert Button
                    IconButton(
                        onClick = onTestStepAlert,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("test_step_alert_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Test Step Alert",
                            tint = themeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Prominent alert banner displayed immediately when a countdown step completes.
 */
@Composable
private fun StepCompletionAlertBanner(
    alert: StepCompletionAlert?,
    onDismiss: () -> Unit,
    onProceed: () -> Unit,
    onExtend: () -> Unit
) {
    AnimatedVisibility(
        visible = alert != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        if (alert != null) {
            val alertColor = if (alert.isProofing) ProofingBlue else BakeFireOrange

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step_completion_alert_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = alertColor.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, alertColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(alertColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (alert.isProofing) Icons.Default.HourglassBottom else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Timer Chime Triggered • Notification Sent",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = alertColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (alert.nextStage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Up Next: ${alert.nextStage.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${alert.nextStage.durationMinutes} min • ${alert.nextStage.targetTempF}°F",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (alert.isProofing) {
                            OutlinedButton(
                                onClick = onExtend,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+10 min Proof", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Button(
                            onClick = onProceed,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = alertColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(alert.actionPrompt, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
