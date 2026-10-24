package agency.lacosta.duocash.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import agency.lacosta.duocash.R
import agency.lacosta.duocash.data.settings.ThemeMode
import agency.lacosta.duocash.domain.Currency
import agency.lacosta.duocash.domain.Money
import agency.lacosta.duocash.ui.components.CurrencyPickerDialog
import agency.lacosta.duocash.ui.components.StatusBarIcons

private enum class Picking { PRIMARY, SECONDARY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    StatusBarIcons()
    var picking by rememberSaveable { mutableStateOf<Picking?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        val s = settings ?: return@Scaffold
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            SectionTitle(stringResource(R.string.settings_currencies))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    CurrencyRow(stringResource(R.string.settings_primary), s.primary) { picking = Picking.PRIMARY }
                    CurrencyRow(stringResource(R.string.settings_secondary), s.secondary) { picking = Picking.SECONDARY }
                }
                IconButton(onClick = viewModel::swap) {
                    Icon(Icons.Outlined.SwapVert, stringResource(R.string.settings_swap))
                }
            }

            Divider()
            SectionTitle(stringResource(R.string.settings_my_rate))
            RateEditor(
                key = "${s.primary}-${s.secondary}",
                initial = s.manualRate?.let { Money.formatForInput(it) } ?: "",
                base = s.primary,
                quote = s.secondary,
                onSave = viewModel::setManualRate,
            )
            Text(
                stringResource(R.string.settings_rate_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            Divider()
            SectionTitle(stringResource(R.string.settings_theme))
            ThemeSelector(s.themeMode, viewModel::setThemeMode)

            Divider()
            About()
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    picking?.let { which ->
        val current = settings ?: return@let
        CurrencyPickerDialog(
            title = stringResource(if (which == Picking.PRIMARY) R.string.settings_primary else R.string.settings_secondary),
            selected = if (which == Picking.PRIMARY) current.primary else current.secondary,
            onSelect = {
                if (which == Picking.PRIMARY) viewModel.setPrimary(it) else viewModel.setSecondary(it)
                picking = null
            },
            onDismiss = { picking = null },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
private fun Divider() {
    HorizontalDivider(Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun CurrencyRow(label: String, currency: Currency, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(
            "${currency.code} · ${stringResource(currency.nameRes)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RateEditor(key: String, initial: String, base: Currency, quote: Currency, onSave: (String) -> Boolean) {
    var text by rememberSaveable(key) { mutableStateOf(initial) }
    var error by rememberSaveable(key) { mutableStateOf(false) }
    var saved by rememberSaveable(key) { mutableStateOf(false) }
    LaunchedEffect(text) { saved = false }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("1 ${base.code} =", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                error = false
            },
            singleLine = true,
            isError = error,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
        )
        Text(quote.code, style = MaterialTheme.typography.bodyLarge)
    }
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {
            val ok = onSave(text)
            error = !ok
            saved = ok
        }) { Text(stringResource(R.string.action_save)) }
        Spacer(Modifier.width(12.dp))
        when {
            error -> Text(stringResource(R.string.movement_error_rate), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            saved -> Text(stringResource(R.string.settings_rate_saved), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(mode: ThemeMode, onChange: (ThemeMode) -> Unit) {
    val options = listOf(
        ThemeMode.DARK to R.string.theme_dark,
        ThemeMode.LIGHT to R.string.theme_light,
        ThemeMode.SYSTEM to R.string.theme_system,
    )
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (value, label) ->
            SegmentedButton(
                selected = mode == value,
                onClick = { onChange(value) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
            ) { Text(stringResource(label)) }
        }
    }
}

@Composable
private fun About() {
    val context = LocalContext.current
    val version = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull().orEmpty()
    Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.signature), style = MaterialTheme.typography.bodyMedium)
        Text(
            stringResource(R.string.settings_version, version),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
