package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NoMeetingRoom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreadMakerModel
import com.example.data.model.MachineState
import com.example.data.model.MachineTelemetry
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.BreadWarmWheat
import com.example.ui.theme.KneadGreen

@Composable
fun MachineStatusCard(
    telemetry: MachineTelemetry,
    onToggleConnection: () -> Unit,
    onToggleLid: () -> Unit,
    onTogglePanLock: () -> Unit,
    onToggleLamp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isZojirushi = telemetry.model.isZojirushi

    // Synchronized blade rotation animations
    val infiniteTransition = rememberInfiniteTransition(label = "motor_rot")
    val motorRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (telemetry.motorRpm > 80) 500 else 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "motor_rot_angle"
    )

    // Second blade counter-rotation / synchronous turn for Zojirushi BB-PDC20
    val motorRotationRight by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (telemetry.motorRpm > 80) 500 else 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "motor_rot_angle_right"
    )

    val lampGlowColor by animateColorAsState(
        targetValue = if (telemetry.interiorLampOn) Color(0xFFFFD54F) else Color(0xFF424242),
        label = "lamp_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("machine_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Machine model & connection status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (telemetry.isConnected) KneadGreen else AlertBerryRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = telemetry.machineName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (telemetry.isConnected) "${telemetry.connectionType} • ${telemetry.model.panType}" else "Disconnected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (telemetry.isConnected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.clickable { onToggleConnection() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (telemetry.isConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                            contentDescription = "Connection Toggle",
                            modifier = Modifier.size(16.dp),
                            tint = if (telemetry.isConnected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (telemetry.isConnected) "Online" else "Pair",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (telemetry.isConnected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Zojirushi Menu Course & Hardware Feature Pill
            if (isZojirushi) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Course #${telemetry.zojirushiMenuCourse.courseNumber}: ${telemetry.zojirushiMenuCourse.courseName}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (telemetry.model.hasHomemadeMemory) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "Mem ${telemetry.homemadeMemorySlot}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "${if (telemetry.model.bladeCount == 2) "Dual Blades" else "Single Blade"} • ${if (telemetry.model.hasLidHeater) "Twin Heaters" else "Base Element"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Telemetry Readout Box (Simulated Oven Chamber Window)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E1510),
                                if (telemetry.interiorLampOn) Color(0xFF332115) else Color(0xFF150F0B)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (telemetry.heatingElementActive) BakeFireOrange.copy(alpha = 0.8f) else Color(0xFF3D2C20),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Temperature Chamber Gauge
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Chamber Heat",
                                    tint = if (telemetry.heatingElementActive) BakeFireOrange else Color(0xFF888888),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CHAMBER TEMP",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.sp,
                                    color = BreadWarmWheat.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${telemetry.chamberTempF.toInt()}°F",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (telemetry.chamberTempF > 250f) Color(0xFFFF8A65) else Color(0xFFFFD54F)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Target ${telemetry.chamberTargetTempF.toInt()}°F",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFAAAAAA),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Motor RPM / Kneading Blades
                        Column(horizontalAlignment = Alignment.End) {
                            if (telemetry.model.bladeCount == 2) {
                                // Zojirushi Dual Blades Status
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Left Paddle",
                                        tint = if (telemetry.motorRpm > 0) BreadWarmWheat else Color(0xFF888888),
                                        modifier = Modifier
                                            .size(15.dp)
                                            .graphicsLayer {
                                                rotationZ = if (telemetry.motorRpm > 0) motorRotation else 0f
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Right Paddle",
                                        tint = if (telemetry.motorRpm > 0) BreadWarmWheat else Color(0xFF888888),
                                        modifier = Modifier
                                            .size(15.dp)
                                            .graphicsLayer {
                                                rotationZ = if (telemetry.motorRpm > 0) motorRotationRight else 0f
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (telemetry.motorRpm > 0) "${telemetry.motorRpm} RPM" else "IDLE",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (telemetry.motorRpm > 0) BreadWarmWheat else Color(0xFF888888)
                                    )
                                }
                                Text(
                                    text = if (telemetry.motorRpm > 0) "Twin Blades Active" else "Twin Blades Idle",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (telemetry.motorRpm > 0) BreadWarmWheat.copy(alpha = 0.8f) else Color(0xFF888888)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Paddle Motor",
                                        tint = if (telemetry.motorRpm > 0) BreadWarmWheat else Color(0xFF888888),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .graphicsLayer {
                                                rotationZ = if (telemetry.motorRpm > 0) motorRotation else 0f
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (telemetry.motorRpm > 0) "${telemetry.motorRpm} RPM" else "MOTOR IDLE",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (telemetry.motorRpm > 0) BreadWarmWheat else Color(0xFF888888)
                                    )
                                }
                                Text(
                                    text = if (telemetry.motorRpm > 0) "Single Blade Kneading" else "Blade Idle",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (telemetry.motorRpm > 0) BreadWarmWheat.copy(alpha = 0.8f) else Color(0xFF888888)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Crust Booster or Lid Heater Indicator
                            if (telemetry.lidHeaterActive || telemetry.crustBoosterActive) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BakeFireOrange.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BakeFireOrange)
                                ) {
                                    Text(
                                        text = if (isZojirushi) "LID HEATER ON" else "CRUST BOOST ON",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFAB91),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Chamber Element Bar with Dual Heaters Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                telemetry.state == MachineState.COMPLETED -> "Bake finished • Warm hold"
                                telemetry.state == MachineState.PAUSED -> "Paused • Chamber sealed"
                                telemetry.state == MachineState.IDLE -> "Standby • Ready to start"
                                isZojirushi && telemetry.lidHeaterActive -> "Dual Heaters Active: Base + Lid Element for Top Crust"
                                telemetry.heatingElementActive -> "Heating Elements Active (Upper + Lower)"
                                telemetry.motorRpm > 0 && isZojirushi -> "Twin blades actively kneading horizontal 2-lb loaf"
                                telemetry.motorRpm > 0 -> "Kneading paddle actively folding gluten"
                                else -> "Fermentation proofing chamber stabilized"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCCCCCC),
                            modifier = Modifier.weight(1f)
                        )

                        // Light Lamp Switch
                        IconButton(
                            onClick = onToggleLamp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Interior Light",
                                tint = lampGlowColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hardware Safety Switches: Lid Position & Pan Lock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lid Toggle Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (telemetry.lidOpen) AlertBerryRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (telemetry.lidOpen) AlertBerryRed else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleLid() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (telemetry.lidOpen) Icons.Default.MeetingRoom else Icons.Default.NoMeetingRoom,
                            contentDescription = "Lid Status",
                            tint = if (telemetry.lidOpen) AlertBerryRed else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (telemetry.lidOpen) "Lid: OPEN (Loss)" else "Lid: Sealed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (telemetry.lidOpen) AlertBerryRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Pan Lock Toggle Chip (Dual-coupling lock for BB-PDC20)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!telemetry.panLocked) AlertBerryRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!telemetry.panLocked) AlertBerryRed else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTogglePanLock() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (telemetry.panLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Pan Lock Status",
                            tint = if (telemetry.panLocked) KneadGreen else AlertBerryRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (telemetry.panLocked) {
                                if (isZojirushi) "Pan: Dual-Coupled" else "Pan: Locked"
                            } else "Pan: Unlocked",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (telemetry.panLocked) MaterialTheme.colorScheme.onSurface else AlertBerryRed
                        )
                    }
                }
            }
        }
    }
}
