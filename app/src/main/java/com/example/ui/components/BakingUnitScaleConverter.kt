package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BakingConverter
import com.example.data.model.BreadRecipe
import com.example.data.model.FlourCupDensity
import com.example.data.model.LiquidDensityItem
import com.example.data.model.RecipeIngredient
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.ProofingBlue
import java.util.Locale

/**
 * Common ingredients used for unit weight and volume density conversions.
 */
enum class CommonBakingItem(
    val displayName: String,
    val gramsPerCup: Double,
    val gramsPerTbsp: Double,
    val isLiquid: Boolean,
    val emoji: String
) {
    BREAD_FLOUR("Bread Flour", 127.0, 7.9, false, "🌾"),
    ALL_PURPOSE_FLOUR("All-Purpose Flour", 120.0, 7.5, false, "🌾"),
    WHOLE_WHEAT("Whole Wheat Flour", 120.0, 7.5, false, "🌾"),
    RYE_FLOUR("Dark Rye Flour", 102.0, 6.4, false, "🌾"),
    SPELT_FLOUR("Ancient Spelt", 115.0, 7.2, false, "🌾"),
    WATER("Water / Milk", 240.0, 15.0, true, "💧"),
    SOURDOUGH_STARTER("Sourdough Starter (100%)", 245.0, 15.3, true, "🫧"),
    HONEY("Pure Honey", 340.0, 21.3, true, "🍯"),
    OLIVE_OIL("Olive Oil / Butter", 218.0, 13.8, true, "🫒"),
    FINE_SALT("Fine Sea Salt", 270.0, 17.0, false, "🧂"),
    INSTANT_YEAST("Instant Yeast", 149.0, 9.3, false, "🍞"),
    WHITE_SUGAR("Granulated Sugar", 200.0, 12.5, false, "🍬")
}

/**
 * Standard baseline sourdough template for generic recipe scaling when no recipe is provided.
 */
private val DEFAULT_SCALING_INGREDIENTS = listOf(
    RecipeIngredient("di1", "Unbleached Bread Flour", "450 g", 100f),
    RecipeIngredient("di2", "Water (lukewarm 80°F)", "315 ml", 70f),
    RecipeIngredient("di3", "Active Sourdough Levain", "110 g", 24.4f),
    RecipeIngredient("di4", "Fine Sea Salt", "9 g", 2.0f),
    RecipeIngredient("di5", "Extra Virgin Olive Oil", "14 g", 3.1f)
)

/**
 * A utility UI component for converting baking units (grams to ounces, pounds, mL, etc.)
 * and assisting with interactive recipe scaling.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BakingUnitScaleConverter(
    recipe: BreadRecipe? = null,
    modifier: Modifier = Modifier,
    initialTab: Int = 0,
    onClose: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Unit Converter (g ↔ oz)", "Recipe Scaler (Pan Sizing)")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("baking_unit_scale_converter"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                            contentDescription = "Baking Converter",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Baking Scale & Unit Utility",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (recipe != null) "Scaling: ${recipe.title}" else "Precision artisan conversions & yield scaling",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (onClose != null) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Close"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("converter_utility_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> BakingUnitConverterSection()
                1 -> RecipeScalerSection(recipe = recipe)
            }
        }
    }
}

/**
 * Section for converting baking weight and volume units:
 * Grams (g) ↔ Ounces (oz), Pounds (lb), Kilograms (kg), Cups, and Spoons.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BakingUnitConverterSection(modifier: Modifier = Modifier) {
    val clipboardManager = LocalClipboardManager.current
    var isGramsToOunces by remember { mutableStateOf(true) }
    var inputValue by remember { mutableStateOf("450") }
    var selectedItem by remember { mutableStateOf(CommonBakingItem.BREAD_FLOUR) }
    var copiedFeedback by remember { mutableStateOf(false) }

    val numericInput = inputValue.toDoubleOrNull() ?: 0.0

    // Calculations
    val grams: Double
    val ounces: Double
    val pounds: Double

    if (isGramsToOunces) {
        grams = numericInput
        ounces = BakingConverter.gramsToOunces(grams)
        pounds = BakingConverter.gramsToPounds(grams)
    } else {
        ounces = numericInput
        grams = BakingConverter.ouncesToGrams(ounces)
        pounds = ounces / 16.0
    }

    // Volume approximations based on ingredient density
    val approximateCups = if (selectedItem.gramsPerCup > 0) grams / selectedItem.gramsPerCup else 0.0
    val approximateTbsp = if (selectedItem.gramsPerTbsp > 0) grams / selectedItem.gramsPerTbsp else 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("unit_converter_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Direction Toggle & Info Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap conversion direction",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGramsToOunces) "Mode: Grams ➔ Ounces & Pounds" else "Mode: Ounces ➔ Grams & Pounds",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(
                    onClick = {
                        isGramsToOunces = !isGramsToOunces
                        inputValue = if (isGramsToOunces) {
                            BakingConverter.formatDouble(grams, 0)
                        } else {
                            BakingConverter.formatDouble(ounces, 1)
                        }
                    },
                    modifier = Modifier.testTag("toggle_conversion_direction_btn")
                ) {
                    Text("Switch Direction", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Ingredient Density Selector Chips
        Column {
            Text(
                text = "Select Ingredient Density for Volume Equivalence:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CommonBakingItem.entries.forEach { item ->
                    val isSelected = selectedItem == item
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedItem = item },
                        leadingIcon = { Text(item.emoji, fontSize = 12.sp) },
                        label = {
                            Text(
                                text = item.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // Input Field with Unit Suffix
        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = {
                Text(if (isGramsToOunces) "Enter Weight in Grams (g) *" else "Enter Weight in Ounces (oz) *")
            },
            placeholder = { Text(if (isGramsToOunces) "e.g. 450" else "e.g. 16.0") },
            trailingIcon = {
                Text(
                    text = if (isGramsToOunces) "grams (g)" else "ounces (oz)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unit_converter_input")
        )

        // Quick Preset Buttons
        Column {
            Text(
                text = if (isGramsToOunces) "Standard Bread Pan & Batch Presets (Grams):" else "Standard US Imperial Presets (Ounces):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGramsToOunces) {
                    listOf(
                        "50g" to "50",
                        "100g" to "100",
                        "250g" to "250",
                        "400g" to "400",
                        "450g (1 lb loaf)" to "450",
                        "500g" to "500",
                        "680g (1.5 lb)" to "680",
                        "900g (2 lb)" to "900",
                        "1000g (1 kg)" to "1000"
                    ).forEach { (label, value) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { inputValue = value }
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    listOf(
                        "1.0 oz" to "1.0",
                        "4.0 oz (¼ lb)" to "4.0",
                        "8.0 oz (½ lb)" to "8.0",
                        "12.0 oz" to "12.0",
                        "16.0 oz (1 lb loaf)" to "16.0",
                        "24.0 oz (1.5 lb)" to "24.0",
                        "32.0 oz (2 lb loaf)" to "32.0"
                    ).forEach { (label, value) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { inputValue = value }
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Live Conversion Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("conversion_result_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Converted Baking Equivalents",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = {
                            val copyText = "${BakingConverter.formatDouble(grams, 1)} g = ${BakingConverter.formatDouble(ounces, 2)} oz (${BakingConverter.formatDouble(pounds, 2)} lb) • ${selectedItem.displayName}"
                            clipboardManager.setText(AnnotatedString(copyText))
                            copiedFeedback = true
                        },
                        modifier = Modifier.testTag("copy_conversion_btn")
                    ) {
                        Icon(
                            imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Weight Row (Ounces & Grams)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "WEIGHT IN OUNCES",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BreadCrustGolden
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.2f oz", ounces),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "≈ ${String.format(Locale.US, "%.2f", pounds)} lbs",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "WEIGHT IN GRAMS",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProofingBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f g", grams),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "≈ ${String.format(Locale.US, "%.3f", grams / 1000.0)} kg",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Volume Approximation Row (Cups & Spoons)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VOLUME FOR ${selectedItem.displayName.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "≈ ${String.format(Locale.US, "%.2f", approximateCups)} Cups (US Legal)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "≈ ${String.format(Locale.US, "%.1f", approximateTbsp)} tbsp",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section for interactive recipe scaling:
 * Allows scaling an entire loaf recipe between 0.5x, 0.75x, 1.0x, 1.33x, 1.5x, 2.0x,
 * or entering a custom target flour weight / batch multiplier.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeScalerSection(
    recipe: BreadRecipe? = null,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var scaleMultiplier by remember { mutableDoubleStateOf(1.0) }
    var scaleModeIndex by remember { mutableIntStateOf(0) } // 0: By Multiplier, 1: By Target Pan Size
    var customMultiplierInput by remember { mutableStateOf("1.0") }
    var copiedFormulaFeedback by remember { mutableStateOf(false) }

    // Ingredients to scale
    val baseIngredients = recipe?.ingredients ?: DEFAULT_SCALING_INGREDIENTS

    // Preset multipliers with standard loaf sizes
    val presets = listOf(
        Triple("0.5x", 0.5, "Half / 1.0 lb loaf"),
        Triple("0.75x", 0.75, "1.5 lb small"),
        Triple("1.0x", 1.0, "Original (Standard)"),
        Triple("1.33x", 1.33, "2.0 lb traditional"),
        Triple("1.5x", 1.5, "2.2 lb large batch"),
        Triple("2.0x", 2.0, "Double batch (2 loaves)")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recipe_scaler_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Recipe Context Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (recipe != null) "Scaling Active Recipe: ${recipe.title}" else "Scaling Standard Artisan Formula (Bread Flour 100%)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Current Scale: ${String.format(Locale.US, "%.2f", scaleMultiplier)}x yield multiplier",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", scaleMultiplier)}x",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Multiplier Presets
        Column {
            Text(
                text = "Quick Batch Yield Presets:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { (label, factor, description) ->
                    val isSelected = Math.abs(scaleMultiplier - factor) < 0.01
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            scaleMultiplier = factor
                            customMultiplierInput = String.format(Locale.US, "%.2f", factor)
                        },
                        label = {
                            Column {
                                Text(label, fontWeight = FontWeight.Bold)
                                Text(description, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("preset_scale_${label.replace(".", "_")}")
                    )
                }
            }
        }

        // Interactive Scale Multiplier Slider & Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fine-Tune Scale Multiplier:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", scaleMultiplier)}x",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = scaleMultiplier.toFloat(),
                    onValueChange = {
                        scaleMultiplier = it.toDouble()
                        customMultiplierInput = String.format(Locale.US, "%.2f", it)
                    },
                    valueRange = 0.25f..3.0f,
                    steps = 55,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scale_factor_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0.25x (Quarter)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                    Text("1.0x (Standard)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                    Text("3.0x (Triple)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                }
            }
        }

        // Scaled Ingredients Table Header & Copy Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scaled Ingredients (${baseIngredients.size} Items):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {
                    val builder = StringBuilder()
                    builder.append("Scaled Recipe Formula (${String.format(Locale.US, "%.2f", scaleMultiplier)}x):\n")
                    baseIngredients.forEach { ing ->
                        val (scaledGrams, scaledOunces, scaledFormatted) = calculateScaledValues(ing.amount, scaleMultiplier)
                        builder.append("• ${ing.name}: $scaledFormatted (${String.format(Locale.US, "%.2f", scaledOunces)} oz)\n")
                    }
                    clipboardManager.setText(AnnotatedString(builder.toString()))
                    copiedFormulaFeedback = true
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("copy_scaled_recipe_btn")
            ) {
                Icon(
                    imageVector = if (copiedFormulaFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy formula",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (copiedFormulaFeedback) "Copied!" else "Copy Formula", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Scaled Ingredients Card List
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scaled_ingredients_list_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                baseIngredients.forEachIndexed { index, ingredient ->
                    val (scaledGrams, scaledOunces, scaledFormatted) = calculateScaledValues(ingredient.amount, scaleMultiplier)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ingredient.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Orig: ${ingredient.amount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (ingredient.bakerPercentage > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${ingredient.bakerPercentage.toInt()}% Baker's",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Scaled Quantities in Grams and Ounces
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = scaledFormatted,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = String.format(Locale.US, "≈ %.2f oz", scaledOunces),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = BreadCrustGolden
                            )
                        }
                    }

                    if (index < baseIngredients.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates scaled weight values in grams and ounces for an ingredient amount string.
 * Example: "450 g" scaled by 1.5 -> (675.0 g, 23.81 oz, "675 g")
 * Example: "280 ml" scaled by 1.5 -> (420.0 g, 14.82 oz, "420 ml")
 * Example: "2 tbsp" scaled by 2.0 -> (30.0 g, 1.06 oz, "4.0 tbsp")
 */
private fun calculateScaledValues(
    rawAmount: String,
    scaleFactor: Double
): Triple<Double, Double, String> {
    val clean = rawAmount.trim()
    val regex = Regex("""^([\d.,]+)\s*([a-zA-Z%]+)?.*$""")
    val match = regex.find(clean)

    if (match != null) {
        val num = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
        val unit = match.groupValues.getOrNull(2)?.lowercase(Locale.US) ?: ""
        val scaledNum = num * scaleFactor

        return when (unit) {
            "g", "gram", "grams" -> {
                val oz = BakingConverter.gramsToOunces(scaledNum)
                Triple(scaledNum, oz, "${BakingConverter.formatDouble(scaledNum, 0)} g")
            }
            "ml" -> {
                val oz = BakingConverter.gramsToOunces(scaledNum) // approx water density
                Triple(scaledNum, oz, "${BakingConverter.formatDouble(scaledNum, 0)} ml")
            }
            "oz", "ounce", "ounces" -> {
                val g = BakingConverter.ouncesToGrams(scaledNum)
                Triple(g, scaledNum, "${BakingConverter.formatDouble(scaledNum, 2)} oz")
            }
            "tbsp" -> {
                val g = scaledNum * 15.0 // approx standard tbsp
                val oz = BakingConverter.gramsToOunces(g)
                Triple(g, oz, "${BakingConverter.formatDouble(scaledNum, 1)} tbsp")
            }
            "tsp" -> {
                val g = scaledNum * 5.0 // approx standard tsp
                val oz = BakingConverter.gramsToOunces(g)
                Triple(g, oz, "${BakingConverter.formatDouble(scaledNum, 1)} tsp")
            }
            "cup", "cups" -> {
                val g = scaledNum * 127.0 // standard flour cup
                val oz = BakingConverter.gramsToOunces(g)
                Triple(g, oz, "${BakingConverter.formatDouble(scaledNum, 2)} cups")
            }
            else -> {
                val oz = BakingConverter.gramsToOunces(scaledNum)
                Triple(scaledNum, oz, "${BakingConverter.formatDouble(scaledNum, 1)} $unit")
            }
        }
    }

    return Triple(0.0, 0.0, rawAmount)
}
