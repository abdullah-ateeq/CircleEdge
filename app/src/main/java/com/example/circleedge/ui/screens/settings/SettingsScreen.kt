package com.example.circleedge.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.data.model.AppThemeMode
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.ui.viewmodel.CircleEdgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CircleEdgeViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Column {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Preferences and display configurations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: Appearance
        SettingsSection(title = "APPEARANCE") {
            // Theme Mode Selector
            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppThemeMode.values().forEach { mode ->
                    val isSelected = settings.themeMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateSettings(settings.copy(themeMode = mode)) },
                        label = { Text(mode.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dynamic Color Toggle
            SettingsSwitchRow(
                title = "Dynamic Color",
                subtitle = "Apply Material You theme colors on Android 12+",
                checked = settings.dynamicColor,
                onCheckedChange = { viewModel.updateSettings(settings.copy(dynamicColor = it)) }
            )
        }

        // Section: Defaults
        SettingsSection(title = "CALCULATION DEFAULTS") {
            // Default Input Type
            Text(
                text = "Default Input Type",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CircleInputType.values().forEach { type ->
                    val isSelected = settings.defaultInputType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateSettings(settings.copy(defaultInputType = type)) },
                        label = { Text(type.label, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Default Unit
            Text(
                text = "Default Measurement Unit",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MeasurementUnit.values().forEach { unit ->
                    val isSelected = settings.defaultUnit == unit
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateSettings(settings.copy(defaultUnit = unit)) },
                        label = { Text(unit.linearSymbol, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Decimal Places (0, 1, 2, 3, 4)
            Text(
                text = "Decimal Places: ${settings.decimalPlaces}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (0..4).forEach { places ->
                    val isSelected = settings.decimalPlaces == places
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updateSettings(settings.copy(decimalPlaces = places)) },
                        label = { Text("$places") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Display Options
        SettingsSection(title = "DISPLAY VISIBILITY") {
            SettingsSwitchRow(
                title = "Show Circle Diagram",
                subtitle = "Display technical canvas visualization",
                checked = settings.showDiagram,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showDiagram = it)) }
            )

            SettingsSwitchRow(
                title = "Show Formula Breakdown",
                subtitle = "Display collapsible 'How it was calculated' card",
                checked = settings.showFormula,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showFormula = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Individual Result Properties",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            SettingsSwitchRow(
                title = "Show Radius",
                subtitle = "Display radius result on primary card",
                checked = settings.showRadius,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showRadius = it)) }
            )

            SettingsSwitchRow(
                title = "Show Diameter",
                subtitle = "Display diameter result on primary card",
                checked = settings.showDiameter,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showDiameter = it)) }
            )

            SettingsSwitchRow(
                title = "Show Circumference",
                subtitle = "Display circumference result on primary card",
                checked = settings.showCircumference,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showCircumference = it)) }
            )

            SettingsSwitchRow(
                title = "Show Area",
                subtitle = "Display area result on primary card",
                checked = settings.showArea,
                onCheckedChange = { viewModel.updateSettings(settings.copy(showArea = it)) }
            )
        }

        // Section: Interaction
        SettingsSection(title = "INTERACTION & SAFETY") {
            SettingsSwitchRow(
                title = "Haptic Feedback",
                subtitle = "Vibrate lightly on selections and actions",
                checked = settings.hapticFeedback,
                onCheckedChange = { viewModel.updateSettings(settings.copy(hapticFeedback = it)) }
            )

            SettingsSwitchRow(
                title = "Confirm Deletions",
                subtitle = "Show confirmation dialog before deleting history",
                checked = settings.confirmDelete,
                onCheckedChange = { viewModel.updateSettings(settings.copy(confirmDelete = it)) }
            )
        }

        // Section: Maintenance
        SettingsSection(title = "MAINTENANCE") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Settings", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showClearHistoryDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear History", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("This will restore all default preferences. Saved calculation history will not be deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetSettings()
                    showResetDialog = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear All History?") },
            text = { Text("This will delete all saved calculations permanently.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllHistory()
                    showClearHistoryDialog = false
                }) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
