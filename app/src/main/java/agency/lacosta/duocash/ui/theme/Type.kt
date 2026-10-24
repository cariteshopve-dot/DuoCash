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
private fun variable(res: Int, weight: Int) =
    Font(res, FontWeight(weight), variationSettings = FontVariation.Settings(FontVariation.weight(weight)))

/** Titulares y números grandes. */
val Bricolage = FontFamily(
    variable(R.font.bricolage_grotesque, 600),
    variable(R.font.bricolage_grotesque, 800),
)

/** Textos. */
val Instrument = FontFamily(
    variable(R.font.instrument_sans, 400),
    variable(R.font.instrument_sans, 500),
    variable(R.font.instrument_sans, 600),
)

private val base = Typography()

val DuoTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
    displayMedium = base.displayMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
    displaySmall = base.displaySmall.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
    headlineLarge = base.headlineLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.ExtraBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Instrument, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Instrument),
    bodyMedium = base.bodyMedium.copy(fontFamily = Instrument),
    bodySmall = base.bodySmall.copy(fontFamily = Instrument),
    labelLarge = base.labelLarge.copy(fontFamily = Instrument, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Instrument, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Instrument, fontWeight = FontWeight.Medium),
)

/** Montos en las listas: Bricolage, para que los números se lean como marca. */
val AmountStyle = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
