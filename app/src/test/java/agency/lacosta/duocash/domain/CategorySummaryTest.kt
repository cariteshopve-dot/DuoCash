package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class CategorySummaryTest {

    private fun movement(type: MovementType, category: Category, usd: Long, ves: Long) = Movement(
        type = type,
        amountMinor = usd,
        currency = Currency.USD,
        counterAmountMinor = ves,
        counterCurrency = Currency.VES,
        rate = BigDecimal("36.5"),
        category = category,
        note = "",
        occurredAt = Instant.EPOCH,
    )

    @Test
    fun `suma gastos por categoria de mayor a menor`() {
        val list = listOf(
            movement(MovementType.EXPENSE, Category.FOOD, 1_000, 36_500),
            movement(MovementType.EXPENSE, Category.GROCERIES, 5_000, 182_500),
            movement(MovementType.EXPENSE, Category.FOOD, 2_000, 73_000),
            movement(MovementType.INCOME, Category.SALARY, 60_000, 2_190_000),
        )

        val usd = CategorySummary.expenses(list, Currency.USD, null)
        assertEquals(listOf(Category.GROCERIES, Category.FOOD), usd.categories.map { it.category })
        assertEquals(listOf(5_000L, 3_000L), usd.categories.map { it.minor })
        assertEquals(8_000L, usd.totalMinor)

        val ves = CategorySummary.expenses(list, Currency.VES, null)
        assertEquals(292_000L, ves.totalMinor)
    }

    @Test
    fun `cuenta los gastos sin tasa para esa moneda`() {
        val list = listOf(movement(MovementType.EXPENSE, Category.FOOD, 1_000, 36_500))
        val eur = CategorySummary.expenses(list, Currency.EUR, null)
        assertEquals(0L, eur.totalMinor)
        assertEquals(1, eur.missing)
    }
}
