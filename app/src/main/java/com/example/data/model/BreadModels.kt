package com.example.data.model

import com.squareup.moshi.JsonClass

enum class BreadMakerModel(
    val modelName: String,
    val shortName: String,
    val modelCode: String,
    val panType: String,
    val bladeCount: Int,
    val hasLidHeater: Boolean,
    val hasHomemadeMemory: Boolean,
    val defaultLoafCapacity: String,
    val isZojirushi: Boolean = true,
    val defaultLoafSize: LoafSize = LoafSize.TWO_LB,
    val memorySlotCount: Int = 3,
    val menuCourseCount: Int = 14,
    val manualUrl: String = "https://manuals.plus/m/4f36891616398983822c8413e12d3c321c3f54eba437d7ba579451d53fbfc9a7",
    val description: String = "",
    val series: String = "Virtuoso® 2-lb",
    val modelCategory: String = "2-lb Horizontal (Dual Blade)",
    val hasAutoDispenser: Boolean = false,
    val specializedFeature: String = "Artisan Knead & Proof Cycles",
    val maxFlourGrams: Int = 560
) {
    ZOJIRUSHI_BB_PDC20(
        modelName = "Zojirushi Home Bakery Virtuoso® Plus (BB-PDC20)",
        shortName = "Virtuoso® Plus (BB-PDC20)",
        modelCode = "BB-PDC20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = true,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 3,
        menuCourseCount = 14,
        manualUrl = "https://manuals.plus/m/4f36891616398983822c8413e12d3c321c3f54eba437d7ba579451d53fbfc9a7",
        description = "Flagship dual-blade 2-lb horizontal breadmaker with upper lid heating element for even crust browning and 14 preprogrammed menu settings including Sourdough Starter.",
        series = "Virtuoso® 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Course #12 Stabilized 82°F Sourdough Starter Chamber & Lid Heater",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_PAC20(
        modelName = "Zojirushi Home Bakery Virtuoso® (BB-PAC20)",
        shortName = "Virtuoso® (BB-PAC20)",
        modelCode = "BB-PAC20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = true,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 3,
        menuCourseCount = 10,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_pac20.pdf",
        description = "Classic dual-blade horizontal 2-lb breadmaker with lid heater, Gluten-Free course, and 3 homemade memory settings for artisan timing.",
        series = "Virtuoso® 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Upper Lid Browning Element & 3 Programmable Memory Banks",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_CEC20(
        modelName = "Zojirushi Home Bakery Supreme® (BB-CEC20)",
        shortName = "Supreme® (BB-CEC20)",
        modelCode = "BB-CEC20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = false,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 3,
        menuCourseCount = 10,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_cec20.pdf",
        description = "Beloved dual-blade horizontal workhorse with dual kneading blades, 10 preprogrammed courses, 13-hour delay timer, and programmable memory.",
        series = "Supreme® 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Dual Synchronous Kneading Blades & 13-hr Delay Timer",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_CCX20(
        modelName = "Zojirushi Home Bakery Supreme® Classic (BB-CCX20)",
        shortName = "Supreme® (BB-CCX20)",
        modelCode = "BB-CCX20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = false,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 3,
        menuCourseCount = 8,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_ccx20.pdf",
        description = "Heritage dual-blade horizontal 2-lb breadmaker with 3 memory presets, twin kneading paddles, and classic stainless steel chamber.",
        series = "Supreme® 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Twin Paddles & Programmable Custom Cycle Memories",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_KW20(
        modelName = "Zojirushi Home Bakery Traditional (BB-KW20)",
        shortName = "Traditional (BB-KW20)",
        modelCode = "BB-KW20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 0,
        menuCourseCount = 8,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_kw20.pdf",
        description = "Heavy-duty dual-blade 2-lb horizontal baker with quick-baking setting, dough setting, and robust dual drive motor.",
        series = "Traditional 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Heavy-Duty Dual Drive Kneading & Rapid Loaf Cycles",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_V20(
        modelName = "Zojirushi Home Bakery Dual Blade (BB-V20)",
        shortName = "Dual Blade (BB-V20)",
        modelCode = "BB-V20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.TWO_LB,
        memorySlotCount = 0,
        menuCourseCount = 6,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_v20.pdf",
        description = "Vintage 2-lb horizontal dual-paddle baker known for reliable, long-lasting performance and artisan sandwich crumb.",
        series = "Traditional 2-lb",
        modelCategory = "2-lb Horizontal (Dual Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Classic Dual-Paddle Horizontal Baking System",
        maxFlourGrams = 560
    ),
    ZOJIRUSHI_BB_SSC10(
        modelName = "Zojirushi Home Bakery Maestro® (BB-SSC10)",
        shortName = "Maestro® (BB-SSC10)",
        modelCode = "BB-SSC10",
        panType = "Premium 1.0 lb Compact Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "1.0 lb Loaf with Auto-Dispenser",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 3,
        menuCourseCount = 14,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_ssc10.pdf",
        description = "Advanced 1-lb smart compact baker with automated nut/raisin dispenser, double temperature sensors, and homemade custom cycle memory.",
        series = "Maestro® 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = true,
        specializedFeature = "Automated Fruit/Nut Dispenser & Dual Temperature Sensors",
        maxFlourGrams = 300
    ),
    ZOJIRUSHI_BB_ST10(
        modelName = "Zojirushi Home Bakery Kobo Kitchen (BB-ST10)",
        shortName = "Kobo Kitchen (BB-ST10)",
        modelCode = "BB-ST10",
        panType = "Premium 1.0 lb Vertical Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "1.0 lb Loaf with Dual Dispenser",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 3,
        menuCourseCount = 26,
        manualUrl = "https://www.zojirushi.co.jp/toiawase/manual/pdf/BBST10.pdf",
        description = "Premium Japanese 1-lb smart baker with automated yeast & ingredient dispenser, dedicated Natural Yeast / Sourdough incubation courses, and soft crust texture options.",
        series = "Kobo 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = true,
        specializedFeature = "Automated Yeast Dispenser & Dedicated Natural Yeast Culture",
        maxFlourGrams = 300
    ),
    ZOJIRUSHI_BB_HAC10(
        modelName = "Zojirushi Home Bakery Mini (BB-HAC10)",
        shortName = "Mini (BB-HAC10)",
        modelCode = "BB-HAC10",
        panType = "Compact 1.0 lb Vertical Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.0 lb Vertical Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 0,
        menuCourseCount = 8,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_hac10.pdf",
        description = "Compact space-saving 1-lb breadmaker with carrying handle, single kneading blade, French bread setting, and Cookie/Pasta dough mode.",
        series = "Mini / Compact 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Space-Saving Footprint with Top Carrying Handle",
        maxFlourGrams = 300
    ),
    ZOJIRUSHI_BB_HE10(
        modelName = "Zojirushi Home Bakery Compact (BB-HE10)",
        shortName = "Compact (BB-HE10)",
        modelCode = "BB-HE10",
        panType = "Compact 1.0 lb Vertical Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.0 lb Vertical Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 0,
        menuCourseCount = 9,
        manualUrl = "https://www.zojirushi.co.jp/toiawase/manual/pdf/BBHE10.pdf",
        description = "Popular compact 1-lb single-blade baker with dedicated Rice Flour Bread, Soft Bread, French Bread, and fresh churned butter programs.",
        series = "Mini / Compact 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Rice Flour Bread & Fresh Churned Butter Course",
        maxFlourGrams = 300
    ),
    ZOJIRUSHI_BB_HD10(
        modelName = "Zojirushi Home Bakery (BB-HD10)",
        shortName = "Compact (BB-HD10)",
        modelCode = "BB-HD10",
        panType = "Compact 1.0 lb Vertical Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.0 lb Vertical Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 0,
        menuCourseCount = 8,
        manualUrl = "https://www.zojirushi.co.jp/toiawase/manual/pdf/bb_hd10.pdf",
        description = "Compact 1-lb baker with 3 crust texture options (Regular, Firm, Soft), French loaf mode, and rapid bake cycle for everyday fresh loaves.",
        series = "Mini / Compact 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Triple Crust Texture Selector & Compact Drive",
        maxFlourGrams = 300
    ),
    ZOJIRUSHI_BB_HA10(
        modelName = "Zojirushi Home Bakery Mini (BB-HA10)",
        shortName = "Mini (BB-HA10)",
        modelCode = "BB-HA10",
        panType = "Compact 1.0 lb Vertical Single-Blade Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.0 lb Vertical Loaf",
        isZojirushi = true,
        defaultLoafSize = LoafSize.ONE_LB,
        memorySlotCount = 0,
        menuCourseCount = 7,
        manualUrl = "https://www.zojirushi.com/servicesupport/manuals/manual_pdf/bb_ha10.pdf",
        description = "Classic compact 1-lb vertical breadmaker with top carrying handle, quick bread setting, and easy cleanup.",
        series = "Mini / Compact 1-lb",
        modelCategory = "1-lb Compact (Single Blade)",
        hasAutoDispenser = false,
        specializedFeature = "Top Carry Handle & Express 2-hour Quick Bread",
        maxFlourGrams = 300
    ),
    UNIVERSAL_SMART_BAKER(
        modelName = "CrustMaster Pro Smart Baker",
        shortName = "Universal Baker",
        modelCode = "UNIVERSAL",
        panType = "Standard 1.5 - 2.0 lb Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.5 - 2.0 lb Loaf",
        isZojirushi = false,
        defaultLoafSize = LoafSize.ONE_HALF_LB,
        memorySlotCount = 0,
        menuCourseCount = 12,
        manualUrl = "https://manuals.plus",
        description = "Universal single-blade electric bread machine compatible with all standard yeast and sourdough formulas.",
        series = "Universal",
        modelCategory = "Universal Baker",
        hasAutoDispenser = false,
        specializedFeature = "Universal Compatibility with 1.5 lb & 2.0 lb Recipes",
        maxFlourGrams = 600
    );

    fun getCourses(): List<ModelCourseInfo> = when (this) {
        ZOJIRUSHI_BB_PDC20 -> listOf(
            ModelCourseInfo(1, "White", "Classic sandwich loaf with tender crumb"),
            ModelCourseInfo(2, "Whole Wheat", "100% whole grain with prolonged preheat"),
            ModelCourseInfo(3, "European", "Artisan loaf with crisp crust via upper lid heater"),
            ModelCourseInfo(4, "Multigrain", "Hearty grains and seeds"),
            ModelCourseInfo(5, "Gluten Free", "Single intensive mix and high-heat starch bake"),
            ModelCourseInfo(6, "Salt Free", "Low sodium formula with tailored yeast balance"),
            ModelCourseInfo(7, "Sugar Free", "Extended proof for natural flour sweetness"),
            ModelCourseInfo(8, "Vegan", "Plant-based milk and dairy-free enrichment"),
            ModelCourseInfo(9, "Quick White", "Rapid cycle with instant yeast in under 2 hours"),
            ModelCourseInfo(10, "Quick Whole Wheat", "Accelerated whole grain cycle"),
            ModelCourseInfo(11, "Dough", "Knead and proof cycle for hand-shaping"),
            ModelCourseInfo(12, "Sourdough Starter", "Precise 82°F chamber temp for wild yeast culturing"),
            ModelCourseInfo(13, "Cake / Jam", "Stirring and steady simmer heating"),
            ModelCourseInfo(14, "Homemade Custom", "Fully programmable stages stored in memory slots", isHomemade = true)
        )
        ZOJIRUSHI_BB_PAC20 -> listOf(
            ModelCourseInfo(1, "Basic White", "Regular, Firm, or Soft white bread"),
            ModelCourseInfo(2, "Basic Wheat", "100% whole wheat or multigrain loaf"),
            ModelCourseInfo(3, "Gluten Free", "Specialized gluten-free loaf cycle"),
            ModelCourseInfo(4, "French", "Crisp crust with upper lid browning element"),
            ModelCourseInfo(5, "Dough", "Artisan dough for baguettes, pizza & rolls"),
            ModelCourseInfo(6, "Jam", "Fresh fruit preserves simmer cycle"),
            ModelCourseInfo(7, "Cake", "Quick batter breads and pound cakes"),
            ModelCourseInfo(8, "Quick Baking", "Accelerated 2-hour rapid bake"),
            ModelCourseInfo(9, "Homemade 1", "Custom artisan timing profile 1", isHomemade = true),
            ModelCourseInfo(10, "Homemade 2 & 3", "Custom profiles 2 & 3", isHomemade = true)
        )
        ZOJIRUSHI_BB_CEC20 -> listOf(
            ModelCourseInfo(1, "Basic White", "Standard sandwich loaf with dual kneading"),
            ModelCourseInfo(2, "Basic Wheat", "Extended preheat whole grain loaf"),
            ModelCourseInfo(3, "Basic Dough", "Knead and proof for hearth loaves"),
            ModelCourseInfo(4, "Quick White", "Fast turnaround sandwich loaf"),
            ModelCourseInfo(5, "Quick Wheat", "Accelerated whole wheat cycle"),
            ModelCourseInfo(6, "Quick Dough", "Rapid dough mixing and rise"),
            ModelCourseInfo(7, "Jam", "Fresh fruit preserves"),
            ModelCourseInfo(8, "Cake", "Quick sweet tea breads and cakes"),
            ModelCourseInfo(9, "Sourdough Starter", "Controlled incubation cycle"),
            ModelCourseInfo(10, "Homemade Memory", "3 programmable artisan memory banks", isHomemade = true)
        )
        ZOJIRUSHI_BB_CCX20 -> listOf(
            ModelCourseInfo(1, "Basic Bread", "Regular or Firm crust 2-lb loaf"),
            ModelCourseInfo(2, "Quick Bread", "Rapid bake with instant yeast"),
            ModelCourseInfo(3, "Wheat Bread", "Hearty whole wheat formulation"),
            ModelCourseInfo(4, "Dough Setting", "Dual-blade dough kneading and proofing"),
            ModelCourseInfo(5, "Fresh Jam", "Fruit jam with automated stirring"),
            ModelCourseInfo(6, "Cake", "Sweet loaf and quick bread cycle"),
            ModelCourseInfo(7, "Sourdough", "Incubation and artisan rise"),
            ModelCourseInfo(8, "Homemade Preset", "Custom time memory banks", isHomemade = true)
        )
        ZOJIRUSHI_BB_KW20 -> listOf(
            ModelCourseInfo(1, "Basic White", "Everyday sandwich loaf"),
            ModelCourseInfo(2, "Quick White", "Fast single-proof loaf"),
            ModelCourseInfo(3, "Whole Wheat", "Extended preheat grain cycle"),
            ModelCourseInfo(4, "Quick Wheat", "Rapid whole grain bake"),
            ModelCourseInfo(5, "French Bread", "Crisp crust without milk or fat"),
            ModelCourseInfo(6, "Bread Dough", "Dual-paddle dough kneading"),
            ModelCourseInfo(7, "Cake", "Quick bread and batter loaf"),
            ModelCourseInfo(8, "Jam", "Fresh fruit simmer cycle")
        )
        ZOJIRUSHI_BB_V20 -> listOf(
            ModelCourseInfo(1, "Basic Bread", "Classic 2-lb loaf with dual paddles"),
            ModelCourseInfo(2, "Quick Bread", "Fast turn-around everyday bread"),
            ModelCourseInfo(3, "Wheat Bread", "Whole grain extended cycle"),
            ModelCourseInfo(4, "French Bread", "Crisp European style loaf"),
            ModelCourseInfo(5, "Dough Cycle", "Kneaded dough ready for shaping"),
            ModelCourseInfo(6, "Cake & Jam", "Batter cakes and fruit preserves")
        )
        ZOJIRUSHI_BB_SSC10 -> listOf(
            ModelCourseInfo(1, "White", "1.0 lb sandwich bread"),
            ModelCourseInfo(2, "European", "Crusty European bread with artisan crumb"),
            ModelCourseInfo(3, "Quick White", "Express bake loaf"),
            ModelCourseInfo(4, "Whole Wheat", "100% whole grain loaf"),
            ModelCourseInfo(5, "Quick Whole Wheat", "Fast whole grain cycle"),
            ModelCourseInfo(6, "Multigrain", "With automatic nut & seed dispenser"),
            ModelCourseInfo(7, "Gluten Free", "Gluten-free bread setting"),
            ModelCourseInfo(8, "Salt Free", "Low-sodium dietary loaf"),
            ModelCourseInfo(9, "Sugar Free", "Sugar-free dietary loaf"),
            ModelCourseInfo(10, "Vegan", "Plant-based milk formula"),
            ModelCourseInfo(11, "Bread / Pizza Dough", "Knead and bulk ferment"),
            ModelCourseInfo(12, "Pasta Dough", "Firm dough kneading"),
            ModelCourseInfo(13, "Cake", "Quick sweet loaf"),
            ModelCourseInfo(14, "Homemade Custom", "3 programmable memory slots", isHomemade = true)
        )
        ZOJIRUSHI_BB_ST10 -> listOf(
            ModelCourseInfo(1, "Premium White", "Ultra-soft and moist luxury crumb"),
            ModelCourseInfo(2, "Soft White", "Delicate crust everyday loaf"),
            ModelCourseInfo(3, "Natural Yeast Bread", "Sourdough / Hoshino natural yeast loaf"),
            ModelCourseInfo(4, "Natural Yeast Starter", "Incubation cycle for sourdough culture"),
            ModelCourseInfo(5, "Whole Wheat", "Whole grain loaf"),
            ModelCourseInfo(6, "Rice Flour Bread", "100% gluten-free rice flour bread"),
            ModelCourseInfo(7, "French Bread", "Crisp crust artisan loaf"),
            ModelCourseInfo(8, "Brioche", "Rich egg and butter enriched loaf"),
            ModelCourseInfo(9, "Quick Loaf", "Rapid yeast bake in 1.5 hours"),
            ModelCourseInfo(10, "Rice Grain Bread", "Loaf baked with cooked rice grains"),
            ModelCourseInfo(11, "Mochi", "Steamed and pounded sweet rice"),
            ModelCourseInfo(12, "Udon / Pasta Dough", "Firm springy noodle dough"),
            ModelCourseInfo(13, "Cake", "Pound cake and quick bread"),
            ModelCourseInfo(14, "Jam", "Fresh fruit spread"),
            ModelCourseInfo(15, "Homemade Memory 1", "Custom cycle slot 1", isHomemade = true),
            ModelCourseInfo(16, "Homemade Memory 2", "Custom cycle slot 2", isHomemade = true),
            ModelCourseInfo(17, "Homemade Memory 3", "Custom cycle slot 3", isHomemade = true)
        )
        ZOJIRUSHI_BB_HAC10 -> listOf(
            ModelCourseInfo(1, "Basic White", "Regular, Firm, or Soft 1-lb loaf"),
            ModelCourseInfo(2, "French Bread", "Crisp chewy crust baguette-style loaf"),
            ModelCourseInfo(3, "Bread Dough", "Pizza and artisan shaping dough"),
            ModelCourseInfo(4, "Cookie / Pasta Dough", "No-rise dough mixing"),
            ModelCourseInfo(5, "Cake", "Quick sweet loaves"),
            ModelCourseInfo(6, "Fruit Jam", "Simmered fruit preserves"),
            ModelCourseInfo(7, "Quick Bread", "Rapid bake setting"),
            ModelCourseInfo(8, "Crust Control", "Light, Medium, or Dark crust selection")
        )
        ZOJIRUSHI_BB_HE10 -> listOf(
            ModelCourseInfo(1, "Regular White", "Classic compact loaf"),
            ModelCourseInfo(2, "Soft White", "Super tender sandwich bread"),
            ModelCourseInfo(3, "French Bread", "Crispy crust and airy crumb"),
            ModelCourseInfo(4, "Rice Flour Bread", "Wheat-free rice flour loaf"),
            ModelCourseInfo(5, "Fresh Butter", "Churn chilled heavy cream into butter"),
            ModelCourseInfo(6, "Bread Dough", "Kneaded dough for dinner rolls & pizza"),
            ModelCourseInfo(7, "Quick Loaf", "Rapid baking in 2 hours"),
            ModelCourseInfo(8, "Cake", "Quick batter tea cakes"),
            ModelCourseInfo(9, "Jam", "Fresh homemade preserves")
        )
        ZOJIRUSHI_BB_HD10 -> listOf(
            ModelCourseInfo(1, "Regular Bread", "Standard 1-lb white loaf"),
            ModelCourseInfo(2, "Soft Bread", "Fluffy soft textured crumb"),
            ModelCourseInfo(3, "Firm Bread", "Chewy artisan European crust"),
            ModelCourseInfo(4, "Dough", "Pizza & roll dough"),
            ModelCourseInfo(5, "Quick Loaf", "Rapid baking cycle"),
            ModelCourseInfo(6, "Cookie / Pasta", "Pastry dough mixing"),
            ModelCourseInfo(7, "Cake", "Quick sweet loaf"),
            ModelCourseInfo(8, "Jam", "Fresh fruit simmer")
        )
        ZOJIRUSHI_BB_HA10 -> listOf(
            ModelCourseInfo(1, "Basic White", "Everyday 1-lb loaf"),
            ModelCourseInfo(2, "Quick White", "Express single-rise bread"),
            ModelCourseInfo(3, "French Bread", "Crisp crust without shortening"),
            ModelCourseInfo(4, "Bread Dough", "Yeast dough for hand baking"),
            ModelCourseInfo(5, "Cookie / Pasta", "Pasta & cookie dough"),
            ModelCourseInfo(6, "Cake", "Sweet loaf"),
            ModelCourseInfo(7, "Jam", "Fruit jam")
        )
        UNIVERSAL_SMART_BAKER -> listOf(
            ModelCourseInfo(1, "Basic White", "Standard sandwich bread"),
            ModelCourseInfo(2, "French", "Crisp crust artisan loaf"),
            ModelCourseInfo(3, "Whole Wheat", "Grain extended cycle"),
            ModelCourseInfo(4, "Sweet Bread", "Enriched brioche & fruit loaf"),
            ModelCourseInfo(5, "Gluten Free", "Non-wheat starch loaf"),
            ModelCourseInfo(6, "Rapid / Express", "Fast 1.5 hr cycle"),
            ModelCourseInfo(7, "Dough", "Knead and bulk rise"),
            ModelCourseInfo(8, "Knead Only", "Gluten development only"),
            ModelCourseInfo(9, "Cake", "Quick tea bread"),
            ModelCourseInfo(10, "Jam", "Fruit simmer"),
            ModelCourseInfo(11, "Bake Only", "Manual oven heating"),
            ModelCourseInfo(12, "Custom Homemade", "User programmed stages", isHomemade = true)
        )
    }

    fun mapRecommendedCourse(standardCourse: ZojirushiMenuCourse): ModelCourseInfo {
        val courses = getCourses()
        val matchNumber = when (this) {
            ZOJIRUSHI_BB_PDC20 -> standardCourse.courseNumber
            ZOJIRUSHI_BB_PAC20 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 2
                ZojirushiMenuCourse.EUROPEAN -> 4 // French
                ZojirushiMenuCourse.MULTIGRAIN -> 2
                ZojirushiMenuCourse.GLUTEN_FREE -> 3
                ZojirushiMenuCourse.SALT_FREE -> 1
                ZojirushiMenuCourse.SUGAR_FREE -> 1
                ZojirushiMenuCourse.VEGAN -> 1
                ZojirushiMenuCourse.QUICK_WHITE -> 8
                ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 8
                ZojirushiMenuCourse.DOUGH -> 5
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 9 // Homemade 1
                ZojirushiMenuCourse.CAKE_JAM -> 6
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 9
            }
            ZOJIRUSHI_BB_CEC20 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 2
                ZojirushiMenuCourse.EUROPEAN -> 1 // Basic White
                ZojirushiMenuCourse.MULTIGRAIN -> 2
                ZojirushiMenuCourse.GLUTEN_FREE -> 1
                ZojirushiMenuCourse.SALT_FREE, ZojirushiMenuCourse.SUGAR_FREE, ZojirushiMenuCourse.VEGAN -> 1
                ZojirushiMenuCourse.QUICK_WHITE -> 4
                ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 5
                ZojirushiMenuCourse.DOUGH -> 3
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 9 // Sourdough Starter
                ZojirushiMenuCourse.CAKE_JAM -> 7
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 10
            }
            ZOJIRUSHI_BB_CCX20 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 3
                ZojirushiMenuCourse.EUROPEAN -> 1
                ZojirushiMenuCourse.MULTIGRAIN -> 3
                ZojirushiMenuCourse.GLUTEN_FREE -> 1
                ZojirushiMenuCourse.SALT_FREE, ZojirushiMenuCourse.SUGAR_FREE, ZojirushiMenuCourse.VEGAN -> 1
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 2
                ZojirushiMenuCourse.DOUGH -> 4
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 7
                ZojirushiMenuCourse.CAKE_JAM -> 5
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 8
            }
            ZOJIRUSHI_BB_KW20 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 3
                ZojirushiMenuCourse.EUROPEAN -> 5 // French
                ZojirushiMenuCourse.MULTIGRAIN -> 3
                ZojirushiMenuCourse.GLUTEN_FREE, ZojirushiMenuCourse.SALT_FREE, ZojirushiMenuCourse.SUGAR_FREE, ZojirushiMenuCourse.VEGAN -> 1
                ZojirushiMenuCourse.QUICK_WHITE -> 2
                ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 4
                ZojirushiMenuCourse.DOUGH -> 6
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 6 // Dough
                ZojirushiMenuCourse.CAKE_JAM -> 7
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 1
            }
            ZOJIRUSHI_BB_V20 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 3
                ZojirushiMenuCourse.EUROPEAN -> 4 // French
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 2
                ZojirushiMenuCourse.DOUGH -> 5
                ZojirushiMenuCourse.CAKE_JAM -> 6
                else -> 1
            }
            ZOJIRUSHI_BB_SSC10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 4
                ZojirushiMenuCourse.EUROPEAN -> 2 // European
                ZojirushiMenuCourse.MULTIGRAIN -> 6
                ZojirushiMenuCourse.GLUTEN_FREE -> 7
                ZojirushiMenuCourse.SALT_FREE -> 8
                ZojirushiMenuCourse.SUGAR_FREE -> 9
                ZojirushiMenuCourse.VEGAN -> 10
                ZojirushiMenuCourse.QUICK_WHITE -> 3
                ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 5
                ZojirushiMenuCourse.DOUGH -> 11
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 14 // Homemade custom
                ZojirushiMenuCourse.CAKE_JAM -> 13
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 14
            }
            ZOJIRUSHI_BB_ST10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 5
                ZojirushiMenuCourse.EUROPEAN -> 7 // French
                ZojirushiMenuCourse.MULTIGRAIN -> 1
                ZojirushiMenuCourse.GLUTEN_FREE -> 6 // Rice Flour
                ZojirushiMenuCourse.SALT_FREE, ZojirushiMenuCourse.SUGAR_FREE, ZojirushiMenuCourse.VEGAN -> 2
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 9
                ZojirushiMenuCourse.DOUGH -> 12
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 4 // Natural Yeast Starter
                ZojirushiMenuCourse.CAKE_JAM -> 13
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 15
            }
            ZOJIRUSHI_BB_HAC10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 1
                ZojirushiMenuCourse.EUROPEAN -> 2 // French
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 7
                ZojirushiMenuCourse.DOUGH -> 3
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 3 // Dough
                ZojirushiMenuCourse.CAKE_JAM -> 5
                else -> 1
            }
            ZOJIRUSHI_BB_HE10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 1
                ZojirushiMenuCourse.EUROPEAN -> 3 // French
                ZojirushiMenuCourse.GLUTEN_FREE -> 4 // Rice flour
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 7
                ZojirushiMenuCourse.DOUGH -> 6
                ZojirushiMenuCourse.CAKE_JAM -> 8
                else -> 2
            }
            ZOJIRUSHI_BB_HD10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 1
                ZojirushiMenuCourse.EUROPEAN -> 3 // Firm
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 5
                ZojirushiMenuCourse.DOUGH -> 4
                ZojirushiMenuCourse.CAKE_JAM -> 7
                else -> 2
            }
            ZOJIRUSHI_BB_HA10 -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 1
                ZojirushiMenuCourse.EUROPEAN -> 3 // French
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 2
                ZojirushiMenuCourse.DOUGH -> 4
                ZojirushiMenuCourse.CAKE_JAM -> 6
                else -> 1
            }
            UNIVERSAL_SMART_BAKER -> when (standardCourse) {
                ZojirushiMenuCourse.WHITE -> 1
                ZojirushiMenuCourse.WHOLE_WHEAT -> 3
                ZojirushiMenuCourse.EUROPEAN -> 2
                ZojirushiMenuCourse.GLUTEN_FREE -> 5
                ZojirushiMenuCourse.QUICK_WHITE, ZojirushiMenuCourse.QUICK_WHOLE_WHEAT -> 6
                ZojirushiMenuCourse.DOUGH -> 7
                ZojirushiMenuCourse.SOURDOUGH_STARTER -> 7
                ZojirushiMenuCourse.CAKE_JAM -> 9
                ZojirushiMenuCourse.HOMEMADE_CUSTOM -> 12
                else -> 1
            }
        }
        return courses.find { it.courseNumber == matchNumber } ?: courses.first()
    }
}

data class ModelCourseInfo(
    val courseNumber: Int,
    val courseName: String,
    val description: String,
    val isHomemade: Boolean = false
)

enum class ZojirushiMenuCourse(val courseNumber: Int, val courseName: String, val description: String) {
    WHITE(1, "White", "Classic sandwich loaf with soft crumb"),
    WHOLE_WHEAT(2, "Whole Wheat", "100% whole grain with extended preheat and hydration"),
    EUROPEAN(3, "European", "Crusty artisan loaf with crisp top crust via lid heater"),
    MULTIGRAIN(4, "Multigrain", "Hearty grains with auto-dispenser seed and kernel addition"),
    GLUTEN_FREE(5, "Gluten Free", "Single intensive mix and high-heat starch bake"),
    SALT_FREE(6, "Salt Free", "Low sodium formula with tailored yeast activity"),
    SUGAR_FREE(7, "Sugar Free", "Extended proof for natural flour sweetness"),
    VEGAN(8, "Vegan", "Plant-based milk and dairy-free enrichment"),
    QUICK_WHITE(9, "Quick White", "Rapid cycle with instant yeast in under 2 hours"),
    QUICK_WHOLE_WHEAT(10, "Quick Whole Wheat", "Accelerated whole grain cycle"),
    DOUGH(11, "Dough", "Knead and proof cycle for shaping and oven baking"),
    SOURDOUGH_STARTER(12, "Sourdough Starter", "Precise 82°F chamber temperature for wild yeast culturing"),
    CAKE_JAM(13, "Cake / Jam", "Stirring and steady simmer heating"),
    HOMEMADE_CUSTOM(14, "Homemade Custom", "Fully programmable stages stored in memory slots")
}

enum class BakingStageType(val displayName: String, val shortCode: String) {
    PREHEAT("Preheat & Temp Equalize", "PRE"),
    KNEAD_1("Hydration & Flour Mixing", "KN1"),
    REST_AUTOLYSE("Rest & Autolyse", "RST"),
    KNEAD_2("Gluten Development & Add-in", "KN2"),
    RISE_1("Bulk Fermentation", "RS1"),
    PUNCH_DOWN("Degas & Paddle Removal", "PCH"),
    RISE_2("Intermediate Proof", "RS2"),
    RISE_3("Final Proof & Loaf Rise", "RS3"),
    BAKE("Artisan Crust Bake", "BAK"),
    KEEP_WARM("Crust Preservation Warm", "WRM")
}

enum class CrustShade(val label: String, val bakeTempOffset: Int, val description: String) {
    LIGHT("Light", -15, "Delicate golden, soft tender thin crust"),
    MEDIUM("Medium", 0, "Classic balanced golden-brown crust"),
    DARK("Dark", +15, "Deep caramel, pronounced roasted notes"),
    ARTISAN_CRISPY("Artisan Crispy", +25, "Extra crisp blistered crust with top element boost")
}

enum class LoafSize(val label: String, val weightGrams: Int, val flourMultiplier: Float) {
    ONE_LB("1.0 lb", 450, 0.75f),
    ONE_HALF_LB("1.5 lb", 680, 1.0f),
    TWO_LB("2.0 lb", 900, 1.33f)
}

enum class YeastType(val label: String, val bloomNeeded: Boolean, val fermentationSpeed: String) {
    SOURDOUGH_STARTER("Active Sourdough Levain (100% Hydration)", false, "Slow & complex natural fermentation"),
    INSTANT_YEAST("Instant / Bread Machine Yeast", false, "Fast direct mix into dry ingredients"),
    ACTIVE_DRY_YEAST("Active Dry Yeast", true, "Requires 10-min lukewarm liquid activation"),
    FRESH_CAKE_YEAST("Fresh Baker's Compressed Yeast", true, "Artisanal aroma, crumble into liquid")
}

enum class RecipeCategory(
    val label: String,
    val description: String,
    val emoji: String,
    val iconName: String
) {
    ALL("All Recipes", "All recipes in library", "📚", "all"),
    FAVORITES("Favorites", "Quick access to your most-baked & bookmarked loaves", "⭐", "favorites"),
    SOURDOUGH("Sourdough", "Natural levain & wild fermentation", "🌾", "sourdough"),
    YEAST_BREAD("Yeast Bread", "Classic sandwich, artisan & rustic loaves", "🍞", "yeast_bread"),
    SWEET_LOAVES("Sweet Loaves", "Enriched brioche, milk bread & fruit loaves", "🍯", "sweet_loaves"),
    WHOLE_GRAIN("Whole Grain", "100% whole wheat, spelt, rye & ancient grains", "🌾", "whole_grain"),
    GLUTEN_FREE("Gluten-Free", "Specialized allergen-safe bread formulas", "🌿", "gluten_free"),
    CUSTOM("Custom Recipes", "Your own custom recipes in Room DB", "💾", "custom");

    companion object {
        val userSelectable: List<RecipeCategory>
            get() = listOf(SOURDOUGH, YEAST_BREAD, SWEET_LOAVES, WHOLE_GRAIN, GLUTEN_FREE)
    }
}

typealias FlourCategory = RecipeCategory

enum class MachineState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

@JsonClass(generateAdapter = true)
data class RecipeIngredient(
    val id: String,
    val name: String,
    val amount: String,
    val bakerPercentage: Float = 0f,
    val stageToAdd: BakingStageType = BakingStageType.PREHEAT,
    val isAutoDispenser: Boolean = false,
    val notes: String = ""
)

@JsonClass(generateAdapter = true)
data class BakingStage(
    val type: BakingStageType,
    val title: String,
    val durationMinutes: Int,
    val targetTempF: Int,
    val heatingPowerPercent: Int = 0,
    val motorRpm: Int = 0,
    val hasIngredientAlert: Boolean = false,
    val alertTitle: String? = null,
    val alertDescription: String? = null,
    val chimePrompt: String? = null
)

@JsonClass(generateAdapter = true)
data class BreadRecipe(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: RecipeCategory,
    val primaryFlour: String,
    val specialtyFlours: List<String> = emptyList(),
    val yeastType: YeastType,
    val defaultLoafSize: LoafSize = LoafSize.TWO_LB,
    val defaultCrust: CrustShade = CrustShade.MEDIUM,
    val hydrationPercent: Int, // e.g. 68%
    val totalTimeMinutes: Int,
    val ingredients: List<RecipeIngredient>,
    val stages: List<BakingStage>,
    val bakerNotes: List<String> = emptyList(),
    val isCustom: Boolean = false,
    val zojirushiRecommendedCourse: ZojirushiMenuCourse = ZojirushiMenuCourse.EUROPEAN,
    val photoUri: String? = null,
    val isFavorite: Boolean = false,
    val bakeCount: Int = 0
)

data class IngredientAlert(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val stageType: BakingStageType,
    val itemsToAdd: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class StepCompletionAlert(
    val id: String = System.currentTimeMillis().toString(),
    val completedStage: BakingStage,
    val nextStage: BakingStage?,
    val isProofing: Boolean,
    val isBaking: Boolean,
    val title: String,
    val message: String,
    val actionPrompt: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MachineTelemetry(
    val model: BreadMakerModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20,
    val isConnected: Boolean = true,
    val machineName: String = "Zojirushi Virtuoso® Plus BB-PDC20",
    val connectionType: String = "Bluetooth 5.2 LE (Zojirushi Connect)",
    val signalDbm: Int = -54,
    val firmwareVersion: String = "v4.1.2-bbpdc20",
    val state: MachineState = MachineState.IDLE,
    val activeRecipe: BreadRecipe? = null,
    val currentStageIndex: Int = 0,
    val stageElapsedSeconds: Int = 0,
    val stageDurationSeconds: Int = 0,
    val totalElapsedSeconds: Int = 0,
    val totalDurationSeconds: Int = 0,
    val chamberTempF: Float = 75f,
    val chamberTargetTempF: Float = 75f,
    val proofTempOffsetF: Int = 0, // +/- 10°F user adjustment
    val bakeTempOffsetF: Int = 0,  // +/- 30°F user adjustment
    val crustBoosterActive: Boolean = false, // +20°F radiant top heat in last 10 mins
    val selectedCrust: CrustShade = CrustShade.MEDIUM,
    val selectedLoafSize: LoafSize = LoafSize.TWO_LB,
    val lidOpen: Boolean = false,
    val panLocked: Boolean = true,
    val interiorLampOn: Boolean = false,
    val motorRpm: Int = 0,
    // Zojirushi BB-PDC20 specific hardware telemetry:
    val leftBladeRpm: Int = 0,
    val rightBladeRpm: Int = 0,
    val lidHeaterActive: Boolean = false,  // Upper lid heater for even browning
    val baseHeaterActive: Boolean = false, // Lower base heater
    val heatingElementActive: Boolean = false,
    val zojirushiMenuCourse: ZojirushiMenuCourse = ZojirushiMenuCourse.EUROPEAN,
    val homemadeMemorySlot: Int = 1, // Memory 1, 2, or 3
    val paddleRemoved: Boolean = false,
    val twinPaddlesRemoved: Boolean = false,
    val paddleRemovalReminderEnabled: Boolean = true,
    val simulationSpeed: Int = 1, // 1x normal, 10x, 30x fast demo
    val activeAlert: IngredientAlert? = null,
    val activeStepCompletionAlert: StepCompletionAlert? = null,
    val delayStartMinutes: Int = 0,
    val converterSettings: ConverterSettings = ConverterSettings()
) {
    val currentStage: BakingStage?
        get() = activeRecipe?.stages?.getOrNull(currentStageIndex)

    val isCurrentStageProofing: Boolean
        get() = currentStage?.type in listOf(BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3, BakingStageType.REST_AUTOLYSE)

    val isCurrentStageBaking: Boolean
        get() = currentStage?.type == BakingStageType.BAKE

    val progressFraction: Float
        get() = if (totalDurationSeconds > 0) {
            (totalElapsedSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val stageProgressFraction: Float
        get() = if (stageDurationSeconds > 0) {
            (stageElapsedSeconds.toFloat() / stageDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingSeconds: Int
        get() = (totalDurationSeconds - totalElapsedSeconds).coerceAtLeast(0)

    val stageRemainingSeconds: Int
        get() = (stageDurationSeconds - stageElapsedSeconds).coerceAtLeast(0)
}
