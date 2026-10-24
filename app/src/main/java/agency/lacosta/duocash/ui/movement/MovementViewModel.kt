package agency.lacosta.duocash.ui.movement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import agency.lacosta.duocash.data.MovementRepository
import agency.lacosta.duocash.data.settings.Settings
import agency.lacosta.duocash.data.settings.SettingsRepository
import agency.lacosta.duocash.domain.Category
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.domain.Movement
import agency.lacosta.duocash.domain.MovementType
import agency.lacosta.duocash.ui.container
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FormError { AMOUNT, RATE }

data class MovementUiState(
    val loading: Boolean = true,
    val isEditing: Boolean = false,
    val type: MovementType = MovementType.EXPENSE,
    val amountText: String = "",
    val currency: Currency = Currency.USD,
    val counterCurrency: Currency = Currency.VES,
    /** La tasa como la lee la persona: 1 [rateBase] = [rateText] [rateQuote]. */
    val rateText: String = "",
    val category: Category = Category.FOOD,
    val note: String = "",
    val date: LocalDate = LocalDate.now(),
    val error: FormError? = null,
    val done: Boolean = false,
) {
    /** El bolívar y similares se leen mejor como "1 USD = 36,5 VES" aunque el gasto sea en bolívares. */
    val rateBase: Currency get() = if (currency.isWeakSide(counterCurrency)) counterCurrency else currency
    val rateQuote: Currency get() = if (rateBase == currency) counterCurrency else currency

    /** Cuántas unidades de [counterCurrency] vale 1 de [currency]. */
    val storedRate: BigDecimal?
        get() {
            val x = Money.parse(rateText)?.takeIf { it.signum() > 0 } ?: return null
            return if (rateBase == currency) x else BigDecimal.ONE.divide(x, MathContext.DECIMAL64)
        }

    val amountMinor: Long? get() = Money.parse(amountText)?.let { Money.toMinor(it, currency) }

    val counterPreviewMinor: Long?
        get() {
            val amount = amountMinor ?: return null
            val rate = storedRate ?: return null
            return Money.convert(amount, currency, counterCurrency, rate)
        }
}

private val strongCurrencies = setOf(Currency.USD, Currency.EUR, Currency.USDT)

/** True si esta moneda vale menos que [other] y conviene expresar la tasa al revés. */
private fun Currency.isWeakSide(other: Currency): Boolean = this !in strongCurrencies && other in strongCurrencies

class MovementViewModel(
    savedState: SavedStateHandle,
    private val movements: MovementRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val id: Long? = savedState.get<Long>(ARG_ID)?.takeIf { it > 0 }
    private var original: Movement? = null
    private var settings = Settings()

    private val _state = MutableStateFlow(MovementUiState())
    val state: StateFlow<MovementUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settings = settingsRepository.settings.first()
            val existing = id?.let { movements.get(it) }
            original = existing
            _state.value = if (existing != null) {
                val base = MovementUiState(
                    currency = existing.currency,
                    counterCurrency = existing.counterCurrency,
                )
                val shown = if (base.rateBase == existing.currency) existing.rate
                else BigDecimal.ONE.divide(existing.rate, MathContext.DECIMAL64)
                base.copy(
                    loading = false,
                    isEditing = true,
                    type = existing.type,
                    amountText = Money.formatForInput(Money.toDecimal(existing.amountMinor, existing.currency)),
                    rateText = Money.formatForInput(shown.round(MathContext(8))),
                    category = existing.category,
                    note = existing.note,
                    date = existing.occurredAt.atZone(ZoneId.systemDefault()).toLocalDate(),
                )
            } else {
                withCurrency(MovementUiState(loading = false), settings.primary)
            }
        }
    }

    fun setType(type: MovementType) = _state.update {
        if (it.type == type) it else it.copy(type = type, category = Category.forType(type).first())
    }

    fun setAmount(text: String) = _state.update { it.copy(amountText = text, error = null) }

    fun setCurrency(currency: Currency) = _state.update { withCurrency(it, currency) }

    fun setRate(text: String) = _state.update { it.copy(rateText = text, error = null) }

    fun setCategory(category: Category) = _state.update { it.copy(category = category) }

    fun setNote(text: String) = _state.update { it.copy(note = text) }

    fun setDate(date: LocalDate) = _state.update { it.copy(date = date) }

    fun save() {
        val s = _state.value
        val amount = s.amountMinor?.takeIf { it > 0 } ?: return _state.update { it.copy(error = FormError.AMOUNT) }
        val rate = s.storedRate ?: return _state.update { it.copy(error = FormError.RATE) }
        val counter = s.counterPreviewMinor ?: return
        viewModelScope.launch {
            val movement = Movement(
                id = original?.id ?: 0,
                type = s.type,
                amountMinor = amount,
                currency = s.currency,
                counterAmountMinor = counter,
                counterCurrency = s.counterCurrency,
                rate = rate,
                category = s.category,
                note = s.note.trim(),
                occurredAt = occurredAt(s.date),
                createdAt = original?.createdAt ?: Instant.now(),
            )
            movements.save(movement)
            rememberRateIfMissing(s)
            _state.update { it.copy(done = true) }
        }
    }

    fun delete() {
        val existing = original ?: return
        viewModelScope.launch {
            movements.delete(existing.id)
            _state.update { it.copy(done = true) }
        }
    }

    /** La otra moneda es la secundaria si se anota en la principal; si no, la principal. */
    private fun withCurrency(state: MovementUiState, currency: Currency): MovementUiState {
        val counter = if (currency == settings.primary) settings.secondary else settings.primary
        val next = state.copy(currency = currency, counterCurrency = counter, error = null)
        val known = settings.pairRate?.rateFor(next.rateBase, next.rateQuote)
        return next.copy(rateText = known?.let { Money.formatForInput(it.round(MathContext(8))) } ?: "")
    }

    private fun occurredAt(date: LocalDate): Instant {
        val zone = ZoneId.systemDefault()
        original?.let { if (it.occurredAt.atZone(zone).toLocalDate() == date) return it.occurredAt }
        val time = if (date == LocalDate.now()) LocalTime.now() else LocalTime.NOON
        return date.atTime(time).atZone(zone).toInstant()
    }

    /** La primera vez que se usa el par, la tasa escrita queda como tasa propia. */
    private suspend fun rememberRateIfMissing(s: MovementUiState) {
        if (settings.manualRate != null) return
        val pair = setOf(settings.primary, settings.secondary)
        if (setOf(s.currency, s.counterCurrency) != pair) return
        val rate = s.storedRate ?: return
        val primaryToSecondary = if (s.currency == settings.primary) rate
        else BigDecimal.ONE.divide(rate, MathContext.DECIMAL64)
        settingsRepository.setManualRate(settings.primary, settings.secondary, primaryToSecondary.round(MathContext(10)))
    }

    companion object {
        const val ARG_ID = "id"

        val Factory = viewModelFactory {
            initializer { MovementViewModel(createSavedStateHandle(), container.movements, container.settings) }
        }
    }
}
