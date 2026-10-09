package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.BreadRecipe
import com.example.data.model.CrustShade
import com.example.data.model.LoafSize
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditorScreen(
    onBack: () -> Unit,
    onSaveRecipe: (BreadRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(RecipeCategory.SOURDOUGH) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var primaryFlour by remember { mutableStateOf("Unbleached Bread Flour") }
    var specialtyFloursInput by remember { mutableStateOf("Rye Flour (10%)") }
    var selectedYeast by remember { mutableStateOf(YeastType.SOURDOUGH_STARTER) }
    var yeastExpanded by remember { mutableStateOf(false) }

    var hydrationPercent by remember { mutableFloatStateOf(70f) }
    var selectedLoafSize by remember { mutableStateOf(LoafSize.ONE_HALF_LB) }
    var selectedCrust by remember { mutableStateOf(CrustShade.ARTISAN_CRISPY) }

    // Loaf Photo State
    var photoUri by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            photoUri = pendingCameraUri.toString()
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val loafDir = File(context.filesDir, "loaf_photos").apply { mkdirs() }
                val photoFile = File(loafDir, "loaf_custom_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                pendingCameraUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { sourceUri ->
            try {
                val loafDir = File(context.filesDir, "loaf_photos").apply { mkdirs() }
                val photoFile = File(loafDir, "loaf_custom_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    photoFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val localUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                photoUri = localUri.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                val loafDir = File(context.filesDir, "loaf_photos").apply { mkdirs() }
                val photoFile = File(loafDir, "loaf_custom_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                pendingCameraUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Ingredients list
    val ingredients = remember {
        mutableStateListOf(
            RecipeIngredient("i1", "Water (lukewarm)", "280 ml", 70f, BakingStageType.PREHEAT),
            RecipeIngredient("i2", "Sourdough Starter / Yeast", "100 g", 25f, BakingStageType.PREHEAT),
            RecipeIngredient("i3", "Bread Flour", "400 g", 100f, BakingStageType.PREHEAT),
            RecipeIngredient("i4", "Fine Salt", "8 g", 2f, BakingStageType.REST_AUTOLYSE),
            RecipeIngredient("i5", "Add-in Walnuts / Raisins", "50 g", 12f, BakingStageType.KNEAD_2, isAutoDispenser = true)
        )
    }

    var newIngName by remember { mutableStateOf("") }
    var newIngAmount by remember { mutableStateOf("") }

    // Stages durations
    var preheatMins by remember { mutableStateOf("15") }
    var knead1Mins by remember { mutableStateOf("10") }
    var autolyseMins by remember { mutableStateOf("15") }
    var knead2Mins by remember { mutableStateOf("20") }
    var rise1Mins by remember { mutableStateOf("45") }
    var rise2Mins by remember { mutableStateOf("30") }
    var rise3Mins by remember { mutableStateOf("40") }
    var bakeMins by remember { mutableStateOf("50") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            val recipeId = "custom_${UUID.randomUUID()}"
                            val customStages = listOf(
                                BakingStage(BakingStageType.PREHEAT, "Preheat & Equalize", preheatMins.toIntOrNull() ?: 15, 80),
                                BakingStage(BakingStageType.KNEAD_1, "Initial Hydration Knead", knead1Mins.toIntOrNull() ?: 10, 80, motorRpm = 45),
                                BakingStage(BakingStageType.REST_AUTOLYSE, "Autolyse Rest", autolyseMins.toIntOrNull() ?: 15, 80),
                                BakingStage(
                                    BakingStageType.KNEAD_2,
                                    "Gluten Knead & Add-ins",
                                    knead2Mins.toIntOrNull() ?: 20,
                                    82,
                                    motorRpm = 115,
                                    hasIngredientAlert = true,
                                    alertTitle = "Dispenser Beep: Add Ingredients",
                                    alertDescription = "Add custom mix-ins into the pan or dispenser door."
                                ),
                                BakingStage(BakingStageType.RISE_1, "Bulk Rise", rise1Mins.toIntOrNull() ?: 45, 85),
                                BakingStage(BakingStageType.PUNCH_DOWN, "Punch Down & Paddle Out", 3, 85, hasIngredientAlert = true, alertTitle = "Extract Paddle"),
                                BakingStage(BakingStageType.RISE_2, "Intermediate Rise", rise2Mins.toIntOrNull() ?: 30, 88),
                                BakingStage(
                                    BakingStageType.RISE_3,
                                    "Final Proof & Score",
                                    rise3Mins.toIntOrNull() ?: 40,
                                    90,
                                    hasIngredientAlert = true,
                                    alertTitle = "Score Loaf & Mist",
                                    alertDescription = "Slash top of loaf for optimal oven spring."
                                ),
                                BakingStage(BakingStageType.BAKE, "Artisan Bake", bakeMins.toIntOrNull() ?: 50, 375, heatingPowerPercent = 90),
                                BakingStage(BakingStageType.KEEP_WARM, "Warm Hold", 15, 140)
                            )

                            val totalTime = customStages.sumOf { it.durationMinutes }

                            val specialtyList = specialtyFloursInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                            val newRecipe = BreadRecipe(
                                id = recipeId,
                                title = title.trim(),
                                subtitle = if (subtitle.isBlank()) "${selectedCategory.label} • Custom Blend" else subtitle.trim(),
                                description = if (description.isBlank()) "Custom artisan recipe with ${primaryFlour} and ${hydrationPercent.toInt()}% hydration." else description.trim(),
                                category = selectedCategory,
                                primaryFlour = primaryFlour.trim(),
                                specialtyFlours = specialtyList,
                                yeastType = selectedYeast,
                                defaultLoafSize = selectedLoafSize,
                                defaultCrust = selectedCrust,
                                hydrationPercent = hydrationPercent.toInt(),
                                totalTimeMinutes = totalTime,
                                ingredients = ingredients.toList(),
                                stages = customStages,
                                bakerNotes = listOf(
                                    "Custom formula saved to CrustCraft library.",
                                    "Adjust bake temperature offset if crust is too dark or light."
                                ),
                                isCustom = true,
                                photoUri = photoUri
                            )
                            onSaveRecipe(newRecipe)
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_custom_recipe_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Add to My Library", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        modifier = modifier.testTag("recipe_editor_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loaf Photo Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("editor_loaf_photo_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completed Loaf Photo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (photoUri != null) {
                            TextButton(onClick = { photoUri = null }) {
                                Text("Remove", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (photoUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Loaf photo preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { launchCamera() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retake")
                            }
                            OutlinedButton(
                                onClick = {
                                    pickPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery")
                            }
                        }
                    } else {
                        Text(
                            text = "Take a photo of your loaf with the camera or pick from your device gallery.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { launchCamera() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("editor_camera_btn")
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo")
                            }
                            OutlinedButton(
                                onClick = {
                                    pickPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("editor_gallery_btn")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery")
                            }
                        }
                    }
                }
            }

            // Basic Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Recipe Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Recipe Title *") },
                        placeholder = { Text("e.g. Rosemary Polenta Sourdough") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Subtitle / Tagline") },
                        placeholder = { Text("e.g. 72% Hydration • Golden Crust") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Recipe Notes / Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Recipe Category Selector
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedCategory.emoji} ${selectedCategory.label}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Recipe Category *") },
                            supportingText = { Text(selectedCategory.description) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            RecipeCategory.userSelectable.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${cat.emoji} ${cat.label}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = cat.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Flour, Hydration & Yeast Strategy
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Flour & Fermentation Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = primaryFlour,
                        onValueChange = { primaryFlour = it },
                        label = { Text("Primary Base Flour") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = specialtyFloursInput,
                        onValueChange = { specialtyFloursInput = it },
                        label = { Text("Specialty Flours (comma separated)") },
                        supportingText = { Text("e.g. Spelt, Rye, Einkorn, Oat flour") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Yeast Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = yeastExpanded,
                        onExpandedChange = { yeastExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedYeast.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Yeast / Ferment Agent") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yeastExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = yeastExpanded,
                            onDismissRequest = { yeastExpanded = false }
                        ) {
                            YeastType.values().forEach { yeast ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(yeast.label, fontWeight = FontWeight.Bold)
                                            Text(yeast.fermentationSpeed, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    onClick = {
                                        selectedYeast = yeast
                                        yeastExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Hydration Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Baker's Hydration Target", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${hydrationPercent.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = hydrationPercent,
                            onValueChange = { hydrationPercent = it },
                            valueRange = 50f..88f,
                            steps = 37
                        )
                    }
                }
            }

            // Ingredients Builder
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ingredients List", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    ingredients.forEachIndexed { index, ing ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ing.name, fontWeight = FontWeight.SemiBold)
                                Text(ing.amount, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { ingredients.removeAt(index) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newIngName,
                            onValueChange = { newIngName = it },
                            label = { Text("Ingredient") },
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = newIngAmount,
                            onValueChange = { newIngAmount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            if (newIngName.isNotBlank() && newIngAmount.isNotBlank()) {
                                ingredients.add(
                                    RecipeIngredient(
                                        id = "custom_${System.currentTimeMillis()}",
                                        name = newIngName.trim(),
                                        amount = newIngAmount.trim(),
                                        stageToAdd = BakingStageType.PREHEAT
                                    )
                                )
                                newIngName = ""
                                newIngAmount = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Ingredient")
                    }
                }
            }

            // Stage Durations Adjuster
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Stage Durations (Minutes)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = autolyseMins,
                            onValueChange = { autolyseMins = it },
                            label = { Text("Autolyse (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = knead2Mins,
                            onValueChange = { knead2Mins = it },
                            label = { Text("Knead 2 (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rise1Mins,
                            onValueChange = { rise1Mins = it },
                            label = { Text("Bulk Rise (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = bakeMins,
                            onValueChange = { bakeMins = it },
                            label = { Text("Bake (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
