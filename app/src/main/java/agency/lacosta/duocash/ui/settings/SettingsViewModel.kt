package agency.lacosta.duocash.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import agency.lacosta.duocash.data.settings.Settings
import agency.lacosta.duocash.data.settings.SettingsRepository
import agency.lacosta.duocash.data.settings.ThemeMode
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.ui.container
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<Settings?> =
        repository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setPrimary(currency: Currency) = updatePair { s ->
        if (currency == s.secondary) s.secondary to s.primary else currency to s.secondary
    }

    fun setSecondary(currency: Currency) = updatePair { s ->
        if (currency == s.primary) s.secondary to s.primary else s.primary to currency
    }

    fun swap() = updatePair { s -> s.secondary to s.primary }

    /** Devuelve false si el texto no es una tasa válida. Vacío borra la tasa propia. */
    fun setManualRate(text: String): Boolean {
        val s = settings.value ?: return false
        val rate = if (text.isBlank()) null else Money.parse(text)?.takeIf { it.signum() > 0 } ?: return false
        viewModelScope.launch { repository.setManualRate(s.primary, s.secondary, rate) }
        return true
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    private fun updatePair(next: (Settings) -> Pair<Currency, Currency>) {
        val s = settings.value ?: return
        val (primary, secondary) = next(s)
        viewModelScope.launch { repository.setPair(primary, secondary) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SettingsViewModel(container.settings) }
        }
    }
}
