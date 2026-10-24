package agency.lacosta.duocash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import agency.lacosta.duocash.data.settings.ThemeMode

/** Colores propios de DuoCash que Material no tiene. */
@Immutable
data class DuoColors(
    val card: Color,
    val cardLine: Color,
    val iconBg: Color,
    val iconTint: Color,
    val income: Color,
    val incomeBg: Color,
)

private val NightScheme = darkColorScheme(
    primary = Violet,
    onPrimary = Ink,
    primaryContainer = VioletSoft,
    onPrimaryContainer = Violet,
    secondary = Mint,
    onSecondary = Ink,
    secondaryContainer = NightRaised,
    onSecondaryContainer = Snow,
    tertiary = Mint,
    onTertiary = Ink,
    background = Ink,
    onBackground = Snow,
    surface = Ink,
    onSurface = Snow,
    surfaceVariant = Night,
    onSurfaceVariant = Mist,
    surfaceContainerLowest = Ink,
    surfaceContainerLow = Night,
    surfaceContainer = Night,
    surfaceContainerHigh = NightRaised,
    surfaceContainerHighest = NightRaised,
    outline = NightLine,
    outlineVariant = NightLine,
    error = Coral,
    onError = Ink,
)

private val DayScheme = lightColorScheme(
    primary = VioletDeep,
    onPrimary = Card,
    primaryContainer = VioletMist,
    onPrimaryContainer = VioletDeep,
    secondary = Pine,
    onSecondary = Card,
    secondaryContainer = VioletMist,
    onSecondaryContainer = Ink,
    tertiary = Pine,
    onTertiary = Card,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Card,
    onSurfaceVariant = Slate,
    surfaceContainerLowest = Card,
    surfaceContainerLow = Card,
    surfaceContainer = Card,
    surfaceContainerHigh = Card,
    surfaceContainerHighest = PaperLine,
    outline = PaperLine,
    outlineVariant = PaperLine,
    error = Brick,
    onError = Card,
)

private val NightDuo = DuoColors(
    card = Night,
    cardLine = NightLine,
    iconBg = VioletSoft,
    iconTint = Violet,
    income = Mint,
    incomeBg = MintSoft,
)

private val DayDuo = DuoColors(
    card = Card,
    cardLine = PaperLine,
    iconBg = VioletMist,
    iconTint = VioletDeep,
    income = Pine,
    incomeBg = PineMist,
)

val LocalDuoColors = staticCompositionLocalOf { NightDuo }

/** Noche es el tema por defecto; "Sistema" sigue al teléfono. */
@Composable
fun DuoCashTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    CompositionLocalProvider(LocalDuoColors provides if (dark) NightDuo else DayDuo) {
        MaterialTheme(
            colorScheme = if (dark) NightScheme else DayScheme,
            typography = DuoTypography,
            content = content,
        )
    }
}
