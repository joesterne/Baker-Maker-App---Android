package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreadMakerModel
import com.example.data.model.ConverterSettings
import com.example.data.model.MachineTelemetry
import com.example.data.model.ZojirushiMenuCourse
import com.example.ui.components.BakerMeasurementConverterCard
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden

@Composable
fun MachineSettingsScreen(
    telemetry: MachineTelemetry,
    onToggleConnection: () -> Unit,
    onSelectModel: (BreadMakerModel) -> Unit,
    onSelectZojirushiCourse: (ZojirushiMenuCourse) -> Unit,
    onSelectMemorySlot: (Int) -> Unit,
    onTogglePaddleReminder: (Boolean) -> Unit,
    onSetSimulationSpeed: (Int) -> Unit,
    onTestNotification: () -> Unit,
    onUpdateConverterSettings: (ConverterSettings) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isZojirushi = telemetry.model.isZojirushi
    val currentModel = telemetry.model

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("machine_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Machine Model Selector (Zojirushi Lineup & Universal)
        var selectedModelFilter by remember { mutableStateOf("All") }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bread Maker Machine Model",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = if (isZojirushi) "Zojirushi Ecosystem" else "Universal",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select your specific Zojirushi or standard machine to calibrate loaf capacity, blade mechanics, upper lid heating, and menu courses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Model Category Quick Filter Chips
                val filterCategories = listOf("All", "2-lb Dual Blade", "1-lb Compact", "Universal")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterCategories.forEach { category ->
                        val isSelected = selectedModelFilter == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedModelFilter = category },
                            label = {
                                val count = when (category) {
                                    "2-lb Dual Blade" -> BreadMakerModel.entries.count { it.bladeCount == 2 }
                                    "1-lb Compact" -> BreadMakerModel.entries.count { it.bladeCount == 1 && it.isZojirushi }
                                    "Universal" -> BreadMakerModel.entries.count { !it.isZojirushi }
                                    else -> BreadMakerModel.entries.size
                                }
                                Text("$category ($count)", style = MaterialTheme.typography.labelSmall)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val filteredModels = BreadMakerModel.entries.filter { model ->
                    when (selectedModelFilter) {
                        "2-lb Dual Blade" -> model.bladeCount == 2
                        "1-lb Compact" -> model.bladeCount == 1 && model.isZojirushi
                        "Universal" -> !model.isZojirushi
                        else -> true
                    }
                }

                // Scrollable model selector cards
                val modelScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(modelScrollState),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredModels.forEach { model ->
                        val isSelected = telemetry.model == model
                        Surface(
                            modifier = Modifier
                                .width(235.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectModel(model) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            shadowElevation = if (isSelected) 3.dp else 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = model.modelCode,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Active",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = model.shortName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${model.defaultLoafCapacity} • ${model.bladeCount} Blade${if (model.bladeCount > 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Max: ${model.maxFlourGrams}g flour • ${model.menuCourseCount} Courses",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (model.hasLidHeater) "Lid Heater" else "Base Element",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    if (model.hasAutoDispenser) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = "Auto Dispenser",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connection details & description
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bluetooth,
                                    contentDescription = "Bluetooth",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = telemetry.machineName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${telemetry.model.panType} • ${if (telemetry.isConnected) "Telemetry Linked" else "Disconnected"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onToggleConnection,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (telemetry.isConnected) "Disconnect" else "Connect")
                            }
                        }

                        if (currentModel.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentModel.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Zojirushi Feature Panel: Menu Courses, Homemade Memory, & Hardware Diagnostics
        if (isZojirushi) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Zojirushi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zojirushi ${currentModel.modelCode} Courses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = currentModel.modelCode,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    val modelCourses = currentModel.getCourses()
                    var selectedCourseNumber by remember(currentModel) {
                        mutableStateOf(currentModel.mapRecommendedCourse(telemetry.zojirushiMenuCourse).courseNumber)
                    }
                    val activeCourse = modelCourses.find { it.courseNumber == selectedCourseNumber } ?: modelCourses.first()

                    Text(
                        text = "Active Course: #${activeCourse.courseNumber} ${activeCourse.courseName} — ${activeCourse.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Scrollable Horizontal Course Selector
                    val courseScroll = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(courseScroll),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        modelCourses.forEach { course ->
                            val isSelected = activeCourse.courseNumber == course.courseNumber
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCourseNumber = course.courseNumber
                                    // Map to nearest standard enum for telemetry sync
                                    val mappedStandard = ZojirushiMenuCourse.entries.find { it.courseNumber == course.courseNumber }
                                        ?: ZojirushiMenuCourse.WHITE
                                    onSelectZojirushiCourse(mappedStandard)
                                },
                                label = {
                                    Text(
                                        text = "${course.courseNumber}. ${course.courseName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Home Made Custom Memory Slots
                    if (currentModel.hasHomemadeMemory && currentModel.memorySlotCount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Memory, contentDescription = "Memory Slots", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Homemade Memory Banks:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                (1..currentModel.memorySlotCount).forEach { slot ->
                                    val isSelected = telemetry.homemadeMemorySlot == slot
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onSelectMemorySlot(slot) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "Mem $slot",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Hardware Specs Diagnostics for current model
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            DiagnosticRow("Series Lineup", currentModel.series)
                            DiagnosticRow("Kneading Mechanism", "${currentModel.bladeCount} ${if (currentModel.bladeCount == 2) "Synchronous Dual Blades (Left & Right)" else "High-Torque Single Blade"}")
                            DiagnosticRow("Heating System", if (currentModel.hasLidHeater) "Dual Heating: Base Element + Upper Lid Browning Element" else "High-Efficiency Base Heating Element")
                            DiagnosticRow("Pan Configuration", currentModel.panType)
                            DiagnosticRow("Loaf Capacity Target", currentModel.defaultLoafCapacity)
                            DiagnosticRow("Flour Weight Limit", "Up to ${currentModel.maxFlourGrams}g flour")
                            DiagnosticRow("Ingredient Dispenser", if (currentModel.hasAutoDispenser) "Automated Dry & Nut/Seed Dispenser" else "Manual Addition Alert / Chime")
                            DiagnosticRow("Specialized Hardware", currentModel.specializedFeature)
                            DiagnosticRow("Menu Programs", "${currentModel.menuCourseCount} Preprogrammed Courses")
                        }
                    }
                }
            }
        }

        // Baker's Measurement Converter (Grams to mL, Cups to Grams, Hydration & Preferences)
        BakerMeasurementConverterCard(
            settings = telemetry.converterSettings,
            onUpdateSettings = onUpdateConverterSettings,
            modifier = Modifier.fillMaxWidth()
        )

        // Notification & Twin Paddle Alert Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Alerts & Paddle Extraction Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.Restaurant, contentDescription = "Paddle", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (telemetry.model.bladeCount == 2) "Twin Paddles Extraction Alert" else "Paddle Removal Reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (telemetry.model.bladeCount == 2) {
                                    "Double-beep alert before final rise to remove both blades from rectangular loaf pan"
                                } else {
                                    "Pause at punch-down stage so you can extract the kneading blade from loaf base"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = telemetry.paddleRemovalReminderEnabled,
                        onCheckedChange = onTogglePaddleReminder
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = "Chime", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Push Notifications for Add-ins", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Buzz phone when fruit, nut, butter or scoring stage arrives", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Button(
                        onClick = onTestNotification,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Test Chime")
                    }
                }
            }
        }

        // Simulation Speed for Demonstrations
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulation Speed Controller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Accelerate timer ticker to preview full Zojirushi BB-PDC20 cycles in seconds:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1, 10, 30, 60).forEach { speed ->
                        val isSelected = telemetry.simulationSpeed == speed
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetSimulationSpeed(speed) },
                            label = { Text("${speed}x", fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Maintenance & Care Tips for Current Model
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CleaningServices, contentDescription = "Care", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isZojirushi) "Zojirushi ${currentModel.modelCode} Maintenance" else "Bread Maker Maintenance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isZojirushi) {
                    if (currentModel.bladeCount == 2) {
                        Text("• Soak twin kneading blades in warm water before washing; never use abrasive scouring pads on nonstick coating.", style = MaterialTheme.typography.bodySmall)
                        Text("• Ensure dual drive wing shafts on bottom of pan rotate freely before clicking pan into base.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("• Soak the single kneading blade in warm water before washing; never use abrasive scourers.", style = MaterialTheme.typography.bodySmall)
                        Text("• Inspect the center drive gear beneath the 1.0 lb pan before seating into the heating chamber.", style = MaterialTheme.typography.bodySmall)
                    }
                    if (currentModel.hasLidHeater) {
                        Text("• Wipe the upper lid heating element with a damp cloth after cooling to keep radiant heat uniform.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("• Keep the observation lid window and inner chamber clean of flour dust.", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("• Lightly coat kneading blade(s) with neutral baking oil before loading flour to facilitate clean loaf release.", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("• Never use metal scourers on the non-stick bread pan.", style = MaterialTheme.typography.bodySmall)
                    Text("• Apply food-grade oil to the kneading drive shaft quarterly.", style = MaterialTheme.typography.bodySmall)
                    Text("• Clean the upper radiant glass element with a damp cloth after cool-down.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Offline Mode & Local Storage Architecture Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("offline_storage_status_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Local Storage",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Offline Mode & Local Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "100% LOCAL READY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CrustCraft operates completely offline without requiring an active internet connection. All core application components reside locally on your device:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Room Database: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text("All standard & custom recipes, favorites, and bake counts stored on-device.", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Offline Diagnostic KB: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text("12+ failure modes & remedies available when disconnected from Gemini.", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Loaf Photos: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text("Captured images are saved directly in app private internal storage.", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Timers & Hardware: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Text("All countdowns, alarms, and simulation logic execute locally.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Official Instruction Manual & Recipe Book Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("official_manual_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Manual",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isZojirushi) "Official ${currentModel.shortName} Manual" else "Official Machine Manual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isZojirushi) {
                        "Official ${currentModel.modelName} instruction manual, course guidelines, and official recipe book integrated into CrustCraft. Model specifications: ${currentModel.panType}, ${currentModel.bladeCount} blade(s), ${if (currentModel.hasLidHeater) "dual heaters" else "base element"}."
                    } else {
                        "Standard manufacturer instruction guidebook for electric breadmakers with automatic cycles and programmable settings."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val manualUrl = currentModel.manualUrl
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(manualUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_manual_url_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open Web Manual",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Official ${currentModel.modelCode} Manual")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
