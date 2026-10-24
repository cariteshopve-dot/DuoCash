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
    val header: Color,
    val onHeader: Color,
    val onHeaderMuted: Color,
    val income: Color,
    val waves: List<Color>,
)

private val LightScheme = lightColorScheme(
    primary = Sol,
    onPrimary = MarProfundo,
    primaryContainer = Sol,
    onPrimaryContainer = MarProfundo,
    secondary = Laguna,
    onSecondary = MarProfundo,
    secondaryContainer = ArenaOscura,
    onSecondaryContainer = MarProfundo,
    tertiary = Salitre,
    onTertiary = MarProfundo,
    background = Arena,
    onBackground = MarProfundo,
    surface = Arena,
    onSurface = MarProfundo,
    surfaceVariant = ArenaClara,
    onSurfaceVariant = Reef,
    surfaceContainerLowest = ArenaClara,
    surfaceContainerLow = ArenaClara,
    surfaceContainer = ArenaClara,
    surfaceContainerHigh = ArenaClara,
    surfaceContainerHighest = ArenaOscura,
    outline = MarProfundo.copy(alpha = 0.35f),
    outlineVariant = MarProfundo.copy(alpha = 0.15f),
    error = Sunset,
    onError = MarProfundo,
)

private val DarkScheme = darkColorScheme(
    primary = Sol,
    onPrimary = MarProfundo,
    primaryContainer = Sol,
    onPrimaryContainer = MarProfundo,
    secondary = Laguna,
    onSecondary = MarProfundo,
    secondaryContainer = MarSuperficie,
    onSecondaryContainer = Arena,
    tertiary = Salitre,
    onTertiary = MarProfundo,
    background = MarProfundo,
    onBackground = Arena,
    surface = MarProfundo,
    onSurface = Arena,
    surfaceVariant = MarSuperficie,
    onSurfaceVariant = Salitre,
    surfaceContainerLowest = MarNoche,
    surfaceContainerLow = MarSuperficie,
    surfaceContainer = MarSuperficie,
    surfaceContainerHigh = MarSuperficie,
    surfaceContainerHighest = MarSuperficie,
    outline = Arena.copy(alpha = 0.35f),
    outlineVariant = Arena.copy(alpha = 0.15f),
    error = Sunset,
    onError = MarProfundo,
)

private val LightDuo = DuoColors(
    header = MarProfundo,
    onHeader = Arena,
    onHeaderMuted = Salitre,
    income = Color(0xFF1F7F7F),
    waves = listOf(Laguna, Salitre, Arena),
)

private val DarkDuo = DuoColors(
    header = MarNoche,
    onHeader = Arena,
    onHeaderMuted = Salitre,
    income = Laguna,
    waves = listOf(Laguna, Salitre, MarProfundo),
)

val LocalDuoColors = staticCompositionLocalOf { LightDuo }

@Composable
fun DuoCashTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    CompositionLocalProvider(LocalDuoColors provides if (dark) DarkDuo else LightDuo) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = DuoTypography,
            content = content,
        )
    }
}
