package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BakingConverter
import com.example.data.model.ConverterSettings
import com.example.data.model.FlourCupDensity
import com.example.data.model.LiquidDensityItem
import com.example.data.model.SpoonMeasurement
import com.example.data.model.UnitSystem
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BakerMeasurementConverterCard(
    settings: ConverterSettings,
    onUpdateSettings: (ConverterSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Grams ↔ Ounces", "Recipe Scaler", "Grams ↔ mL", "Cups ↔ Grams", "Spoons & Yeast", "Hydration %")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("measurement_converter_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Scale,
                            contentDescription = "Converter",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Baker's Measurement Converter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Precision density converter, grams to ounces & recipe scaling",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs (Scrollable for full touch targets)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                0 -> BakingUnitConverterSection()
                1 -> RecipeScalerSection()
                2 -> GramsToMlConverterSection(
                    defaultLiquid = settings.defaultLiquid,
                    cupStandardMl = settings.cupStandardMl
                )
                3 -> CupsToGramsConverterSection(
                    cupStandardMl = settings.cupStandardMl
                )
                4 -> SpoonsAndYeastConverterSection()
                5 -> HydrationCalculatorSection()
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Converter Settings & Preferences Section
            ConverterPreferencesSection(
                settings = settings,
                onUpdateSettings = onUpdateSettings
            )
        }
    }
}

/**
 * Grams to Milliliters (mL) and Milliliters to Grams converter with ingredient density
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GramsToMlConverterSection(
    defaultLiquid: LiquidDensityItem,
    cupStandardMl: Double,
    modifier: Modifier = Modifier
) {
    var isGramsToMl by remember { mutableStateOf(true) }
    var selectedLiquid by remember { mutableStateOf(defaultLiquid) }
    var inputValueStr by remember { mutableStateOf("240") }
    val clipboardManager = LocalClipboardManager.current
    var copiedFeedback by remember { mutableStateOf(false) }

    val inputValue = inputValueStr.toDoubleOrNull() ?: 0.0

    val calculatedResult = if (isGramsToMl) {
        BakingConverter.gramsToMl(inputValue, selectedLiquid)
    } else {
        BakingConverter.mlToGrams(inputValue, selectedLiquid)
    }

    val approximateCups = if (isGramsToMl) {
        calculatedResult / cupStandardMl
    } else {
        inputValue / cupStandardMl
    }

    val approximateFlOz = if (isGramsToMl) {
        calculatedResult / 29.5735
    } else {
        inputValue / 29.5735
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Direction toggle: Grams -> mL vs mL -> Grams
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier
                    .weight(1f)
                    .clickable { isGramsToMl = !isGramsToMl }
                    .testTag("converter_direction_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap conversion direction",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGramsToMl) "Direction: Grams (g) ➔ Milliliters (mL)" else "Direction: Milliliters (mL) ➔ Grams (g)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Liquid Selection Chips
        Text(
            text = "Select Liquid Ingredient:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LiquidDensityItem.entries.forEach { liquid ->
                val isSelected = selectedLiquid == liquid
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLiquid = liquid },
                    label = {
                        Text(
                            text = liquid.displayName.substringBefore(" ("),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.testTag("liquid_chip_${liquid.name.lowercase(Locale.ROOT)}")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Field & Quick Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = inputValueStr,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                        inputValueStr = newValue
                    }
                },
                label = {
                    Text(if (isGramsToMl) "Amount in Grams (g)" else "Amount in Milliliters (mL)")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("converter_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // Step adjustment buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = {
                        val current = inputValueStr.toDoubleOrNull() ?: 0.0
                        inputValueStr = BakingConverter.formatDouble((current - 10).coerceAtLeast(0.0))
                    },
                    modifier = Modifier.size(44.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text("-10", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val current = inputValueStr.toDoubleOrNull() ?: 0.0
                        inputValueStr = BakingConverter.formatDouble(current + 10)
                    },
                    modifier = Modifier.size(44.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text("+10", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Preset Chips for Bread Baking
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val presets = if (isGramsToMl) {
                listOf(50.0, 100.0, 150.0, 240.0, 360.0, 500.0)
            } else {
                listOf(50.0, 100.0, 150.0, 240.0, 360.0, 500.0)
            }

            presets.forEach { presetVal ->
                val label = if (presetVal == 240.0) {
                    "240 (1 cup)"
                } else if (presetVal == 360.0) {
                    "360 (Zojirushi 2-lb)"
                } else {
                    "${presetVal.toInt()}${if (isGramsToMl) "g" else "mL"}"
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable {
                            inputValueStr = BakingConverter.formatDouble(presetVal)
                        }
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Result Card (Hero Output)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("converter_result_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Opacity,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Converted Result:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Copy Button
                    IconButton(
                        onClick = {
                            val copyText = if (isGramsToMl) {
                                "${BakingConverter.formatDouble(inputValue)} g ${selectedLiquid.displayName} = ${BakingConverter.formatDouble(calculatedResult)} mL"
                            } else {
                                "${BakingConverter.formatDouble(inputValue)} mL ${selectedLiquid.displayName} = ${BakingConverter.formatDouble(calculatedResult)} g"
                            }
                            clipboardManager.setText(AnnotatedString(copyText))
                            copiedFeedback = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Big Result Display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = BakingConverter.formatDouble(calculatedResult),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGramsToMl) "mL" else "grams",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Volume & Imperial Equivalents
                Text(
                    text = "≈ ${BakingConverter.formatDouble(approximateCups)} US Cups • ${BakingConverter.formatDouble(approximateFlOz)} fl oz",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Formula Breakdown Note
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isGramsToMl) {
                                "Formula: ${BakingConverter.formatDouble(inputValue)} g ÷ ${selectedLiquid.densityGramsPerMl} g/mL density = ${BakingConverter.formatDouble(calculatedResult)} mL"
                            } else {
                                "Formula: ${BakingConverter.formatDouble(inputValue)} mL × ${selectedLiquid.densityGramsPerMl} g/mL density = ${BakingConverter.formatDouble(calculatedResult)} g"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedLiquid.note,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Cups to Grams and Grams to Cups converter for flours & dry ingredients
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CupsToGramsConverterSection(
    cupStandardMl: Double,
    modifier: Modifier = Modifier
) {
    var isCupsToGrams by remember { mutableStateOf(true) }
    var selectedFlour by remember { mutableStateOf(FlourCupDensity.BREAD_FLOUR) }
    var inputValueStr by remember { mutableStateOf("3") }

    val inputValue = inputValueStr.toDoubleOrNull() ?: 0.0

    val calculatedResult = if (isCupsToGrams) {
        BakingConverter.cupsToGrams(inputValue, selectedFlour)
    } else {
        BakingConverter.gramsToCups(inputValue, selectedFlour)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isCupsToGrams = !isCupsToGrams }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCupsToGrams) "Direction: Cups ➔ Grams (g)" else "Direction: Grams (g) ➔ Cups",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select Flour / Dry Ingredient:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FlourCupDensity.entries.forEach { flour ->
                val isSelected = selectedFlour == flour
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFlour = flour },
                    label = {
                        Text(
                            text = flour.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Field
        OutlinedTextField(
            value = inputValueStr,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                    inputValueStr = newValue
                }
            },
            label = {
                Text(if (isCupsToGrams) "Volume in Cups" else "Weight in Grams (g)")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Cup Presets
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val cupPresets = listOf("0.25", "0.5", "1.0", "1.5", "2.0", "3.0", "4.0")
            cupPresets.forEach { preset ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        if (isCupsToGrams) {
                            inputValueStr = preset
                        } else {
                            val cups = preset.toDouble()
                            inputValueStr = BakingConverter.formatDouble(cups * selectedFlour.gramsPerCup)
                        }
                    }
                ) {
                    Text(
                        text = "$preset cup${if (preset != "1.0") "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Converted Result:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = BakingConverter.formatDouble(calculatedResult),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCupsToGrams) "grams (g)" else "cups",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Density: ${selectedFlour.gramsPerCup.toInt()}g per standard cup • ${selectedFlour.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Spoons & Yeast Conversion Table & Calculator
 */
@Composable
fun SpoonsAndYeastConverterSection(
    modifier: Modifier = Modifier
) {
    var selectedSpoon by remember { mutableStateOf(SpoonMeasurement.INSTANT_YEAST) }
    var spoonCountStr by remember { mutableStateOf("1.5") }
    var isTablespoon by remember { mutableStateOf(false) }

    val spoonCount = spoonCountStr.toDoubleOrNull() ?: 0.0
    val gramsPerUnit = if (isTablespoon) selectedSpoon.gramsPerTbsp else selectedSpoon.gramsPerTsp
    val totalGrams = spoonCount * gramsPerUnit

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Small Measure & Leavening Converter:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Selector for ingredient
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                SpoonMeasurement.INSTANT_YEAST,
                SpoonMeasurement.ACTIVE_DRY_YEAST,
                SpoonMeasurement.TABLE_SALT,
                SpoonMeasurement.FINE_SEA_SALT
            ).forEach { item ->
                val isSelected = selectedSpoon == item
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSpoon = item },
                    label = {
                        Text(
                            text = item.displayName.substringBefore(" ("),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle tsp vs tbsp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isTablespoon = false },
                color = if (!isTablespoon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Teaspoons (tsp / 5 mL)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (!isTablespoon) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isTablespoon = true },
                color = if (isTablespoon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Tablespoons (tbsp / 15 mL)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isTablespoon) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = spoonCountStr,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("""^\d*\.?\d*$"""))) {
                    spoonCountStr = newValue
                }
            },
            label = {
                Text("Number of ${if (isTablespoon) "Tablespoons" else "Teaspoons"}")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weight in Grams:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = BakingConverter.formatDouble(totalGrams),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "grams (g)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reference: ${selectedSpoon.note}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (selectedSpoon == SpoonMeasurement.INSTANT_YEAST || selectedSpoon == SpoonMeasurement.ACTIVE_DRY_YEAST) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Yeast Ratio: 1g Instant Yeast = 1.25g Active Dry = 3.0g Fresh Cake Yeast",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Dough Hydration Percentage Calculator
 */
@Composable
fun HydrationCalculatorSection(
    modifier: Modifier = Modifier
) {
    var flourGramsStr by remember { mutableStateOf("500") }
    var liquidGramsStr by remember { mutableStateOf("360") }

    val flourGrams = flourGramsStr.toDoubleOrNull() ?: 0.0
    val liquidGrams = liquidGramsStr.toDoubleOrNull() ?: 0.0

    val hydration = BakingConverter.calculateHydration(flourGrams, liquidGrams)

    val (styleTitle, styleDesc, colorTone) = when {
        hydration <= 0.0 -> Triple("Enter values", "Input flour and water to calculate hydration", MaterialTheme.colorScheme.onSurfaceVariant)
        hydration < 60.0 -> Triple("Stiff Dough (<60%)", "Dense crumb: Bagels, pretzels, stiff noodles", MaterialTheme.colorScheme.tertiary)
        hydration <= 68.0 -> Triple("Standard Sandwich Bread (60-68%)", "Soft, tender, sliceable crumb. Ideal for Zojirushi BB-PDC20 twin blade loaf", MaterialTheme.colorScheme.primary)
        hydration <= 75.0 -> Triple("Open Crumb Artisan Sourdough (68-75%)", "Crispy blistered crust with irregular open airy holes", MaterialTheme.colorScheme.primary)
        else -> Triple("High Hydration Rustic (>75%)", "Ciabatta, focaccia, pan de cristal with custard-like interior", MaterialTheme.colorScheme.secondary)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = flourGramsStr,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) flourGramsStr = it },
                label = { Text("Flour Weight (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = liquidGramsStr,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) liquidGramsStr = it },
                label = { Text("Water / Liquid (g / mL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Baker's Hydration Percentage:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (hydration > 0.0) "${BakingConverter.formatDouble(hydration)}%" else "--%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = styleTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorTone,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = styleDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Settings & Preference Controls for the Converter
 */
@Composable
fun ConverterPreferencesSection(
    settings: ConverterSettings,
    onUpdateSettings: (ConverterSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedCheatSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Converter Preferences & Defaults",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preferred Unit System Selector
        Text(
            text = "Preferred Unit Display Standard:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UnitSystem.entries.forEach { unitSys ->
                val isSelected = settings.preferredUnitSystem == unitSys
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            onUpdateSettings(settings.copy(preferredUnitSystem = unitSys))
                        },
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = unitSys.shortLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = unitSys.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Default Liquid Setting for Grams to mL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Default Liquid Density",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Current: ${settings.defaultLiquid.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    LiquidDensityItem.WATER,
                    LiquidDensityItem.WHOLE_MILK,
                    LiquidDensityItem.SOURDOUGH_STARTER
                ).forEach { liquid ->
                    val isSelected = settings.defaultLiquid == liquid
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateSettings(settings.copy(defaultLiquid = liquid)) },
                        label = {
                            Text(
                                text = liquid.name.split("_").first().lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expandable Cheat Sheet Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expandedCheatSheet = !expandedCheatSheet },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Baker's Quick Reference Table (Cheat Sheet)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = if (expandedCheatSheet) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Expandable Cheat Sheet Content
        AnimatedVisibility(visible = expandedCheatSheet) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                ConversionTableCard()
            }
        }
    }
}

/**
 * Static Quick Reference Cheat Sheet Card
 */
@Composable
fun ConversionTableCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Essential Bread Machine Equivalents:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Table Rows
            CheatSheetRow("Water", "100 g = 100 mL", "1 cup = 240 mL = 240 g")
            CheatSheetRow("Whole Milk", "100 g = 97.1 mL", "1 cup = 240 mL = 247 g")
            CheatSheetRow("Vegetable Oil", "100 g = 108.7 mL", "1 tbsp = 15 mL = 13.8 g")
            CheatSheetRow("Honey", "100 g = 70.4 mL", "1 tbsp = 15 mL = 21.3 g")
            CheatSheetRow("Bread Flour", "1 cup = 127 g", "3 cups = 381 g (approx 2-lb loaf)")
            CheatSheetRow("All-Purpose Flour", "1 cup = 120 g", "3 cups = 360 g")
            CheatSheetRow("Instant Yeast", "1 tsp = 3.1 g", "1 packet = 2¼ tsp = 7.0 g")
            CheatSheetRow("Table Salt", "1 tsp = 5.7 g", "1 tbsp = 17.1 g")
            CheatSheetRow("Butter", "1 stick = 8 tbsp = 113.5 g", "1 cup = 2 sticks = 227 g")
        }
    }
}

@Composable
private fun CheatSheetRow(
    item: String,
    col1: String,
    col2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.1f)
        )
        Text(
            text = col1,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.3f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = col2,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.6f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
