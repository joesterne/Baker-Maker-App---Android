package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import com.example.data.ai.BakingChatBotRepository
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreadMakerModel
import com.example.data.model.BreadRecipe
import com.example.data.model.BreadTroubleshootingKnowledgeBase
import com.example.data.model.CrustShade
import com.example.data.model.TroubleshootingIssue
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.ProofingBlue

@Composable
fun LoafTroubleshootingSection(
    recipe: BreadRecipe?,
    currentModel: BreadMakerModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20,
    onSaveNoteToRecipe: ((String) -> Unit)? = null,
    onApplyHydrationFix: ((Int) -> Unit)? = null,
    onApplyCrustFix: ((CrustShade) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var promptQuery by remember { mutableStateOf("") }
    var selectedIssue by remember { mutableStateOf<TroubleshootingIssue?>(null) }
    var noteSavedMessage by remember { mutableStateOf<String?>(null) }

    val matchingIssues = remember(promptQuery) {
        BreadTroubleshootingKnowledgeBase.diagnose(promptQuery)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loaf_troubleshooting_section"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with badge
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
                            .background(BakeFireOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Troubleshooting Doctor",
                            tint = BakeFireOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Loaf Doctor & Troubleshooting",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Analyze end-result issues & fix your formula",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = currentModel.shortName.substringBefore("(").trim(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-mode Switcher: AI Chatbot (Multimodal) vs Instant Symptom KnowledgeBase
            var diagnosticMode by remember { mutableStateOf(0) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    selected = diagnosticMode == 0,
                    onClick = { diagnosticMode = 0 },
                    shape = RoundedCornerShape(10.dp),
                    color = if (diagnosticMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                    border = if (diagnosticMode == 0) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)) else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = if (diagnosticMode == 0) BakeFireOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Chat & Photo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (diagnosticMode == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (diagnosticMode == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    selected = diagnosticMode == 1,
                    onClick = { diagnosticMode = 1 },
                    shape = RoundedCornerShape(10.dp),
                    color = if (diagnosticMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                    border = if (diagnosticMode == 1) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)) else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = if (diagnosticMode == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instant Diagnosis",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (diagnosticMode == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (diagnosticMode == 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (diagnosticMode == 0) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val chatRepo = remember { BakingChatBotRepository(context) }
                BakingChatBotSection(
                    chatRepository = chatRepo,
                    activeRecipe = recipe,
                    currentModel = currentModel
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
            // Troubleshooting Prompt Area TextField
            OutlinedTextField(
                value = promptQuery,
                onValueChange = {
                    promptQuery = it
                    noteSavedMessage = null
                    // If clearing, reset selected issue if it wasn't selected by chip
                    if (it.isBlank()) {
                        selectedIssue = null
                    } else {
                        // Auto-select best match
                        val match = BreadTroubleshootingKnowledgeBase.diagnose(it).firstOrNull()
                        if (match != null) {
                            selectedIssue = match
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("troubleshooting_prompt_input"),
                placeholder = {
                    Text(
                        text = "Describe issues with your loaf (e.g., sunken top, gummy crumb, pale crust, stuck paddle)...",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Prompt",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (promptQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            promptQuery = ""
                            selectedIssue = null
                            noteSavedMessage = null
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Symptom Chips
            Text(
                text = "OR SELECT COMMON END-RESULT SYMPTOM:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BreadTroubleshootingKnowledgeBase.ISSUES.forEach { issue ->
                    val isSelected = selectedIssue?.id == issue.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedIssue = if (isSelected) null else issue
                            noteSavedMessage = null
                        },
                        leadingIcon = {
                            Text(text = issue.iconEmoji, fontSize = 12.sp)
                        },
                        label = {
                            Text(
                                text = issue.symptom,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("symptom_chip_${issue.id}")
                    )
                }
            }

            // Display Diagnostic Details
            AnimatedVisibility(
                visible = selectedIssue != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                selectedIssue?.let { issue ->
                    Spacer(modifier = Modifier.height(14.dp))
                    DiagnosticResultCard(
                        issue = issue,
                        recipe = recipe,
                        currentModel = currentModel,
                        noteSavedMessage = noteSavedMessage,
                        onSaveNote = {
                            if (onSaveNoteToRecipe != null) {
                                onSaveNoteToRecipe("${issue.iconEmoji} ${issue.symptom}: ${issue.immediateFix.replace("\n", " ")}")
                                noteSavedMessage = "Saved troubleshooting diagnosis to recipe baker notes!"
                            }
                        },
                        onApplyHydration = onApplyHydrationFix,
                        onApplyCrust = onApplyCrustFix
                    )
                }
            }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticResultCard(
    issue: TroubleshootingIssue,
    recipe: BreadRecipe?,
    currentModel: BreadMakerModel,
    noteSavedMessage: String?,
    onSaveNote: () -> Unit,
    onApplyHydration: ((Int) -> Unit)?,
    onApplyCrust: ((CrustShade) -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("diagnostic_result_card_${issue.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Issue Title & Short description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = issue.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = issue.symptom,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = issue.shortDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Root Cause Section
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Root Cause",
                            tint = BakeFireOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROOT CAUSE ANALYSIS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BakeFireOrange,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = issue.primaryCause,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Actionable Fix for Next Bake
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "How to Fix",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RECIPE & INGREDIENT FIX",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = issue.immediateFix,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Machine & Hardware Specifics
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Machine Tuning",
                            tint = ProofingBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MACHINE TUNING (${currentModel.modelCode})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ProofingBlue,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = issue.machineTuning,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pro Tip Quote
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BreadCrustGolden.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tip",
                        tint = BreadCrustGolden,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = issue.tipQuote,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Note saved confirmation banner
            if (noteSavedMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = noteSavedMessage,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Apply Fixes and Save to Recipe Notes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // One-tap hydration fix
                if (recipe != null && issue.hydrationAdjustmentPercent != null && onApplyHydration != null) {
                    val currentHydration = recipe.hydrationPercent
                    val newHydration = (currentHydration + issue.hydrationAdjustmentPercent).coerceIn(50, 85)
                    Button(
                        onClick = { onApplyHydration(newHydration) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_hydration_fix_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Tune Hydration (${newHydration}%)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // One-tap crust shade fix
                if (issue.suggestedCrustChange != null && onApplyCrust != null) {
                    Button(
                        onClick = { onApplyCrust(issue.suggestedCrustChange) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_crust_fix_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BakeFireOrange
                        )
                    ) {
                        Text(
                            text = "Set Crust to ${issue.suggestedCrustChange.label}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Save Note to Recipe button
                OutlinedButton(
                    onClick = onSaveNote,
                    modifier = Modifier.testTag("save_troubleshooting_note_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Save Note",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save Note",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Modal BottomSheet allowing troubleshooting from the Dashboard or any other screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroubleshootingPromptSheet(
    recipe: BreadRecipe?,
    currentModel: BreadMakerModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20,
    onDismiss: () -> Unit,
    onSaveNoteToRecipe: ((String) -> Unit)? = null,
    onApplyHydrationFix: ((Int) -> Unit)? = null,
    onApplyCrustFix: ((CrustShade) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            LoafTroubleshootingSection(
                recipe = recipe,
                currentModel = currentModel,
                onSaveNoteToRecipe = onSaveNoteToRecipe,
                onApplyHydrationFix = onApplyHydrationFix,
                onApplyCrustFix = onApplyCrustFix
            )
        }
    }
}
