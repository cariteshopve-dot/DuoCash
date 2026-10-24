package agency.lacosta.duocash.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.PairRate
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class Settings(
    val primary: Currency = Currency.USD,
    val secondary: Currency = Currency.VES,
    /** La tasa propia del par actual (1 primaria = X secundaria), si la persona la puso. */
    val manualRate: BigDecimal? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) {
    val pairRate: PairRate? get() = manualRate?.let { PairRate(primary, secondary, it) }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {

    private val store = context.dataStore

    val settings: Flow<Settings> = store.data.map { prefs ->
        val primary = prefs[PRIMARY]?.let(Currency::fromCode) ?: Currency.USD
        val secondary = prefs[SECONDARY]?.let(Currency::fromCode) ?: Currency.VES
        Settings(
            primary = primary,
            secondary = secondary,
            manualRate = prefs[rateKey(primary, secondary)]?.toBigDecimalOrNull(),
            themeMode = prefs[THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
        )
    }

    suspend fun setPair(primary: Currency, secondary: Currency) {
        store.edit {
            it[PRIMARY] = primary.code
            it[SECONDARY] = secondary.code
        }
    }

    suspend fun setManualRate(primary: Currency, secondary: Currency, rate: BigDecimal?) {
        store.edit {
            val key = rateKey(primary, secondary)
            if (rate == null) {
                it.remove(key)
            } else {
                it[key] = rate.toPlainString()
            }
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        store.edit { it[THEME] = mode.name }
    }

    private companion object {
        val PRIMARY = stringPreferencesKey("primary_currency")
        val SECONDARY = stringPreferencesKey("secondary_currency")
        val THEME = stringPreferencesKey("theme_mode")

        /** Una tasa propia por par, para no perderla al cambiar de monedas y volver. */
        fun rateKey(base: Currency, quote: Currency) = stringPreferencesKey("manual_rate_${base.code}_${quote.code}")
    }
}
