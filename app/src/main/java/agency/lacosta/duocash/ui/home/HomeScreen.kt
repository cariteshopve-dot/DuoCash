package agency.lacosta.duocash.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import agency.lacosta.duocash.R
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.domain.Movement
import agency.lacosta.duocash.domain.MovementType
import agency.lacosta.duocash.ui.components.StatusBarIcons
import agency.lacosta.duocash.ui.components.WaveBands
import agency.lacosta.duocash.ui.theme.AmountStyle
import agency.lacosta.duocash.ui.theme.LocalDuoColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatusBarIcons(onDarkBackground = true)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.home_add)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item {
                Header(
                    state = state,
                    onPrevious = viewModel::previousMonth,
                    onNext = viewModel::nextMonth,
                    onSettings = onSettings,
                )
            }
            if (!state.loading && state.days.isEmpty()) {
                item { EmptyState() }
            }
            state.days.forEach { day ->
                item(key = "day-${day.date}") { DayHeader(day.date) }
                items(day.movements, key = { it.id }) { movement ->
                    MovementRow(movement, onClick = { onOpen(movement.id) })
                }
            }
            item { Signature() }
        }
    }
}

@Composable
private fun Header(state: HomeUiState, onPrevious: () -> Unit, onNext: () -> Unit, onSettings: () -> Unit) {
    val duo = LocalDuoColors.current
    Column(Modifier.fillMaxWidth().background(duo.header)) {
        Column(Modifier.statusBarsPadding().padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onSettings) {
                    Icon(Icons.Outlined.Settings, stringResource(R.string.settings_title), tint = duo.onHeader)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrevious) {
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        stringResource(R.string.home_previous_month),
                        tint = duo.onHeader,
                    )
                }
                Text(
                    monthLabel(state.month),
                    style = MaterialTheme.typography.titleMedium,
                    color = duo.onHeader,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        stringResource(R.string.home_next_month),
                        tint = duo.onHeader,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.home_balance), style = MaterialTheme.typography.labelLarge, color = duo.onHeaderMuted)
            state.primaryTotals?.let { totals ->
                Text(
                    Money.format(totals.balanceMinor, totals.currency),
                    style = MaterialTheme.typography.displaySmall,
                    color = duo.onHeader,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            state.secondaryTotals?.let { totals ->
                Text(
                    Money.format(totals.balanceMinor, totals.currency),
                    style = MaterialTheme.typography.titleLarge,
                    color = duo.onHeaderMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(16.dp))
            state.primaryTotals?.let { totals ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TotalPill(stringResource(R.string.home_expenses), Money.format(totals.expenseMinor, totals.currency), Modifier.weight(1f))
                    TotalPill(stringResource(R.string.home_income), Money.format(totals.incomeMinor, totals.currency), Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
            RateLine(state)
            Spacer(Modifier.height(8.dp))
        }
        WaveBands(colors = duo.waves)
    }
}

@Composable
private fun TotalPill(label: String, value: String, modifier: Modifier = Modifier) {
    val duo = LocalDuoColors.current
    Column(
        modifier
            .background(duo.onHeader.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = duo.onHeaderMuted)
        Text(value, style = AmountStyle, color = duo.onHeader, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun RateLine(state: HomeUiState) {
    val duo = LocalDuoColors.current
    val s = state.settings
    val text = s.manualRate?.let {
        stringResource(R.string.home_rate, s.primary.code, Money.formatRate(it), s.secondary.code)
    } ?: stringResource(R.string.home_rate_missing)
    Text(text, style = MaterialTheme.typography.bodySmall, color = duo.onHeaderMuted)
    val missing = (state.primaryTotals?.missing ?: 0) + (state.secondaryTotals?.missing ?: 0)
    if (missing > 0) {
        Text(stringResource(R.string.home_missing_rate_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.home_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DayHeader(date: LocalDate) {
    val today = LocalDate.now()
    val label = when (date) {
        today -> stringResource(R.string.day_today)
        today.minusDays(1) -> stringResource(R.string.day_yesterday)
        else -> date.format(DateTimeFormatter.ofPattern("EEEE d MMM", Locale.getDefault()))
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun MovementRow(movement: Movement, onClick: () -> Unit) {
    val duo = LocalDuoColors.current
    val isIncome = movement.type == MovementType.INCOME
    val sign = if (isIncome) "+" else "−"
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(movement.category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(stringResource(movement.category.nameRes), style = MaterialTheme.typography.bodyLarge)
            if (movement.note.isNotBlank()) {
                Text(
                    movement.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$sign ${Money.format(movement.amountMinor, movement.currency)}",
                style = AmountStyle,
                color = if (isIncome) duo.income else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                Money.format(movement.counterAmountMinor, movement.counterCurrency),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Signature() {
    Text(
        stringResource(R.string.signature),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
    )
}

private fun monthLabel(month: YearMonth): String =
    month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault()))
        .replaceFirstChar { it.titlecase(Locale.getDefault()) }
