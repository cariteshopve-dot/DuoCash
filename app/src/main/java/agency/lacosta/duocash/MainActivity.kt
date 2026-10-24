package agency.lacosta.duocash

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import agency.lacosta.duocash.data.settings.Settings
import agency.lacosta.duocash.ui.DuoCashNavHost
import agency.lacosta.duocash.ui.theme.DuoCashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Sin la franja gris que Android pone detrás de los botones de navegación: se ve el fondo de la app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val settingsFlow = (application as DuoCashApp).container.settings.settings
        setContent {
            val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = Settings())
            DuoCashTheme(themeMode = settings.themeMode) {
                DuoCashNavHost()
            }
        }
    }
}
