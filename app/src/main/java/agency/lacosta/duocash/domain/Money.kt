package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

/** Montos en unidades mínimas (centavos) para no perder precisión, aunque sean millones de bolívares. */
object Money {

    fun toDecimal(minor: Long, currency: Currency): BigDecimal =
        BigDecimal.valueOf(minor, currency.decimals)

    fun toMinor(amount: BigDecimal, currency: Currency): Long =
        amount.setScale(currency.decimals, RoundingMode.HALF_UP).unscaledValue().toLong()

    /**
     * Convierte [minor] de [from] a [to]. [rate] es cuántas unidades de [to] vale 1 unidad de [from].
     */
    fun convert(minor: Long, from: Currency, to: Currency, rate: BigDecimal): Long =
        toMinor(toDecimal(minor, from).multiply(rate), to)

    /** Lee lo que escribe la persona: acepta coma o punto como decimal y separadores de miles. */
    fun parse(text: String): BigDecimal? {
        val clean = text.trim().replace(" ", "")
        if (clean.isEmpty()) return null
        val lastComma = clean.lastIndexOf(',')
        val lastDot = clean.lastIndexOf('.')
        val normalized = when {
            lastComma >= 0 && lastDot >= 0 ->
                if (lastComma > lastDot) clean.replace(".", "").replace(',', '.')
                else clean.replace(",", "")
            lastComma >= 0 ->
                if (clean.count { it == ',' } > 1) clean.replace(",", "") else clean.replace(',', '.')
            clean.count { it == '.' } > 1 -> clean.replace(".", "")
            else -> clean
        }
        return normalized.toBigDecimalOrNull()?.takeIf { it.signum() >= 0 }
    }

    fun format(minor: Long, currency: Currency, locale: Locale = Locale.getDefault()): String =
        "${currency.symbol} ${formatNumber(toDecimal(minor, currency), currency.decimals, currency.decimals, locale)}"

    fun formatRate(rate: BigDecimal, locale: Locale = Locale.getDefault()): String =
        formatNumber(rate, 2, 4, locale)

    /** Texto para un campo editable: sin separador de miles, con el decimal del idioma. */
    fun formatForInput(value: BigDecimal, locale: Locale = Locale.getDefault()): String {
        val sep = DecimalFormatSymbols.getInstance(locale).decimalSeparator
        return value.stripTrailingZeros().toPlainString().replace('.', sep)
    }

    private fun formatNumber(value: BigDecimal, minDigits: Int, maxDigits: Int, locale: Locale): String =
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = minDigits
            maximumFractionDigits = maxDigits
            roundingMode = RoundingMode.HALF_UP
        }.format(value)
}
