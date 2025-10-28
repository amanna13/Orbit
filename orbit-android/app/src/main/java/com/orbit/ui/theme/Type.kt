package com.orbit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.orbit.R

// Register Poppins font family with specific weights
val Poppins = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with. Material3's Typography doesn't accept a
// `defaultFontFamily` parameter, so we set fontFamily = Poppins on each TextStyle.
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Poppins),
    displayMedium = TextStyle(fontFamily = Poppins),
    displaySmall = TextStyle(fontFamily = Poppins),

    headlineLarge = TextStyle(fontFamily = Poppins),
    headlineMedium = TextStyle(fontFamily = Poppins),
    headlineSmall = TextStyle(fontFamily = Poppins),

    titleLarge = TextStyle(fontFamily = Poppins),
    titleMedium = TextStyle(fontFamily = Poppins),
    titleSmall = TextStyle(fontFamily = Poppins),

    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = Poppins),
    bodySmall = TextStyle(fontFamily = Poppins),

    labelLarge = TextStyle(fontFamily = Poppins),
    labelMedium = TextStyle(fontFamily = Poppins),
    labelSmall = TextStyle(fontFamily = Poppins)
)