package agency.lacosta.duocash.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import agency.lacosta.duocash.R
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.domain.Movement
import agency.lacosta.duocash.domain.MovementType
import agency.lacosta.duocash.domain.Totals
import agency.lacosta.duocash.ui.components.StatusBarIcons
import agency.lacosta.duocash.ui.theme.AmountStyle
import agency.lacosta.duocash.ui.theme.BalanceStyle
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
    // Qué moneda se ve en grande: la principal o la secundaria.
    var showSecondary by rememberSaveable { mutableStateOf(false) }
    StatusBarIcons()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                // El Scaffold no aplica márgenes del sistema aquí, así que el botón se separa a mano de la barra de Android.
                modifier = Modifier.navigationBarsPadding().size(64.dp),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.home_add), modifier = Modifier.size(30.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 112.dp + navBottom),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Header(
                    state = state,
                    showSecondary = showSecondary,
                    onToggle = { showSecondary = it },
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
                    MovementCard(movement, onClick = { onOpen(movement.id) })
                }
            }
        }
    }
}

@Composable
private fun Header(
    state: HomeUiState,
    showSecondary: Boolean,
    onToggle: (Boolean) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSettings: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val big = if (showSecondary) state.secondaryTotals else state.primaryTotals
    val small = if (showSecondary) state.primaryTotals else state.secondaryTotals

    Column(Modifier.fillMaxWidth().statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Wordmark(Modifier.weight(1f))
            OutlinedIconButton(
                onClick = onSettings,
                border = BorderStroke(1.dp, colors.outline),
                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = LocalDuoColors.current.card),
                modifier = Modifier.size(44.dp),
            ) {
                Icon(Icons.Outlined.Tune, stringResource(R.string.settings_title))
            }
        }

        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, stringResource(R.string.home_previous_month), tint = colors.onSurfaceVariant)
            }
            Text(
                stringResource(R.string.home_balance_of, monthLabel(state.month)),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, stringResource(R.string.home_next_month), tint = colors.onSurfaceVariant)
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            big?.let {
                Text(Money.format(it.balanceMinor, it.currency), style = BalanceStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            small?.let {
                Text(
                    "≈ ${Money.format(it.balanceMinor, it.currency)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(12.dp))
            CurrencyToggle(
                primary = state.settings.primary.code,
                secondary = state.settings.secondary.code,
                showSecondary = showSecondary,
                onToggle = onToggle,
            )
        }

        Spacer(Modifier.height(20.dp))
        big?.let { totals ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(stringResource(R.string.home_expenses), Money.format(totals.expenseMinor, totals.currency), Modifier.weight(1f))
                StatCard(
                    stringResource(R.string.home_income),
                    Money.format(totals.incomeMinor, totals.currency),
                    Modifier.weight(1f),
                    valueColor = LocalDuoColors.current.income,
                )
                StatCard(
                    stringResource(R.string.home_rate_label),
                    state.settings.manualRate?.let { Money.formatRate(it) } ?: "—",
                    Modifier.weight(1f),
                )
            }
        }
        RateNote(state, big, small)
    }
}

@Composable
private fun Wordmark(modifier: Modifier = Modifier) {
    val accent = MaterialTheme.colorScheme.primary
    Text(
        buildAnnotatedString {
            append("Duo")
            withStyle(SpanStyle(color = accent)) { append("Cash") }
        },
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier,
    )
}

@Composable
private fun CurrencyToggle(primary: String, secondary: String, showSecondary: Boolean, onToggle: (Boolean) -> Unit) {
    val duo = LocalDuoColors.current
    Row(
        Modifier
            .background(duo.card, RoundedCornerShape(50))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TogglePill(primary, selected = !showSecondary) { onToggle(false) }
        TogglePill(secondary, selected = showSecondary) { onToggle(true) }
    }
}

@Composable
private fun TogglePill(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) colors.primary else Color.Transparent,
            contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 18.dp),
        modifier = Modifier.height(40.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val duo = LocalDuoColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = duo.card,
        border = BorderStroke(1.dp, duo.cardLine),
        modifier = modifier,
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = AmountStyle, color = valueColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun RateNote(state: HomeUiState, big: Totals?, small: Totals?) {
    val s = state.settings
    val text = when {
        s.manualRate == null -> stringResource(R.string.home_rate_missing)
        (big?.missing ?: 0) + (small?.missing ?: 0) > 0 -> stringResource(R.string.home_missing_rate_note)
        else -> stringResource(R.string.home_rate, s.primary.code, Money.formatRate(s.manualRate), s.secondary.code)
    }
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    )
}

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.home_empty_title), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
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
    Text(label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 2.dp))
}

@Composable
private fun MovementCard(movement: Movement, onClick: () -> Unit) {
    val duo = LocalDuoColors.current
    val isIncome = movement.type == MovementType.INCOME
    val sign = if (isIncome) "+" else "−"
    Surface(onClick = onClick, shape = RoundedCornerShape(18.dp), color = duo.card, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(if (isIncome) duo.incomeBg else duo.iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(movement.category.icon, contentDescription = null, tint = if (isIncome) duo.income else duo.iconTint)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(movement.category.nameRes), style = MaterialTheme.typography.titleSmall)
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
}

private fun monthLabel(month: YearMonth): String =
    month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault()))
