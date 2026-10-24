package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.time.Instant

enum class MovementType { EXPENSE, INCOME }

/**
 * Un gasto o ingreso. Guarda la tasa del momento en que se anotó ([rate]: 1 [currency] = [rate] [counterCurrency])
 * y el equivalente ya calculado, para que su valor no cambie cuando la tasa suba.
 */
data class Movement(
    val id: Long = 0,
    val type: MovementType,
    val amountMinor: Long,
    val currency: Currency,
    val counterAmountMinor: Long,
    val counterCurrency: Currency,
    val rate: BigDecimal,
    val category: Category,
    val note: String,
    val occurredAt: Instant,
    val createdAt: Instant = Instant.now(),
) {
    /** El monto en [target] a la tasa del día en que se anotó, o null si no se anotó en esa moneda. */
    fun amountIn(target: Currency): Long? = when (target) {
        currency -> amountMinor
        counterCurrency -> counterAmountMinor
        else -> null
    }
}
