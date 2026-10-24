package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class MonthSummaryTest {

    private fun movement(
        type: MovementType,
        amount: Long,
        currency: Currency,
        counter: Long,
        counterCurrency: Currency,
    ) = Movement(
        type = type,
        amountMinor = amount,
        currency = currency,
        counterAmountMinor = counter,
        counterCurrency = counterCurrency,
        rate = BigDecimal.ONE,
        category = Category.FOOD,
        note = "",
        occurredAt = Instant.EPOCH,
    )

    @Test
    fun `usa el equivalente guardado y no la tasa de hoy`() {
        val list = listOf(
            movement(MovementType.EXPENSE, 1_000, Currency.USD, 36_500, Currency.VES),
            movement(MovementType.INCOME, 73_000, Currency.VES, 2_000, Currency.USD),
        )
        val today = PairRate(Currency.USD, Currency.VES, BigDecimal("50"))

        val usd = MonthSummary.totals(list, Currency.USD, today)
        assertEquals(1_000L, usd.expenseMinor)
        assertEquals(2_000L, usd.incomeMinor)
        assertEquals(1_000L, usd.balanceMinor)

        val ves = MonthSummary.totals(list, Currency.VES, today)
        assertEquals(36_500L, ves.expenseMinor)
        assertEquals(73_000L, ves.incomeMinor)
    }

    @Test
    fun `convierte con la tasa actual si el movimiento es de otro par`() {
        val list = listOf(movement(MovementType.EXPENSE, 1_000, Currency.USD, 900, Currency.EUR))
        val today = PairRate(Currency.USD, Currency.VES, BigDecimal("40"))

        val ves = MonthSummary.totals(list, Currency.VES, today)
        assertEquals(40_000L, ves.expenseMinor)
        assertEquals(0, ves.missing)
    }

    @Test
    fun `cuenta los que no se pueden convertir`() {
        val list = listOf(movement(MovementType.EXPENSE, 1_000, Currency.EUR, 1_100, Currency.USDT))
        val totals = MonthSummary.totals(list, Currency.VES, PairRate(Currency.USD, Currency.VES, BigDecimal("40")))
        assertEquals(0L, totals.expenseMinor)
        assertEquals(1, totals.missing)
    }

    @Test
    fun `la tasa sirve en las dos direcciones`() {
        val rate = PairRate(Currency.USD, Currency.VES, BigDecimal("40"))
        assertEquals(BigDecimal("40"), rate.rateFor(Currency.USD, Currency.VES))
        assertEquals(0, BigDecimal("0.025").compareTo(rate.rateFor(Currency.VES, Currency.USD)))
        assertEquals(null, rate.rateFor(Currency.EUR, Currency.VES))
    }
}
