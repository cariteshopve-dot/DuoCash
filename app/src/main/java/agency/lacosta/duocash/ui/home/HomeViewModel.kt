package agency.lacosta.duocash.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import agency.lacosta.duocash.data.MovementRepository
import agency.lacosta.duocash.data.settings.Settings
import agency.lacosta.duocash.data.settings.SettingsRepository
import agency.lacosta.duocash.domain.MonthSummary
import agency.lacosta.duocash.domain.Movement
import agency.lacosta.duocash.domain.Totals
import agency.lacosta.duocash.ui.container
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DayGroup(val date: LocalDate, val movements: List<Movement>)

data class HomeUiState(
    val loading: Boolean = true,
    val month: YearMonth = YearMonth.now(),
    val settings: Settings = Settings(),
    val primaryTotals: Totals? = null,
    val secondaryTotals: Totals? = null,
    val days: List<DayGroup> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    movements: MovementRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    private val month = MutableStateFlow(YearMonth.now())

    val state: StateFlow<HomeUiState> = combine(
        month,
        month.flatMapLatest { movements.observeMonth(it) },
        settingsRepository.settings,
    ) { month, list, settings ->
        val zone = ZoneId.systemDefault()
        HomeUiState(
            loading = false,
            month = month,
            settings = settings,
            primaryTotals = MonthSummary.totals(list, settings.primary, settings.pairRate),
            secondaryTotals = MonthSummary.totals(list, settings.secondary, settings.pairRate),
            days = list.groupBy { it.occurredAt.atZone(zone).toLocalDate() }
                .map { (date, items) -> DayGroup(date, items) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun previousMonth() = month.update { it.minusMonths(1) }

    fun nextMonth() = month.update { it.plusMonths(1) }

    companion object {
        val Factory = viewModelFactory {
            initializer { HomeViewModel(container.movements, container.settings) }
        }
    }
}
