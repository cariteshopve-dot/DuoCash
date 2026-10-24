package agency.lacosta.duocash.domain

import androidx.annotation.StringRes
import agency.lacosta.duocash.R

/** Monedas de la v1. [decimals] es la cantidad de decimales con que se guardan los montos. */
enum class Currency(
    val code: String,
    val symbol: String,
    val decimals: Int,
    @StringRes val nameRes: Int,
) {
    USD("USD", "$", 2, R.string.currency_usd),
    EUR("EUR", "€", 2, R.string.currency_eur),
    USDT("USDT", "₮", 2, R.string.currency_usdt),
    VES("VES", "Bs.", 2, R.string.currency_ves),
    ARS("ARS", "$", 2, R.string.currency_ars),
    COP("COP", "$", 2, R.string.currency_cop),
    DOP("DOP", "RD$", 2, R.string.currency_dop),
    CLP("CLP", "$", 0, R.string.currency_clp),
    UYU("UYU", "\$U", 2, R.string.currency_uyu),
    PEN("PEN", "S/", 2, R.string.currency_pen);

    companion object {
        fun fromCode(code: String): Currency = entries.firstOrNull { it.code == code } ?: USD
    }
}
