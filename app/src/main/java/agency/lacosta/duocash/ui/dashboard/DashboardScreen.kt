package agency.lacosta.duocash.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import agency.lacosta.duocash.R
import agency.lacosta.duocash.domain.CategoryTotal
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.ui.components.StatusBarIcons
import agency.lacosta.duocash.ui.theme.AmountStyle
import agency.lacosta.duocash.ui.theme.BalanceStyle
import agency.lacosta.duocash.ui.theme.LocalDuoColors
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSecondary by rememberSaveable { mutableStateOf(false) }
    val breakdown = if (showSecondary) state.secondary else state.primary
    StatusBarIcons()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Column(Modifier.fillMaxWidth().statusBarsPadding()) {
                Text(
                    stringResource(R.string.dashboard_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp),
                )
                MonthRow(state.month, viewModel::previousMonth, viewModel::nextMonth)
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.dashboard_spent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    breakdown?.let {
                        Text(Money.format(it.totalMinor, it.currency), style = BalanceStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(12.dp))
                    CurrencyToggle(state.settings.primary, state.settings.secondary, showSecondary) { showSecondary = it }
                }
                if ((breakdown?.missing ?: 0) > 0) {
                    Text(
                        stringResource(R.string.home_missing_rate_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    )
                }
                Text(
                    stringResource(R.string.dashboard_by_category),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
                )
            }
        }
        val categories = breakdown?.categories.orEmpty()
        if (!state.loading && categories.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.dashboard_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                )
            }
        }
        val total = breakdown?.totalMinor ?: 0L
        items(categories, key = { it.category.key }) { item ->
            CategoryRow(item, total, breakdown!!.currency)
        }
    }
}

@Composable
private fun MonthRow(month: YearMonth, onPrevious: () -> Unit, onNext: () -> Unit) {
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, stringResource(R.string.home_previous_month), tint = muted)
        }
        Text(
            month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault()))
                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, stringResource(R.string.home_next_month), tint = muted)
        }
    }
}

@Composable
private fun CurrencyToggle(primary: Currency, secondary: Currency, showSecondary: Boolean, onToggle: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.background(LocalDuoColors.current.card, RoundedCornerShape(50)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf(primary to false, secondary to true).forEach { (currency, isSecondary) ->
            val selected = showSecondary == isSecondary
            TextButton(
                onClick = { onToggle(isSecondary) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (selected) colors.primary else Color.Transparent,
                    contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                ),
                contentPadding = PaddingValues(horizontal = 18.dp),
                modifier = Modifier.height(40.dp),
            ) { Text(currency.code, style = MaterialTheme.typography.labelLarge) }
        }
    }
}

@Composable
private fun CategoryRow(item: CategoryTotal, totalMinor: Long, currency: Currency) {
    val duo = LocalDuoColors.current
    val share = if (totalMinor > 0) item.minor.toFloat() / totalMinor else 0f
    Surface(shape = RoundedCornerShape(18.dp), color = duo.card, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).background(duo.iconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(item.category.icon, contentDescription = null, tint = duo.iconTint)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(item.category.nameRes), style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(R.string.dashboard_share, Math.round(share * 100)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(Money.format(item.minor, currency), style = AmountStyle)
            }
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(6.dp).background(duo.cardLine, RoundedCornerShape(50))) {
                Box(
                    Modifier
                        .fillMaxWidth(share.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
                )
            }
        }
    }
}
