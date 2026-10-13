package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.data.model.BreadMakerModel
import com.example.data.model.BreadRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

data class BakingChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val pendingImageUri: Uri? = null,
    val pendingImageBase64: String? = null,
    val errorMessage: String? = null
)

class BakingChatBotRepository(
    private val context: Context
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _uiState = MutableStateFlow(
        BakingChatUiState(
            messages = listOf(
                ChatMessage(
                    isUser = false,
                    text = "Hello! I am your AI Master Baker & Diagnostic Assistant 🥖👨‍🍳\n\nI can analyze any bread issue you encounter—from sunken tops, dense crumb, and gummy texture to paddle marks and crust color.\n\nType your question, or tap 📷 Camera / 🖼️ Gallery to show me a picture of your loaf for visual analysis!"
                )
            )
        )
    )
    val uiState: StateFlow<BakingChatUiState> = _uiState.asStateFlow()

    fun setPendingImage(uri: Uri?) {
        if (uri == null) {
            _uiState.update { it.copy(pendingImageUri = null, pendingImageBase64 = null) }
            return
        }
        // Immediately set URI so UI updates the thumbnail preview without lag
        _uiState.update { it.copy(pendingImageUri = uri, pendingImageBase64 = null) }
        repositoryScope.launch {
            val base64 = uriToBase64(uri)
            _uiState.update { current ->
                if (current.pendingImageUri == uri) {
                    current.copy(pendingImageBase64 = base64)
                } else {
                    current
                }
            }
        }
    }

    fun clearPendingImage() {
        _uiState.update { it.copy(pendingImageUri = null, pendingImageBase64 = null) }
    }

    suspend fun sendMessage(
        userText: String,
        activeRecipe: BreadRecipe?,
        machineModel: BreadMakerModel
    ) = withContext(Dispatchers.IO) {
        val trimmed = userText.trim()
        val currentPendingUri = _uiState.value.pendingImageUri
        val currentPendingBase64 = _uiState.value.pendingImageBase64

        if (trimmed.isEmpty() && currentPendingBase64 == null) return@withContext

        val userMessage = ChatMessage(
            isUser = true,
            text = trimmed.ifEmpty { "Diagnose this bread picture." },
            imageBase64 = currentPendingBase64,
            imageUri = currentPendingUri?.toString()
        )

        // Clear pending image and append user message, start loading
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isLoading = true,
                pendingImageUri = null,
                pendingImageBase64 = null,
                errorMessage = null
            )
        }

        try {
            val apiKey = GeminiClient.getApiKey()
            if (apiKey.isBlank()) {
                val errorMsg = "Gemini API key is not configured. Please add your GEMINI_API_KEY to AI Studio Secrets."
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        messages = state.messages + ChatMessage(
                            isUser = false,
                            text = errorMsg,
                            isError = true
                        )
                    )
                }
                return@withContext
            }

            // Build multi-turn contents list
            val allMessages = _uiState.value.messages
            val contentsList = mutableListOf<GeminiContent>()

            // Add history skipping initial welcome bot greeting for model context
            allMessages.drop(1).forEach { msg ->
                val parts = mutableListOf<GeminiPart>()
                if (msg.imageBase64 != null) {
                    parts.add(
                        GeminiPart(
                            inlineData = GeminiInlineData(
                                mimeType = "image/jpeg",
                                data = msg.imageBase64
                            )
                        )
                    )
                }
                if (msg.text.isNotEmpty()) {
                    parts.add(GeminiPart(text = msg.text))
                }
                contentsList.add(
                    GeminiContent(
                        role = if (msg.isUser) "user" else "model",
                        parts = parts
                    )
                )
            }

            val recipeContext = if (activeRecipe != null) {
                val ingSummary = activeRecipe.ingredients.joinToString(", ") { "${it.name} (${it.amount})" }
                "Active Recipe: ${activeRecipe.title} (Hydration: ${activeRecipe.hydrationPercent}%, Yeast: ${activeRecipe.yeastType.label}, Crust: ${activeRecipe.defaultCrust.label}, Ingredients: $ingSummary)"
            } else {
                "No active recipe currently loaded."
            }

            val systemInstructionText = """
                You are an expert artisan master baker and diagnostic AI specialist embedded inside the CrustCraft smart bread machine app.
                Your purpose is to diagnose bread baking problems, analyze loaf pictures, review bread machine cycles, and give precise, actionable corrections to solve any issue.
                
                Current Context:
                - Machine: ${machineModel.modelName} (Model: ${machineModel.modelCode}, Pan: ${machineModel.panType}, Blades: ${machineModel.bladeCount}, Capacity: ${machineModel.defaultLoafCapacity})
                - $recipeContext
                
                Diagnostic Guidelines:
                1. If an image is provided: Carefully inspect the crust color, blister patterns, crumb structure, density, ear/score opening, height, and pan shape.
                2. Explain the root scientific cause (e.g., over-proofing, excessive hydration, water temperature too warm, dead yeast, salt omitted, premature paddle removal, insufficient bake time, incorrect menu course).
                3. Provide 2-3 clear, numbered steps to fix the formula or machine settings for the very next bake.
                4. Always maintain an encouraging, knowledgeable artisan baker tone. Format with clear bullet points and bold highlights.
            """.trimIndent()

            val request = GeminiRequest(
                contents = contentsList,
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemInstructionText))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    topP = 0.95f,
                    topK = 40,
                    maxOutputTokens = 1200
                )
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I was unable to analyze this loaf. Please try rephrasing or submitting another picture."

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    messages = state.messages + ChatMessage(
                        isUser = false,
                        text = candidateText
                    )
                )
            }
        } catch (e: Exception) {
            // Intelligent Offline Mode Fallback:
            // When offline, network is unavailable, or API key is not present, use the built-in
            // local bread troubleshooting knowledge base to deliver instant diagnostics.
            val offlineMatches = com.example.data.model.BreadTroubleshootingKnowledgeBase.diagnose(trimmed)
            val topMatch = offlineMatches.firstOrNull()

            val offlineDiagnosticText = if (topMatch != null) {
                """
                📡 [Offline Mode Active — Local Storage Diagnosis]
                
                I'm operating in offline mode using CrustCraft's local diagnostic knowledge base.
                
                ${topMatch.iconEmoji} Detected Symptom: **${topMatch.symptom}**
                
                🔬 **Probable Cause:**
                ${topMatch.primaryCause}
                
                🛠️ **Recommended Fixes for Next Bake:**
                ${topMatch.immediateFix}
                
                ⚙️ **Machine Tuning:**
                ${topMatch.machineTuning}
                
                💡 *Tip: ${topMatch.tipQuote}*
                """.trimIndent()
            } else {
                """
                📡 [Offline Mode Active — Local Storage]
                
                I could not reach Gemini AI (${e.localizedMessage ?: "No network connection"}).
                
                In offline mode, you can still:
                • Use the "Instant Diagnosis" tab to browse 12+ preloaded symptom solutions.
                • Search any symptom (e.g., "sunken top", "pale crust", "gummy crumb", "paddle stuck").
                • Access all recipes, stage timers, hydration calculators, and favorites saved locally in Room!
                """.trimIndent()
            }

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    messages = state.messages + ChatMessage(
                        isUser = false,
                        text = offlineDiagnosticText,
                        isError = false
                    )
                )
            }
        }
    }

    fun clearChat() {
        _uiState.value = BakingChatUiState(
            messages = listOf(
                ChatMessage(
                    isUser = false,
                    text = "Chat history reset. How can I help you diagnose your latest bread bake? Feel free to describe symptoms or attach a loaf picture!"
                )
            )
        )
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val maxDimension = 1024

            // Step 1: Decode image dimensions without loading pixel data
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }

            var inSampleSize = 1
            if (boundsOptions.outHeight > maxDimension || boundsOptions.outWidth > maxDimension) {
                val halfHeight = boundsOptions.outHeight / 2
                val halfWidth = boundsOptions.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            // Step 2: Decode downsampled bitmap directly into memory
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.RGB_565 // 50% memory savings
            }
            val sampled = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return null

            // Step 3: Precise scale if still slightly larger than maxDimension
            val finalBitmap = if (sampled.width > maxDimension || sampled.height > maxDimension) {
                val ratio = sampled.width.toFloat() / sampled.height.toFloat()
                val targetW = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
                val targetH = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
                val scaled = Bitmap.createScaledBitmap(sampled, targetW, targetH, true)
                if (scaled != sampled) sampled.recycle()
                scaled
            } else {
                sampled
            }

            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            finalBitmap.recycle()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
