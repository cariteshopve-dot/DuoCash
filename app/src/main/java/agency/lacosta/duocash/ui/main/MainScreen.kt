package agency.lacosta.duocash.ui.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import agency.lacosta.duocash.R
import agency.lacosta.duocash.ui.dashboard.DashboardScreen
import agency.lacosta.duocash.ui.home.HomeScreen
import agency.lacosta.duocash.ui.theme.LocalDuoColors

private enum class Tab { HOME, DASHBOARD }

/** Las pestañas de abajo: Inicio y Dashboard. */
@Composable
fun MainScreen(onAdd: () -> Unit, onOpen: (Long) -> Unit, onSettings: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(Tab.HOME) }
    val colors = MaterialTheme.colorScheme

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = LocalDuoColors.current.card) {
                listOf(
                    Triple(Tab.HOME, Icons.Outlined.Home, R.string.tab_home),
                    Triple(Tab.DASHBOARD, Icons.Outlined.BarChart, R.string.tab_dashboard),
                ).forEach { (item, icon, label) ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(stringResource(label)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.onPrimary,
                            selectedTextColor = colors.onSurface,
                            indicatorColor = colors.primary,
                            unselectedIconColor = colors.onSurfaceVariant,
                            unselectedTextColor = colors.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        when (tab) {
            Tab.HOME -> HomeScreen(onAdd = onAdd, onOpen = onOpen, onSettings = onSettings, modifier = Modifier.padding(padding))
            Tab.DASHBOARD -> DashboardScreen(modifier = Modifier.padding(padding))
        }
    }
}
