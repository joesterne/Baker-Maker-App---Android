# CrustCraft 🍞🥖

**CrustCraft** is an artisan Android companion app engineered for automatic bread makers—especially **Zojirushi Home Bakery** models (Virtuoso® Plus, Virtuoso®, Supreme, Maestro, Mini, and universal machines)—as well as sourdough, specialty flour, and custom artisanal bread enthusiasts.

CrustCraft pairs precision stage countdown telemetry, Baker's Percentage hydration calculators, persistent Room database recipe management, and an interactive **multimodal AI Bread Doctor powered by Gemini 3.5 Flash** for text and photo diagnostics with complete offline operation.

---

## ⚡ Offline Mode & Local Storage Architecture

CrustCraft is designed **offline-first**. It works seamlessly with zero internet connection:

### 1. 🗄️ On-Device Room Database Persistence
- **Zero Cloud Dependency for Core Baking:** All standard artisanal recipes, custom user formulas, favorite states, and bake counts are stored in a local SQLite database via Android Room (`crustcraft_database`).
- **Tables:**
  - `recipes`: Preloaded sourdough, milk bread, and artisan formulas seeded at first launch.
  - `custom_recipes`: User-authored recipes with custom ingredient percentages, hydration, and baker's notes.
  - `favorite_recipes`: Persistent favorite bookmarks and lifetime bake counts (`bakeCount`).
- **Reactive Data Layer:** Repositories emit Kotlin Coroutine `Flow` streams from Room DAOs (`RecipeDao`, `CustomRecipeDao`, `FavoriteRecipeDao`), keeping the UI reactive with zero latency.

### 2. 📡 Intelligent Offline Diagnostic Fallback
- **Dual-Engine Loaf Doctor:**
  - **Online Mode:** Calls Gemini 3.5 Flash via REST for deep multimodal computer-vision diagnosis of loaf pictures and natural language consultation.
  - **Offline Mode:** If network connectivity is lost, airplane mode is on, or no API key is provided, the Loaf Doctor automatically falls back to CrustCraft's embedded local knowledge base (`BreadTroubleshootingKnowledgeBase`).
- **Local Diagnostic Knowledge Base:**
  - Contains 12+ preloaded, scientifically verified bread machine & sourdough failure modes (*sunken/collapsed tops, dense/gummy crumbs, flat bricks, mushroom pan overflow, pale or thick crusts, stuck paddles, large holes under crust, premature sourness*).
  - Delivers instant root cause analysis, actionable recipe fixes, and machine tuning without making any network requests.
  - Accessible either via the natural language chat input or the instant diagnosis symptom chip carousel.

### 3. 📸 Private Internal Image Storage
- Loaf photos captured via camera or selected from gallery are copied to app-internal private storage (`context.filesDir/loaf_photos` and `context.filesDir/chat_diagnostics`) with secure `FileProvider` URIs.
- Photos persist across app restarts and remain strictly private on the user's device.

### 4. ⏱️ 100% Local Timers & Hardware Emulation
- Stage countdown timers, dispenser audio/haptic chimes, temperature conversions, and simulation engines run entirely in-process using Android coroutines and standard system services.

---

## ✨ Key Features

### 1. 🤖 AI Loaf Doctor & Diagnostic Chatbot (Gemini 3.5 Flash + Offline KB)
- **Multimodal Analysis:** Ask questions or attach photos of your baked bread (crust, crumb, or loaf cross-section) via camera or gallery.
- **Master Baker Persona:** Embedded system instructions analyze blister patterns, crumb density, rise height, and paddle tear marks.
- **Scientific Diagnoses & Corrective Steps:** Explains root causes (hydration imbalance, over/under proofing, yeast activity, water temperature, crust presets) and provides 2–3 actionable steps for the next bake.
- **Offline Fallback Engine:** Automatic switch to on-device diagnostic heuristics when disconnected.

### 2. 🎛️ Real-Time Baker Dashboard & Machine Telemetry
- **Hardware Telemetry Simulation:** Live motor RPM, chamber temperature (°F), heating element status, lid sensor, interior lamp, and pan lock.
- **Stage Progress Stepper:** Track cycles through *Preheat & Equalize*, *Hydration & Knead*, *Rest & Autolyse*, *Gluten Development*, *Bulk Fermentation*, *Punch Down & Paddle Extraction*, *Proofing 1–3*, *Artisan Bake*, and *Keep Warm*.
- **Live Push Ingredient Alerts:** Timed prompts and chime notifications for dispenser add-ins (raisins, nuts, seeds, cheese) and paddle removal.
- **Simulation Speed Control:** 1x real-time up to 60x simulation for rapid recipe testing.

### 3. ⭐ Artisanal Recipe Library & Persistent Favorites
- **Persistent Storage:** Backed by Android **Room Database** (`AppDatabase`, `RecipeDao`, `FavoriteRecipeDao`).
- **Persistent 'Favorites' Category:** Filter recipes by "Favorites" tab with live count badges and sorting prioritized by your most-baked loaves (`bakeCount`).
- **Comprehensive Catalog:** Preloaded sourdoughs (San Francisco Sourdough, Rustic Boule, Rye, Whole Grain), milk breads (Hokkaido Milk Bread), brioche, pizza doughs, and custom user-authored recipes.
- **Custom Recipe Editor:** Create custom formulas with custom hydration, flour blends, baking stages, and baker's notes.
- **Photo Diary:** Snap and attach photos of your finished loaves to individual recipe entries.

### 4. ⚖️ Baker's Hydration & Measurement Tools
- **Hydration Calculator:** Real-time baker's percentage calculations based on total flour weight.
- **Scale & Unit Converter:** Convert fluid ounces, grams, milliliters, and cups with liquid density adjustment (water, milk, buttermilk, honey, oil).
- **Loaf Rescaling:** Scale recipes between 1.0 lb, 1.5 lb, and 2.0 lb capacities.

### 5. ⏱️ Bread Machine Cycles & Stage Workflows
- **Zojirushi Menu Program Profiles:** Exact cycle timings for White, Whole Wheat, European, Sourdough Starter, Multigrain, Gluten-Free, Cake, and Dough courses.
- **Standalone Stage Countdown Timers:** Dedicated timers for manual sourdough fermentations, fold intervals, and countertop proofing.

### 6. ⚙️ Machine Settings & Model Support
Dedicated presets, pan capacities, and manual links for:
- **Zojirushi BB-PDC20** (*Home Bakery Virtuoso® Plus* – 2.0 lb, dual horizontal blades, lid heater)
- **Zojirushi BB-PAC20** (*Home Bakery Virtuoso®* – 2.0 lb, dual horizontal blades)
- **Zojirushi BB-CEC20** (*Home Bakery Supreme* – 2.0 lb, dual horizontal blades)
- **Zojirushi BB-SSC10** (*Home Bakery Maestro®* – 1.0 lb compact, auto-dispenser)
- **Zojirushi BB-ST10 & BB-KW10** (*Home Bakery Kobo Kitchen*)
- **Zojirushi BB-HAC10 / HD10 / HA10** (*Home Bakery Mini / Compact*)
- **Universal Bread Makers** (*CrustMaster Pro Smart Baker*)

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Data Layer
- **Persistence (Local Storage):** Android Room 2.7.0 with SQLite & Kotlin Coroutines Flow
- **AI / Multimodal:** Google Gemini API (`gemini-3.5-flash`) via Retrofit & OkHttp with local fallback
- **JSON Serialization:** Moshi with Kotlin Reflection
- **Image Loading:** Coil Compose 2.7.0
- **Secrets Management:** Secrets Gradle Plugin reading from `.env` / `.env.example`
- **Testing:** Robolectric 4.16.1 & JUnit 4 JVM testing

---

## 📂 Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                     # Single Activity with Scaffold and tab navigation
├── data/
│   ├── ai/
│   │   ├── BakingChatBotRepository.kt  # Multi-turn chat repository, offline fallback & image encoding
│   │   ├── GeminiApiService.kt         # Retrofit service interface & client
│   │   └── GeminiModels.kt             # Gemini request/response and ChatMessage models
│   ├── local/
│   │   ├── AppDatabase.kt              # Room Database definition & initial seeders
│   │   ├── Converters.kt               # Room type converters (Moshi JSON)
│   │   ├── FavoriteRecipeDao.kt        # Room DAO for persistent favorites & bake counts
│   │   ├── FavoriteRecipeEntity.kt     # Room entity for favorite records
│   │   ├── RecipeDao.kt                # Room DAO for standard recipes
│   │   └── RecipeEntity.kt             # Room entity for recipes
│   ├── model/
│   │   ├── BreadModels.kt              # Domain models (BreadRecipe, BreadMakerModel, etc.)
│   │   ├── PreloadedRecipes.kt         # Preloaded artisanal and sourdough recipes
│   │   ├── StageTimerModel.kt          # Stage countdown workflows and model timing
│   │   └── TroubleshootingModels.kt    # Symptom knowledge base & diagnostic rules (offline KB)
│   └── repository/
│       └── RecipeRepository.kt         # Unified data repository with Flow combines
├── ui/
│   ├── components/
│   │   ├── BakingChatBotSection.kt     # Multi-turn Gemini AI diagnostic chat UI with offline indicator
│   │   ├── LoafTroubleshootingSection.kt # "Loaf Doctor" diagnosis & prompt view
│   │   ├── MachineStatusCard.kt        # Hardware telemetry dashboard widget
│   │   ├── SourdoughCountdownTimerCard.kt # Precision countdown stepper
│   │   └── ...                         # Converters, calculators, steppers
│   ├── screens/
│   │   ├── DashboardScreen.kt          # Primary baker control dashboard
│   │   ├── RecipeLibraryScreen.kt      # Catalog with search & category filters
│   │   ├── RecipeDetailScreen.kt       # Recipe view, photos, scaling & diagnosis
│   │   ├── BreadStageTimerScreen.kt    # Countdown timer routines
│   │   ├── SourdoughRecipesScreen.kt   # Dedicated Sourdough tab
│   │   └── MachineSettingsScreen.kt    # Model selection, offline storage status & machine preferences
│   └── theme/                          # M3 color scheme, typography & styles
└── viewmodel/
    └── BreadMakerViewModel.kt          # Central state orchestrator & coroutine scopes
```

---

## 🚀 Setup & Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- Android SDK 35/36 (minSdk 24)
- JDK 17 / 21

### Offline Readiness (No Setup Required)
- **CrustCraft works right out of the box without any network configuration or API keys.** All database records, timers, formulas, and local troubleshooting diagnoses are preloaded locally.

### (Optional) Configure Gemini API Key for Live Multimodal Chat
To enhance the Loaf Doctor with live Gemini 3.5 Flash multimodal vision analysis:
1. Obtain an API key from [Google AI Studio](https://aistudio.google.com/).
2. In AI Studio Build, enter your key in the **Secrets panel** under `GEMINI_API_KEY`.
3. Alternatively, for local builds, create a `.env` file in the root project directory:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

### Build & Run
Run the standard Gradle tasks:
```bash
# Compile and build the debug APK
gradle assembleDebug

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

---

## 🔒 Permissions
- `android.permission.INTERNET`: Used for Gemini AI diagnostic API calls when online (gracefully skipped when offline).
- `android.permission.CAMERA`: Used for capturing real-time photos of bread crust and crumb for diagnosis and recipe photo diaries.
- `android.permission.POST_NOTIFICATIONS`: Triggers dispenser alert notifications and cycle completion alerts.
- `android.permission.VIBRATE`: Provides haptic cues when ingredient dispenser alerts sound.
