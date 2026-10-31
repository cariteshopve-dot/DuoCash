package agency.lacosta.duocash.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import agency.lacosta.duocash.ui.main.MainScreen
import agency.lacosta.duocash.ui.movement.MovementScreen
import agency.lacosta.duocash.ui.movement.MovementViewModel
import agency.lacosta.duocash.ui.settings.SettingsScreen

private object Routes {
    const val HOME = "home"
    const val MOVEMENT = "movement?${MovementViewModel.ARG_ID}={${MovementViewModel.ARG_ID}}"
    const val SETTINGS = "settings"

    fun movement(id: Long? = null) = if (id == null) "movement" else "movement?${MovementViewModel.ARG_ID}=$id"
}

@Composable
fun DuoCashNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            MainScreen(
                onAdd = { nav.navigate(Routes.movement()) },
                onOpen = { nav.navigate(Routes.movement(it)) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(
            Routes.MOVEMENT,
            arguments = listOf(navArgument(MovementViewModel.ARG_ID) { type = NavType.LongType; defaultValue = 0L }),
        ) {
            MovementScreen(onDone = { nav.popBackStack(Routes.HOME, inclusive = false) })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
