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
            val value = valueIn(m, target, fallback)
            when {
                value == null -> missing++
                m.type == MovementType.EXPENSE -> expense += value
                else -> income += value
            }
        }
        return Totals(target, expense, income, missing)
    }

    /** El monto de [m] en [target]: el equivalente guardado o, si no lo hay, convertido con la tasa actual. */
    fun valueIn(m: Movement, target: Currency, fallback: PairRate?): Long? =
        m.amountIn(target)
            ?: fallback?.rateFor(m.currency, target)?.let { Money.convert(m.amountMinor, m.currency, target, it) }
            ?: fallback?.rateFor(m.counterCurrency, target)?.let {
                Money.convert(m.counterAmountMinor, m.counterCurrency, target, it)
            }
}

/** Lo gastado en una categoría, en la moneda pedida. */
data class CategoryTotal(val category: Category, val minor: Long)

/** Gastos del mes por categoría en una moneda, de mayor a menor. Los que no se pueden convertir quedan en [missing]. */
data class CategoryBreakdown(val currency: Currency, val categories: List<CategoryTotal>, val missing: Int) {
    val totalMinor: Long get() = categories.sumOf { it.minor }
}

object CategorySummary {

    fun expenses(movements: List<Movement>, target: Currency, fallback: PairRate?): CategoryBreakdown {
        val sums = linkedMapOf<Category, Long>()
        var missing = 0
        for (m in movements) {
            if (m.type != MovementType.EXPENSE) continue
            val value = MonthSummary.valueIn(m, target, fallback)
            if (value == null) {
                missing++
            } else {
                sums[m.category] = (sums[m.category] ?: 0L) + value
            }
        }
        val categories = sums.map { (category, minor) -> CategoryTotal(category, minor) }
            .sortedByDescending { it.minor }
        return CategoryBreakdown(target, categories, missing)
    }
}
