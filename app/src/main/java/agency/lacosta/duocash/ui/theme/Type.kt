package agency.lacosta.duocash.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import agency.lacosta.duocash.R

@OptIn(ExperimentalTextApi::class)
private fun sora(weight: Int) =
    Font(R.font.sora, FontWeight(weight), variationSettings = FontVariation.Settings(FontVariation.weight(weight)))

/** Sora para todo: textos, títulos y números. */
val Sora = FontFamily(sora(400), sora(600), sora(700))

private val base = Typography()

private fun TextStyle.sora(weight: FontWeight, tracking: Float = 0f) =
    copy(fontFamily = Sora, fontWeight = weight, letterSpacing = if (tracking != 0f) tracking.sp else letterSpacing)

val DuoTypography = Typography(
    displayLarge = base.displayLarge.sora(FontWeight.Bold, -1.5f),
    displayMedium = base.displayMedium.sora(FontWeight.Bold, -1.5f),
    displaySmall = base.displaySmall.sora(FontWeight.Bold, -1f),
    headlineLarge = base.headlineLarge.sora(FontWeight.Bold, -0.5f),
    headlineMedium = base.headlineMedium.sora(FontWeight.Bold, -0.5f),
    headlineSmall = base.headlineSmall.sora(FontWeight.SemiBold),
    titleLarge = base.titleLarge.sora(FontWeight.Bold, -0.4f),
    titleMedium = base.titleMedium.sora(FontWeight.SemiBold),
    titleSmall = base.titleSmall.sora(FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.sora(FontWeight.Normal),
    bodyMedium = base.bodyMedium.sora(FontWeight.Normal),
    bodySmall = base.bodySmall.sora(FontWeight.Normal),
    labelLarge = base.labelLarge.sora(FontWeight.SemiBold),
    labelMedium = base.labelMedium.sora(FontWeight.Normal),
    labelSmall = base.labelSmall.sora(FontWeight.Normal),
)

/** Montos en las listas y tarjetas. */
val AmountStyle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

/** El balance grande del inicio. */
val BalanceStyle = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Bold, fontSize = 44.sp, letterSpacing = (-1.5).sp)
