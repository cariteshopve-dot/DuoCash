package agency.lacosta.duocash.domain

/** Totales del mes en una moneda. Los movimientos que no se pueden llevar a esa moneda quedan en [missing]. */
data class Totals(val currency: Currency, val expenseMinor: Long, val incomeMinor: Long, val missing: Int) {
    val balanceMinor: Long get() = incomeMinor - expenseMinor
}

object MonthSummary {

    /**
     * Suma en [target]. Usa el equivalente guardado en cada movimiento y, si no lo hay, la tasa actual [fallback].
     */
    fun totals(movements: List<Movement>, target: Currency, fallback: PairRate?): Totals {
        var expense = 0L
        var income = 0L
        var missing = 0
        for (m in movements) {
            val value = m.amountIn(target)
                ?: fallback?.rateFor(m.currency, target)?.let { Money.convert(m.amountMinor, m.currency, target, it) }
                ?: fallback?.rateFor(m.counterCurrency, target)?.let {
                    Money.convert(m.counterAmountMinor, m.counterCurrency, target, it)
                }
            when {
                value == null -> missing++
                m.type == MovementType.EXPENSE -> expense += value
                else -> income += value
            }
        }
        return Totals(target, expense, income, missing)
    }
}
