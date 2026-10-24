package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {

    @Test
    fun `parse acepta coma o punto como decimal`() {
        assertEquals(BigDecimal("12.50"), Money.parse("12,50"))
        assertEquals(BigDecimal("12.50"), Money.parse("12.50"))
    }

    @Test
    fun `parse entiende separadores de miles`() {
        assertEquals(BigDecimal("1234567.89"), Money.parse("1.234.567,89"))
        assertEquals(BigDecimal("1234567.89"), Money.parse("1,234,567.89"))
        assertEquals(BigDecimal("1234567"), Money.parse("1.234.567"))
    }

    @Test
    fun `parse rechaza texto vacio o invalido`() {
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("-5"))
    }

    @Test
    fun `convierte de dolares a bolivares con la tasa`() {
        // 12,50 USD a 36,5 Bs = 456,25 Bs
        assertEquals(45_625L, Money.convert(1_250L, Currency.USD, Currency.VES, BigDecimal("36.5")))
    }

    @Test
    fun `redondea al convertir a una moneda sin decimales`() {
        // 10,00 USD a 943,456 CLP = 9.434,56 -> 9.435 CLP
        assertEquals(9_435L, Money.convert(1_000L, Currency.USD, Currency.CLP, BigDecimal("943.456")))
    }

    @Test
    fun `montos grandes no pierden precision`() {
        val minor = Money.toMinor(BigDecimal("98765432109.99"), Currency.VES)
        assertEquals(BigDecimal("98765432109.99"), Money.toDecimal(minor, Currency.VES))
    }

    @Test
    fun `formato usa el simbolo y los decimales de la moneda`() {
        assertEquals("Bs. 1.234,50", Money.format(123_450L, Currency.VES, Locale.forLanguageTag("es-VE")))
        assertEquals("$ 1,234.50", Money.format(123_450L, Currency.USD, Locale.US))
        assertEquals("$ 9,435", Money.format(9_435L, Currency.CLP, Locale.US))
    }

    @Test
    fun `texto para editar usa el decimal del idioma`() {
        assertEquals("36,5", Money.formatForInput(BigDecimal("36.50"), Locale.forLanguageTag("es-VE")))
        assertEquals("36.5", Money.formatForInput(BigDecimal("36.50"), Locale.US))
    }
}
