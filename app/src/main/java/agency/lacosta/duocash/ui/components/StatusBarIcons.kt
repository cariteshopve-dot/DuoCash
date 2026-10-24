package agency.lacosta.duocash.ui.components

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Ajusta el color de los íconos de la barra de estado a lo que hay detrás.
 * [onDarkBackground] fuerza íconos claros, por ejemplo sobre la cabecera Mar profundo.
 */
@Composable
fun StatusBarIcons(onDarkBackground: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !onDarkBackground
    }
}
