package agency.lacosta.duocash.domain

import java.math.BigDecimal
import java.math.MathContext

/** Tasa entre dos monedas: 1 [base] = [value] [quote]. */
data class PairRate(val base: Currency, val quote: Currency, val value: BigDecimal) {

    /** Cuántas unidades de [to] vale 1 unidad de [from], si esta tasa sirve para ese par. */
    fun rateFor(from: Currency, to: Currency): BigDecimal? = when {
        from == base && to == quote -> value
        from == quote && to == base && value.signum() > 0 -> BigDecimal.ONE.divide(value, MathContext.DECIMAL64)
        else -> null
    }
}
