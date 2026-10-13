package com.example.data.model

data class TroubleshootingIssue(
    val id: String,
    val symptom: String,
    val iconEmoji: String,
    val shortDescription: String,
    val keywords: List<String>,
    val primaryCause: String,
    val immediateFix: String,
    val machineTuning: String,
    val hydrationAdjustmentPercent: Int? = null,
    val suggestedCrustChange: CrustShade? = null,
    val tipQuote: String
)

object BreadTroubleshootingKnowledgeBase {
    val ISSUES = listOf(
        TroubleshootingIssue(
            id = "sunken_top",
            symptom = "Collapsed / Sunken Top",
            iconEmoji = "🌋",
            shortDescription = "Loaf rose in proofing but caved in during or after baking",
            keywords = listOf("sunken", "collapsed", "cave", "crater", "sink", "dip", "fell", "deflate", "valley"),
            primaryCause = "Over-fermentation or excessive liquid ratio. The yeast produced more carbon dioxide than the gluten structure could hold before heat set the crumb, causing the roof to cave in.",
            immediateFix = "1. Reduce water/liquid by 15g–25g (drop hydration by 2%–4%).\n2. Reduce instant yeast by 1/4 tsp (or reduce sourdough starter by 20g).\n3. Increase salt by 1/4 tsp to regulate and strengthen yeast fermentation.",
            machineTuning = "Avoid opening the bread machine lid once the final rise stage begins. Ensure room temperature is between 68°F–75°F (warm rooms accelerate proofing).",
            hydrationAdjustmentPercent = -3,
            tipQuote = "\"A loaf that rises fast and collapses is usually drowning or over-yeasted.\""
        ),
        TroubleshootingIssue(
            id = "dense_gummy_crumb",
            symptom = "Dense, Gummy, or Heavy Crumb",
            iconEmoji = "🧱",
            shortDescription = "Sticky texture, wet crumb, slice cuts gum up the knife",
            keywords = listOf("gummy", "dense", "heavy", "wet", "sticky", "uncooked", "doughy", "raw", "sponge", "glue"),
            primaryCause = "Under-baking, cutting loaf too early while hot, or under-developed gluten/insufficient proofing.",
            immediateFix = "1. Allow baked loaf to cool completely on a wire rack for at least 1.5–2 hours before slicing (steam must redistribute).\n2. If crumb is consistently under-baked, extend the Bake stage duration by 5–10 minutes.\n3. Verify your flour has at least 12% protein (use unbleached bread flour instead of AP flour).",
            machineTuning = "On Zojirushi machines, use 'European' or 'Dark Crust' setting for longer bake time and higher internal loaf temperature (target 195°F–205°F internally).",
            hydrationAdjustmentPercent = -2,
            tipQuote = "\"Never slice hot breadmaker loaves! Gelatinized starches need cooling to firm up.\""
        ),
        TroubleshootingIssue(
            id = "flat_brick",
            symptom = "Flat Loaf / Did Not Rise",
            iconEmoji = "🍞",
            shortDescription = "Loaf is short, dense, and barely expanded above the pan blade",
            keywords = listOf("flat", "brick", "no rise", "didn't rise", "dead", "short", "heavy", "stunted", "rock"),
            primaryCause = "Inactive/expired yeast, water that was too hot (>115°F) which killed yeast, direct contact between salt and yeast during delay timer, or forgetting to insert the kneading blade.",
            immediateFix = "1. Test yeast in 1/4 cup lukewarm water with a pinch of sugar; if it doesn't foam in 10 minutes, replace it.\n2. When layering ingredients: place liquids first, flour second, salt/sugar in corners, and yeast in a dry crater on top.\n3. Confirm kneading blade is firmly seated on the pan driveshaft before loading.",
            machineTuning = "If using pre-set Delay Timer, ensure yeast remains completely dry on top of the flour mound until the preheat phase finishes.",
            tipQuote = "\"Keep salt and liquids quarantined from yeast until the motor starts spinning!\""
        ),
        TroubleshootingIssue(
            id = "mushroom_overflow",
            symptom = "Mushrooming / Pan Overflow",
            iconEmoji = "🍄",
            shortDescription = "Dough expanded over pan rims and touched the lid window",
            keywords = listOf("mushroom", "overflow", "too big", "lid", "spill", "huge", "top heavy", "exploded"),
            primaryCause = "Too much flour, yeast, or baking a 2-lb recipe in a 1-lb compact bread machine (e.g., Zojirushi Maestro BB-SSC10 or Mini BB-HAC10).",
            immediateFix = "1. Scale down recipe total flour weight to pan capacity: max 560g for 2.0-lb pans, max 300g for 1.0-lb compact pans.\n2. Reduce instant yeast by 1/2 tsp.\n3. Weigh flour on a gram scale rather than scooping with measuring cups (which can pack 20% excess flour).",
            machineTuning = "Select the correct Loaf Size setting (1.5 lb vs 2.0 lb) on the machine control panel.",
            tipQuote = "\"Compact 1-lb pans require under 300g total flour—always check machine capacity!\""
        ),
        TroubleshootingIssue(
            id = "pale_soft_crust",
            symptom = "Crust Too Pale or Soft",
            iconEmoji = "🥖",
            shortDescription = "Lacks golden browning, top crust is pale white or leathery",
            keywords = listOf("pale", "white", "soft", "light", "not brown", "leathery", "blonde", "doughy crust"),
            primaryCause = "Bake temperature too low, missing lid heating element, or formula lacks sugars/milk solids for Maillard browning.",
            immediateFix = "1. Add 1 tsp honey, sugar, or dry milk powder to promote caramelized crust browning.\n2. Switch Crust Shade selector to 'Dark' or 'Medium'.\n3. On machines without lid heaters, brush top with egg wash or melted butter immediately after punch-down.",
            machineTuning = "On Zojirushi BB-PDC20 and BB-PAC20, verify Lid Heater is enabled. European cycle (#3) engages radiant upper heat specifically for deep golden crusts.",
            suggestedCrustChange = CrustShade.DARK,
            tipQuote = "\"A teaspoon of sugar or dry milk speeds caramelization and enriches golden crust tones.\""
        ),
        TroubleshootingIssue(
            id = "burnt_dark_crust",
            symptom = "Crust Too Dark or Burnt",
            iconEmoji = "🔥",
            shortDescription = "Thick, hard, charred or overly browned crust with bitter aroma",
            keywords = listOf("burnt", "dark", "charred", "black", "bitter", "thick crust", "hard crust", "burned"),
            primaryCause = "Excessive sugar/honey or dairy in recipe caramelizing too aggressively, or Crust setting on Dark with a high-sugar enriched dough.",
            immediateFix = "1. Reduce sugar/sweetener by 1–2 tbsp.\n2. Set Crust Shade to 'Light'.\n3. Remove loaf from the pan immediately when the end chime sounds to prevent residual pan heat from hardening the crust.",
            machineTuning = "Use 'Basic Light' or 'Sweet Bread' course which bakes at slightly reduced chamber temperatures (340°F vs 375°F).",
            suggestedCrustChange = CrustShade.LIGHT,
            tipQuote = "\"Enriched sweet breads caramelize rapidly—always pair sweet doughs with Light crust.\""
        ),
        TroubleshootingIssue(
            id = "tunneling_holes",
            symptom = "Large Cavity Under Top Crust",
            iconEmoji = "🕳️",
            shortDescription = "Huge air pocket or gap directly beneath upper crust (tunneling)",
            keywords = listOf("tunnel", "cavity", "hole", "cavern", "air pocket", "bubble", "gap", "separated"),
            primaryCause = "Dough was under-kneaded, insufficient degassing during punch-down, or trapped steam pocket during final rise.",
            immediateFix = "1. Ensure flour protein is sufficient (12%+ bread flour) to produce a uniform gluten network.\n2. Perform gentle manual punch-down at the mid-cycle alert.\n3. Reduce hydration by 2% if water is pooling on top of dough during proofing.",
            machineTuning = "On twin-blade machines (Virtuoso BB-PDC20/BB-CEC20), ensure both blades are rotating smoothly to knead both sides of the horizontal pan evenly.",
            hydrationAdjustmentPercent = -2,
            tipQuote = "\"Uneven kneading creates pockets of air that expand into tunnels during oven spring.\""
        ),
        TroubleshootingIssue(
            id = "crumbly_dry_texture",
            symptom = "Dry, Crumbly, or Stale Slices",
            iconEmoji = "🌾",
            shortDescription = "Bread falls apart when sliced, dry mouthfeel, stales in 24 hours",
            keywords = listOf("crumbly", "dry", "stale", "falls apart", "coarse", "dusty", "sand"),
            primaryCause = "Hydration too low, insufficient fat/butter/oil, or too much whole grain flour without extra hydration.",
            immediateFix = "1. Increase liquid/water by 15g–20g (+2% to +3% hydration).\n2. Add 1 tbsp butter, olive oil, or tangzhong water roux to lock in crumb moisture.\n3. Whole wheat and rye absorb significantly more water than white flour—always add 2 tbsp extra water for whole grains.",
            machineTuning = "Store sliced bread in a paper bag or sealed bread box with cut side down on cutting board. Avoid refrigeration.",
            hydrationAdjustmentPercent = 3,
            tipQuote = "\"Fat (butter, oil) coats gluten strands and locks moisture inside the crumb for days.\""
        ),
        TroubleshootingIssue(
            id = "paddle_stuck",
            symptom = "Kneading Paddle Baked Inside Loaf",
            iconEmoji = "🥄",
            shortDescription = "Blade tore a large hole in bottom of bread when removing",
            keywords = listOf("paddle", "blade", "stuck", "hook", "hole", "tear", "bottom", "removal"),
            primaryCause = "Forgot to extract blade during punch-down beep or pan driveshaft lacked nonstick oil.",
            immediateFix = "1. Lightly grease the metal paddle post with baking spray before assembling.\n2. Enable CrustCraft's 'Paddle Removal Reminder' alert.\n3. When beep sounds before Rise 3 (approx 1h45m before finish), open lid with floured hands and lift blade(s) out with removal hook.",
            machineTuning = "CrustCraft provides automated alerts specifically at the Zojirushi punch-down stage to remind you to take the paddle out.",
            tipQuote = "\"Removing the paddle at the punch-down alert leaves only tiny pinholes in your loaf!\""
        ),
        TroubleshootingIssue(
            id = "lopsided_loaf",
            symptom = "Lopsided / Slanted Loaf",
            iconEmoji = "⚡",
            shortDescription = "One end of horizontal loaf is tall while the other is short",
            keywords = listOf("lopsided", "slanted", "uneven", "crooked", "tilted", "one side", "horizontal", "slope"),
            primaryCause = "In dual-blade machines (BB-PDC20, BB-CEC20), dough migrated to one side during final rise, or one blade wasn't seated.",
            immediateFix = "1. At the final punch-down chime, open lid and gently reshape dough into an even horizontal cylinder along the pan length.\n2. Check that both paddles are seated at the same angle.",
            machineTuning = "Ensure the bread maker is resting on a level, sturdy countertop (vibrations on unlevel counters drift dough).",
            tipQuote = "\"At the punch-down alert, give the dough ball a quick 5-second gentle tuck into the pan center.\""
        ),
        TroubleshootingIssue(
            id = "overly_sour",
            symptom = "Overly Sour or Sharp Alcohol Taste",
            iconEmoji = "🍋",
            shortDescription = "Pungent vinegar bite or strong alcohol aroma overpowering bread",
            keywords = listOf("sour", "acidic", "vinegar", "alcohol", "sharp", "acetone", "tart"),
            primaryCause = "Sourdough starter was over-ripe, starter inoculate percentage was too high, or proofing temp was too hot.",
            immediateFix = "1. Feed starter 4–6 hours prior to baking so it is at young peak rather than falling and acidic.\n2. Reduce levain percentage from 25% down to 15%–18%.\n3. Add 1/2 tsp sugar to balance lactic vs acetic acid ratios.",
            machineTuning = "Use cooler water (68°F) for dough mixing to prolong sweet lactic fermentation.",
            tipQuote = "\"Feed your levain shortly before mixing to favor sweet creamy lactic notes over sharp vinegar.\""
        )
    )

    fun diagnose(query: String): List<TroubleshootingIssue> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isBlank()) return ISSUES

        val scored = ISSUES.map { issue ->
            var score = 0
            if (issue.symptom.lowercase().contains(trimmed)) score += 12
            if (trimmed.contains(issue.id.replace("_", " "))) score += 10
            issue.keywords.forEach { kw ->
                if (trimmed.contains(kw)) score += 8
                if (kw.contains(trimmed)) score += 5
            }
            if (issue.primaryCause.lowercase().contains(trimmed)) score += 3
            if (issue.immediateFix.lowercase().contains(trimmed)) score += 2
            issue to score
        }.filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }

        return if (scored.isNotEmpty()) scored else ISSUES
    }
}
