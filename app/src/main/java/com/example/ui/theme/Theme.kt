package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BreadWarmWheat,
    onPrimary = BreadCrustDark,
    primaryContainer = BreadCrustMedium,
    onPrimaryContainer = FlourWhite,
    secondary = BreadAmber,
    onSecondary = BreadCrustDark,
    secondaryContainer = OvenDarkSurfaceVariant,
    onSecondaryContainer = FlourWhite,
    tertiary = BreadCrustGolden,
    background = OvenDarkBg,
    onBackground = DoughCream,
    surface = OvenDarkSurface,
    onSurface = DoughCream,
    surfaceVariant = OvenDarkSurfaceVariant,
    onSurfaceVariant = BreadWarmWheat
)

private val LightColorScheme = lightColorScheme(
    primary = CrustPrimary,
    onPrimary = CrustOnPrimary,
    primaryContainer = CrustPrimaryContainer,
    onPrimaryContainer = CrustOnPrimaryContainer,
    secondary = CrustSecondary,
    onSecondary = CrustOnSecondary,
    secondaryContainer = CrustSecondaryContainer,
    onSecondaryContainer = CrustOnSecondaryContainer,
    tertiary = CrustTertiary,
    onTertiary = CrustOnTertiary,
    background = CrustBackgroundLight,
    onBackground = CrustOnBackgroundLight,
    surface = CrustSurfaceLight,
    onSurface = CrustOnSurfaceLight,
    surfaceVariant = CrustSurfaceVariantLight,
    onSurfaceVariant = CrustOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our artisanal bakery palette for distinctive warm design
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
