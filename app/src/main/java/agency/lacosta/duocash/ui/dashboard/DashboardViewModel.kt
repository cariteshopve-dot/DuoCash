package agency.lacosta.duocash.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import agency.lacosta.duocash.data.MovementRepository
import agency.lacosta.duocash.data.settings.Settings
import agency.lacosta.duocash.data.settings.SettingsRepository
import agency.lacosta.duocash.domain.CategoryBreakdown
import agency.lacosta.duocash.domain.CategorySummary
import agency.lacosta.duocash.ui.container
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DashboardUiState(
    val loading: Boolean = true,
    val month: YearMonth = YearMonth.now(),
    val settings: Settings = Settings(),
    val primary: CategoryBreakdown? = null,
    val secondary: CategoryBreakdown? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    movements: MovementRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val month = MutableStateFlow(YearMonth.now())

    val state: StateFlow<DashboardUiState> = combine(
        month,
        month.flatMapLatest { movements.observeMonth(it) },
        settingsRepository.settings,
    ) { month, list, settings ->
        DashboardUiState(
            loading = false,
            month = month,
            settings = settings,
            primary = CategorySummary.expenses(list, settings.primary, settings.pairRate),
            secondary = CategorySummary.expenses(list, settings.secondary, settings.pairRate),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun previousMonth() = month.update { it.minusMonths(1) }

    fun nextMonth() = month.update { it.plusMonths(1) }

    companion object {
        val Factory = viewModelFactory {
            initializer { DashboardViewModel(container.movements, container.settings) }
        }
    }
}
