package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Scale
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.model.BakingStage
import com.example.data.model.BreadMakerModel
import com.example.data.model.BreadRecipe
import com.example.data.model.ConverterSettings
import com.example.ui.components.BakerMeasurementConverterCard
import com.example.ui.components.BakingUnitScaleConverter
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.KneadGreen
import com.example.ui.theme.ProofingBlue
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: BreadRecipe,
    onBack: () -> Unit,
    onLoadAndBake: () -> Unit,
    onSavePhoto: (String?) -> Unit = {},
    onToggleFavorite: (String) -> Unit = {},
    currentModel: BreadMakerModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConverterDialog by remember { mutableStateOf(false) }
    var showScaleDialog by remember { mutableStateOf(false) }
    var converterSettings by remember { mutableStateOf(ConverterSettings()) }

    // Camera & Photo State
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showFullScreenPhoto by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Camera TakePicture Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            onSavePhoto(pendingCameraUri.toString())
        }
    }

    // Camera Permission Launcher
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val loafDir = File(context.filesDir, "loaf_photos").apply { mkdirs() }
                val photoFile = File(loafDir, "loaf_${recipe.id}_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                pendingCameraUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            showPermissionDeniedAlert = true
        }
    }

    // Photo Picker Launcher
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { sourceUri ->
            try {
                val loafDir = File(context.filesDir, "loaf_photos").apply { mkdirs() }
                val photoFile = File(loafDir, "loaf_${recipe.id}_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    photoFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val localUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                onSavePhoto(localUri.toString())
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
                val photoFile = File(loafDir, "loaf_${recipe.id}_${System.currentTimeMillis()}.jpg")
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onToggleFavorite(recipe.id) },
                        modifier = Modifier.testTag("appbar_favorite_btn")
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (recipe.isFavorite) BreadCrustGolden else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { launchCamera() },
                        modifier = Modifier.testTag("appbar_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture Loaf Photo"
                        )
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
                        onClick = onLoadAndBake,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("load_and_bake_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Load")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Load into Bread Maker & Start",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("recipe_detail_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Completed Loaf Photo Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loaf_photo_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (recipe.photoUri != null) {
                    // Photo exists: Display high-res preview and controls
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Bake",
                                    tint = BreadCrustGolden,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Completed Loaf Photo",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Baked Result",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showFullScreenPhoto = true }
                                .testTag("loaf_photo_image")
                        ) {
                            AsyncImage(
                                model = recipe.photoUri,
                                contentDescription = "Completed ${recipe.title} loaf photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Tap to zoom chip
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Zoom",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "View Fullscreen",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Photo Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { launchCamera() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("retake_loaf_photo_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Retake",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retake Photo", style = MaterialTheme.typography.labelMedium)
                            }

                            OutlinedButton(
                                onClick = {
                                    pickPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.testTag("change_loaf_photo_gallery_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Gallery",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery", style = MaterialTheme.typography.labelMedium)
                            }

                            IconButton(
                                onClick = { showDeleteConfirmDialog = true },
                                modifier = Modifier.testTag("delete_loaf_photo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete photo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                } else {
                    // No photo yet: Attractive placeholder & call-to-action
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Capture Your Baked Loaf",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Take a photo of your golden crust and crumb structure when the baking cycle finishes to document your artisan bake!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { launchCamera() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("take_loaf_photo_camera_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Take Photo",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    pickPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pick_loaf_photo_gallery_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "From Gallery",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery")
                            }
                        }
                    }
                }
            }

            // Hero Intro Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = recipe.subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (recipe.isFavorite || recipe.bakeCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (recipe.isFavorite) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BreadCrustGolden.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, BreadCrustGolden.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = BreadCrustGolden,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "FAVORITE LOAF",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BreadCrustGolden
                                        )
                                    }
                                }
                            }

                            if (recipe.bakeCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Whatshot,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BAKED ${recipe.bakeCount} TIMES",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Grid (Hydration, Total Time, Yeast Type, Default Crust)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            label = "HYDRATION",
                            value = "${recipe.hydrationPercent}%",
                            icon = Icons.Default.WaterDrop,
                            color = ProofingBlue,
                            modifier = Modifier.weight(1f)
                        )
                        val hours = recipe.totalTimeMinutes / 60
                        val mins = recipe.totalTimeMinutes % 60
                        MetricPill(
                            label = "TOTAL CYCLE",
                            value = "${hours}h ${mins}m",
                            icon = Icons.Default.Timer,
                            color = BreadCrustGolden,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("YEAST METHOD", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                Text(recipe.yeastType.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("RECOMMENDED CRUST", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                Text("${recipe.defaultCrust.label} (${recipe.defaultLoafSize.label})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Zojirushi / Bread Machine Course Mapping
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val manualUrl = currentModel.manualUrl
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(manualUrl))
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                val mappedCourse = currentModel.mapRecommendedCourse(recipe.zojirushiRecommendedCourse)
                                Column {
                                    Text(
                                        text = "${if (currentModel.isZojirushi) "Zojirushi ${currentModel.modelCode}" else "Machine"} Course #${mappedCourse.courseNumber} (${mappedCourse.courseName})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${mappedCourse.description} • Tap to view ${currentModel.shortName} manual",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Manual URL",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // 1-lb Pan capacity advisory for compact Zojirushi models
                    if (currentModel.defaultLoafSize == com.example.data.model.LoafSize.ONE_LB && recipe.defaultLoafSize != com.example.data.model.LoafSize.ONE_LB) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Compact ${currentModel.modelCode} Pan: Standard recipe is ${recipe.defaultLoafSize.label}. Use 'Scale & g↔oz' below to scale down to fit ${currentModel.defaultLoafCapacity} (max ${currentModel.maxFlourGrams}g flour).",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Ingredients Section with Baker's percentages & Stage additions
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingredients & Baker's Percentage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showScaleDialog = true }
                                .testTag("recipe_open_scale_converter_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Scale,
                                    contentDescription = "Scale & Units",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scale & g↔oz",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showConverterDialog = true }
                                .testTag("recipe_open_converter_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Scale,
                                    contentDescription = "Baking Converter",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "g ↔ mL",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
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
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    val stageNote = if (ingredient.isAutoDispenser) "Dispenser Chime" else ingredient.stageToAdd.displayName
                                    Text(
                                        text = "Add at: $stageNote ${if (ingredient.notes.isNotEmpty()) "• ${ingredient.notes}" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (ingredient.isAutoDispenser) AlertBerryRed else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = ingredient.amount,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (ingredient.bakerPercentage > 0f) {
                                        Text(
                                            text = "${ingredient.bakerPercentage.toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            if (index < recipe.ingredients.size - 1) {
                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Step-by-Step Baking Cycle Program Stages
            Column {
                Text(
                    text = "Automated Baking Cycle Program (${recipe.stages.size} Stages)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                recipe.stages.forEachIndexed { index, stage ->
                    StageDetailRow(index + 1, stage)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Baker's Expert Notes
            if (recipe.bakerNotes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Baker Notes",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Artisan Baker's Notes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        recipe.bakerNotes.forEach { note ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text("• ", color = MaterialTheme.colorScheme.onTertiaryContainer)
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Measurement Converter Dialog (g <-> mL)
    if (showConverterDialog) {
        Dialog(
            onDismissRequest = { showConverterDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Baker's Measurement Converter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showConverterDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close dialog")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    BakerMeasurementConverterCard(
                        settings = converterSettings,
                        onUpdateSettings = { converterSettings = it }
                    )
                }
            }
        }
    }

    // Baking Scale & Unit Converter Dialog (g <-> oz, lb, kg & Recipe Scaling)
    if (showScaleDialog) {
        Dialog(
            onDismissRequest = { showScaleDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .padding(vertical = 20.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    BakingUnitScaleConverter(
                        recipe = recipe,
                        onClose = { showScaleDialog = false }
                    )
                }
            }
        }
    }

    // Full-screen Photo Preview Dialog
    if (showFullScreenPhoto && recipe.photoUri != null) {
        Dialog(
            onDismissRequest = { showFullScreenPhoto = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .testTag("fullscreen_photo_dialog")
            ) {
                AsyncImage(
                    model = recipe.photoUri,
                    contentDescription = "Full loaf photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                // Top bar with close button & recipe name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = { showFullScreenPhoto = false },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Camera Permission Denied Alert
    if (showPermissionDeniedAlert) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedAlert = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Camera Permission Needed") },
            text = {
                Text("Camera access allows you to take pictures of your freshly baked loaves directly within CrustCraft. You can allow camera access or choose a loaf photo from your device's photo gallery.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDeniedAlert = false
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDeniedAlert = false
                        pickPhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text("Use Gallery Instead")
                }
            }
        )
    }

    // Delete Photo Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Remove Loaf Photo?") },
            text = { Text("Are you sure you want to remove the photo of your completed loaf from this recipe entry?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onSavePhoto(null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun StageDetailRow(stepNumber: Int, stage: BakingStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (stage.type) {
                com.example.data.model.BakingStageType.KNEAD_1,
                com.example.data.model.BakingStageType.KNEAD_2 -> KneadGreen.copy(alpha = 0.08f)
                com.example.data.model.BakingStageType.RISE_1,
                com.example.data.model.BakingStageType.RISE_2,
                com.example.data.model.BakingStageType.RISE_3,
                com.example.data.model.BakingStageType.REST_AUTOLYSE -> ProofingBlue.copy(alpha = 0.08f)
                com.example.data.model.BakingStageType.BAKE -> BakeFireOrange.copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stage.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (stage.hasIngredientAlert) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert",
                            tint = AlertBerryRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                if (stage.alertDescription != null) {
                    Text(
                        text = "Alert: ${stage.alertDescription}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AlertBerryRed
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${stage.durationMinutes} min",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stage.targetTempF}°F",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
