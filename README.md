# CrustCraft 🍞🥖

**CrustCraft** is an artisan Android companion app engineered for automatic bread makers—especially **Zojirushi Home Bakery** models (Virtuoso® Plus, Virtuoso®, Supreme, Maestro, Mini, and universal smart machines)—as well as sourdough, specialty flour, and custom artisanal bread enthusiasts.

CrustCraft pairs precision stage countdown telemetry, Baker's Percentage hydration calculators, persistent Room database recipe management, and an interactive **multimodal AI Bread Doctor powered by Gemini 3.5 Flash** for text and picture diagnostics with complete offline operation.

---

## ⚡ Offline Mode & Local Storage Architecture

CrustCraft is built **offline-first**. Every core baking capability, formula calculation, and diagnostic evaluation operates completely without an internet connection:

### 1. 🗄️ On-Device Room Database Persistence
- **Zero Cloud Dependency for Core Baking:** All artisanal recipes, custom user-created recipes, favorite states, lifetime bake logs, and multi-stage bread machine cycle trackers are stored locally using SQLite via Android Room (`crustcraft_database`, schema version 7).
- **Persistent Tables:**
  - `recipes`: Preloaded sourdough, milk bread, and artisan formulas seeded automatically on initial launch.
  - `custom_recipes`: User-authored recipes with custom ingredient weights, hydration percentages, program cycles, and baker notes.
  - `favorite_recipes`: Persistent favorite bookmarks and lifetime bake counts (`bakeCount`) with timestamps of the last bake.
  - `cycle_tracking`: Multi-stage bread machine cycle tracking sessions (current stage, elapsed and remaining seconds, target temperatures, motor RPM, and 'add-ins' alarm configurations with customizable mix-ins like raisins, walnuts, cranberries, and seeds).
- **Reactive Data Layer:** Repositories emit Kotlin Coroutine `Flow` streams directly from Room DAOs (`RecipeDao`, `CustomRecipeDao`, `FavoriteRecipeDao`, `BreadCycleTrackingDao`), ensuring UI changes update reactively with zero lag.

### 2. 📡 Intelligent Dual-Engine Diagnostic Fallback
- **Online Mode:** When connected to the internet and configured with a Gemini API key, the Loaf Doctor invokes Google's `gemini-3.5-flash` model via Retrofit for deep multimodal computer-vision diagnosis of loaf pictures and interactive multi-turn troubleshooting conversations.
- **Offline Mode:** If network connectivity is lost, airplane mode is active, or an API key is not present, the Loaf Doctor automatically falls back to CrustCraft's embedded local knowledge base (`BreadTroubleshootingKnowledgeBase`).
- **Preloaded Local Diagnostic Knowledge Base:**
  - Contains 12+ scientifically verified bread machine and sourdough failure modes:
    - *Sunken / Collapsed Crater Top* (excess hydration, over-proofing, over-yeasting)
    - *Dense, Heavy, or Gummy Crumb* (under-hydration, dead yeast, salt excess)
    - *Short, Flat Brick Loaf* (yeast forgotten or scalded by warm water, wrong flour)
    - *Mushroom Top / Pan Overflow* (excess yeast/sugar, dough touching lid heater)
    - *Pale or Leathery Top Crust* (low sugar, lack of upper heating element, under-baked)
    - *Torn Bottom / Stuck Knead Paddle* (paddle left during final proof/bake)
    - *Flying Crust / Cavities Under Crust* (steam trapped under rapid crust set)
    - *Premature Sourness or Off-Flavors* (extended warm fermentation)
  - Delivers instant root cause analysis, actionable recipe adjustments, and machine tuning without making any network requests.
  - Accessible via both the freeform natural language prompt area and the instant symptom chip carousel.

### 3. 📸 Private Internal Image Storage
- Photos of loaves captured with the camera or selected from the device photo picker are copied directly into app-internal private storage (`context.filesDir/loaf_photos` and `context.filesDir/chat_diagnostics`) using secure `FileProvider` content URIs.
- Photos persist across device restarts and remain strictly private on the user's device.

### 4. ⏱️ 100% Local Timers & Hardware Emulation
- Stage countdown timers, dispenser audio/haptic chimes, temperature conversions, and simulation engines run entirely in-process using Android coroutines and system vibrator/notification services.

---

## ✨ Key Features

### 1. 🤖 AI Loaf Doctor & Diagnostic Chatbot (Gemini 3.5 Flash + Local KB)
- **Multimodal Text & Picture Diagnostics:** Ask questions or attach photos of your finished bread (crust color, crumb structure, loaf cross-section, or oven rise) directly from your camera or photo gallery.
- **Master Baker Persona:** Embedded system instructions instruct the AI to analyze crumb pore distribution, blister patterns, gelatinization, and paddle tear marks.
- **Root Cause & Concrete Remedies:** Delivers scientifically grounded explanations (hydration ratios, water temperature, yeast viability, proofing times, crust shades) and provides 2–3 immediate adjustments for your next bake.
- **Troubleshooting Prompt Area:** Accessible across the app:
  - From the main **Baker Dashboard** via the `Doctor` quick tool button in a dedicated bottom sheet (`TroubleshootingPromptSheet`).
  - Embedded in every **Recipe Detail Screen** (`LoafTroubleshootingSection`) to diagnose specific recipes.

### 2. 🎛️ Real-Time Baker Dashboard & Machine Telemetry
- **Hardware Telemetry Simulation:** Live motor RPM, chamber temperature (°F), heating element status, lid sensor, interior lamp, and pan lock.
- **Stage Progress Stepper:** Track cycles through *Preheat & Equalize*, *Hydration & Knead*, *Rest & Autolyse*, *Gluten Development*, *Bulk Fermentation*, *Punch Down & Paddle Extraction*, *Proofing 1–3*, *Artisan Bake*, and *Keep Warm*.
- **Ingredient Dispenser Alerts:** Timed prompts and chime notifications for dispenser add-ins (raisins, nuts, seeds, cheese) and paddle extraction before final proofing.
- **Simulation Speed Control:** 1x real-time up to 60x simulation for rapid recipe and workflow testing.

### 3. ⭐ Artisanal Recipe Library & Persistent Favorites
- **Persistent Room Storage:** Backed by Android Room Database (`AppDatabase`, `RecipeDao`, `CustomRecipeDao`, `FavoriteRecipeDao`).
- **⭐ Favorites Filter Tab:** Top-level category filter displaying live count badges of favorited recipes and prioritizing your most-baked loaves (`bakeCount`).
- **Comprehensive Catalog:** Preloaded sourdoughs (San Francisco Sourdough, Rustic Boule, Rye, Whole Grain), milk breads (Hokkaido Milk Bread), brioche, pizza doughs, official Zojirushi manual recipes, and custom user formulas.
- **Custom Recipe Editor:** Create custom formulas with custom hydration percentages, flour blends, baking stages, and baker notes.
- **Recipe Photo Diary:** Snap and attach photos of your finished loaves directly to individual recipe records.

### 4. ⚖️ Baker's Hydration & Measurement Tools
- **Hydration Calculator:** Instant baker's percentage calculations based on total flour weight.
- **Scale & Unit Converter:** Convert fluid ounces, grams, milliliters, and cups with liquid density adjustment (water, milk, buttermilk, honey, oil).
- **Loaf Capacity Scaling:** Scale recipes dynamically between 1.0 lb, 1.5 lb, and 2.0 lb pan capacities.

### 5. 🍞 Room-Backed Multi-Stage Bread Machine Cycle Tracker & Add-In Alarms
- **Dedicated Multi-Stage Cycle Tracker UI Component (`BreadMachineCycleTrackerCard`):** Persisted directly in SQLite via Room (`cycle_tracking` table).
- **Stage Progression & Telemetry:** Visual multi-stage timeline across all 10 stages (*Preheat, Knead 1, Rest/Autolyse, Knead 2, Rise 1, Punch Down/Paddle Extraction, Rise 2, Rise 3, Bake, Keep Warm*), with live countdowns, target chamber temperatures (°F), and motor RPM.
- **Dedicated 'Add-Ins' Alarms (Raisins & Nuts):**
  - Timed dispenser chime automatically triggered during Knead 2 when dough elasticity is primed.
  - Interactive audible beep toggle, haptic vibration alert, and prominent AlertBerryRed visual banner with one-tap acknowledgment (`Mark Add-Ins Added & Mute Chime`).
  - Customizable mix-ins list persisted in Room (e.g. Thompson Raisins, Toasted Walnuts, Dried Cranberries, Pumpkin Seeds, Pecans, Chocolate Chips).
  - Instant `Test Alarm` button to test push notifications, haptic vibrations, and visual alarm states.
- **Zojirushi Menu Program Profiles:** Exact cycle timings for White, Whole Wheat, European, Sourdough Starter, Multigrain, Gluten-Free, Cake, and Dough courses.
- **Standalone Stage Countdown Timers:** Dedicated timers for manual sourdough fermentations, stretch-and-fold intervals, and countertop proofing.

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
- **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern + Clean Data Layer
- **Persistence (Local Storage):** Android Room 2.7.0 with SQLite & Kotlin Coroutines Flow
- **AI & Computer Vision:** Google Gemini API (`gemini-3.5-flash`) via Retrofit & OkHttp with local fallback
- **JSON Serialization:** Moshi with Kotlin Reflection
- **Image Loading:** Coil Compose 2.7.0
- **Secrets Management:** Secrets Gradle Plugin reading from `.env` / `.env.example`
- **Testing:** Robolectric 4.16.1 & JUnit 4 JVM testing

---

## 📂 Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                         # Single Activity with Scaffold and tab navigation
├── data/
│   ├── ai/
│   │   ├── BakingChatBotRepository.kt      # Multi-turn chat repository, offline fallback & image encoding
│   │   ├── GeminiApiService.kt             # Retrofit service interface & client
│   │   └── GeminiModels.kt                 # Gemini request/response and ChatMessage models
│   ├── local/
│   │   ├── AppDatabase.kt                  # Room Database definition & initial seeders (schema v7)
│   │   ├── BreadCycleTrackingDao.kt        # Room DAO for multi-stage cycle tracking & add-in alarms
│   │   ├── BreadMachineCycleTrackerEntity.kt# Room entity for cycle tracking state & mix-in alarms
│   │   ├── Converters.kt                   # Room type converters (Moshi JSON)
│   │   ├── CustomRecipeDao.kt              # Room DAO for custom user-authored recipes
│   │   ├── CustomRecipeEntity.kt           # Room entity for custom recipes
│   │   ├── FavoriteRecipeDao.kt            # Room DAO for persistent favorites & bake counts
│   │   ├── FavoriteRecipeEntity.kt         # Room entity for favorite records
│   │   ├── PreloadedCustomRecipes.kt       # Starter custom recipes seeded into Room
│   │   ├── PreloadedRecipes.kt             # Preloaded artisanal and sourdough recipes
│   │   ├── RecipeDao.kt                    # Room DAO for standard recipes
│   │   ├── RecipeEntity.kt                 # Room entity for recipes
│   │   └── ZojirushiManualRecipes.kt       # Official machine handbook recipes
│   ├── model/
│   │   ├── BreadMachineCycle.kt            # Cycle definitions, courses, and stage temperature/RPM profiles
│   │   ├── BreadModels.kt                  # Domain models (BreadRecipe, BreadMakerModel, BakingStage, etc.)
│   │   ├── StageTimerModel.kt              # Stage countdown workflows and model timing
│   │   └── TroubleshootingModels.kt        # Symptom knowledge base & diagnostic rules (offline KB)
│   └── repository/
│       └── RecipeRepository.kt             # Unified data repository with Room Flow streams
├── ui/
│   ├── components/
│   │   ├── BakerMeasurementConverter.kt   # Volume-to-mass and density converter card
│   │   ├── BakingChatBotSection.kt         # Multi-turn Gemini AI diagnostic chat UI with offline indicator
│   │   ├── BakingUnitScaleConverter.kt     # Metric / Imperial baking unit scaler
│   │   ├── BreadMachineCycleTrackerCard.kt # Dedicated Room-backed multi-stage cycle tracker & add-in alarm component
│   │   ├── HydrationCalculatorSheet.kt     # Baker's percentage hydration modal sheet
│   │   ├── IngredientAlertBanner.kt        # Timed add-in banner notification
│   │   ├── LoafTroubleshootingSection.kt   # "Loaf Doctor" diagnosis & prompt view
│   │   ├── MachineStatusCard.kt            # Hardware telemetry dashboard widget
│   │   ├── SourdoughCountdownTimerCard.kt  # Precision countdown stepper
│   │   ├── StageProgressStepper.kt         # Visual bread machine cycle progress stepper
│   │   └── TemperatureAdjustmentPanel.kt   # Water temperature & ambient climate adjuster
│   ├── screens/
│   │   ├── BreadMachineCyclesTimerScreen.kt# Official course timings & countdowns
│   │   ├── BreadStageTimerScreen.kt        # Standalone artisanal stage timers
│   │   ├── DashboardScreen.kt              # Primary baker control dashboard with quick tools
│   │   ├── MachineSettingsScreen.kt        # Machine model selector & offline storage manager
│   │   ├── RecipeDetailScreen.kt           # Recipe view, photos, scaling & embedded diagnosis
│   │   ├── RecipeEditorScreen.kt           # Custom recipe creator and editor
│   │   ├── RecipeLibraryScreen.kt          # Catalog with search & category filters (⭐ Favorites)
│   │   └── SourdoughRecipesScreen.kt       # Dedicated Sourdough tab & starter maintenance
│   └── theme/                              # M3 color scheme, typography, shapes & palette
└── viewmodel/
    └── BreadMakerViewModel.kt              # Central state orchestrator & coroutine scopes
```

---

## 🚀 Setup & Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- Android SDK 35/36 (minSdk 24)
- JDK 17 / 21

### Offline Readiness (No Setup Required)
- **CrustCraft works completely out of the box without network access or API credentials.** All database records, timers, formulas, and local troubleshooting diagnoses are preloaded and persistent on-device.

### (Optional) Configure Gemini API Key for Live Multimodal Chat
To enable real-time Gemini 3.5 Flash computer-vision analysis of bread photos:
1. Obtain an API key from [Google AI Studio](https://aistudio.google.com/).
2. In AI Studio Build, enter your key in the **Secrets panel** under `GEMINI_API_KEY`.
3. For local workstation builds, add your key to `.env` in the root project directory:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

### Build & Run
```bash
# Compile and build debug APK
gradle assembleDebug

# Run unit tests and Room validation
gradle :app:testDebugUnitTest
```

---

## 🔒 Permissions & Security
- `android.permission.INTERNET`: Used exclusively for multimodal Gemini API requests when online (gracefully bypassed when offline).
- `android.permission.CAMERA`: Used for capturing real-time photos of bread crust and crumb for diagnosis and recipe photo diaries.
- `android.permission.POST_NOTIFICATIONS`: Triggers dispenser alert notifications and cycle completion alerts.
- `android.permission.VIBRATE`: Provides haptic cues when ingredient dispenser alerts sound.
- **Privacy Assurance:** Photos captured or selected remain on-device in app-private storage (`filesDir`). No telemetry or user recipe data is uploaded to external servers.
