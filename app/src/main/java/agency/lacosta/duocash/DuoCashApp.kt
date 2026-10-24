package agency.lacosta.duocash

import android.app.Application
import agency.lacosta.duocash.data.MovementRepository
import agency.lacosta.duocash.data.db.DuoCashDatabase
import agency.lacosta.duocash.data.settings.SettingsRepository

class DuoCashApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Dependencias de la app, creadas una sola vez. */
class AppContainer(app: Application) {
    private val database = DuoCashDatabase.create(app)
    val movements = MovementRepository(database.movementDao())
    val settings = SettingsRepository(app)
}
