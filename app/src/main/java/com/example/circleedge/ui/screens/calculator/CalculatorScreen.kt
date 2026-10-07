package com.example.circleedge.ui.screens.calculator

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.domain.format.CircleFormatter
import com.example.circleedge.ui.components.CircleDiagram
import com.example.circleedge.ui.components.FormulaBreakdown
import com.example.circleedge.ui.components.MeasurementSelector
import com.example.circleedge.ui.components.PresetsRow
import com.example.circleedge.ui.components.ResultsCard
import com.example.circleedge.ui.components.UnitSelector
import com.example.circleedge.ui.viewmodel.CircleEdgeViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun CalculatorScreen(
    viewModel: CircleEdgeViewModel,
    modifier: Modifier = Modifier
) {
    val inputType by viewModel.inputType.collectAsState()
    val inputValueText by viewModel.inputValueText.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val projectName by viewModel.projectName.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun triggerHaptic() {
        if (settings.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isTabletOrLandscape = maxWidth >= 600.dp

            if (isTabletOrLandscape) {
                // Two-column layout for tablets and landscape
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Left Column: Input Controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CalculatorHeader()

                        // Input Type Segmented Selector
                        Text(
                            text = "SELECT KNOWN MEASUREMENT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        MeasurementSelector(
                            selectedType = inputType,
                            onSelect = {
                                triggerHaptic()
                                viewModel.setInputType(it)
                            }
                        )

                        // Input Text Field
                        OutlinedTextField(
                            value = inputValueText,
                            onValueChange = { viewModel.setInputValueText(it) },
                            label = { Text("Enter ${inputType.label} (${selectedUnit.symbolFor(inputType)})") },
                            placeholder = { Text("e.g. 10") },
                            isError = validationError != null,
                            supportingText = {
                                if (validationError != null) {
                                    Text(
                                        text = validationError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "${inputType.label} input field"
                                }
                        )

                        // Unit Selector
                        Text(
                            text = "UNIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        UnitSelector(
                            selectedUnit = selectedUnit,
                            inputType = inputType,
                            onSelectUnit = {
                                triggerHaptic()
                                viewModel.setUnit(it)
                            }
                        )

                        // Quick Presets
                        PresetsRow(
                            onPresetSelected = {
                                triggerHaptic()
                                viewModel.applyPreset(it)
                            }
                        )

                        // Project Name Field
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { viewModel.setProjectName(it) },
                            label = { Text("Project / Part Name (optional)") },
                            placeholder = { Text("e.g. Dining Table, Pipe, Panel") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Action Buttons: Copy, Share, Save, Reset
                        ActionButtonsRow(
                            result = calculationResult,
                            projectName = projectName,
                            unit = selectedUnit,
                            decimalPlaces = settings.decimalPlaces,
                            onCopy = { text ->
                                clipboardManager.setText(AnnotatedString(text))
                                triggerHaptic()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Result copied")
                                }
                            },
                            onShare = { text ->
                                triggerHaptic()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share CircleEdge Result")
                                context.startActivity(shareIntent)
                            },
                            onSave = {
                                triggerHaptic()
                                viewModel.saveCurrentCalculation {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Calculation saved to History")
                                    }
                                }
                            },
                            onReset = {
                                triggerHaptic()
                                viewModel.resetCalculator()
                            }
                        )

                        DisclaimerNote()
                    }

                    // Right Column: Diagram and Results
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (settings.showDiagram) {
                            CircleDiagram(
                                result = calculationResult,
                                inputType = inputType,
                                unit = selectedUnit,
                                decimalPlaces = settings.decimalPlaces
                            )
                        }

                        if (calculationResult != null) {
                            ResultsCard(
                                result = calculationResult!!,
                                selectedInputType = inputType,
                                unit = selectedUnit,
                                settings = settings
                            )

                            if (settings.showFormula) {
                                val parsed = try { BigDecimal(inputValueText.trim()) } catch (_: Exception) { null }
                                if (parsed != null && parsed > BigDecimal.ZERO) {
                                    FormulaBreakdown(
                                        inputType = inputType,
                                        inputValue = parsed,
                                        unit = selectedUnit,
                                        decimalPlaces = settings.decimalPlaces,
                                        result = calculationResult!!
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Portrait single-column layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CalculatorHeader()

                    // Known Measurement Selector
                    Text(
                        text = "SELECT KNOWN MEASUREMENT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    MeasurementSelector(
                        selectedType = inputType,
                        onSelect = {
                            triggerHaptic()
                            viewModel.setInputType(it)
                        }
                    )

                    // Input Text Field
                    OutlinedTextField(
                        value = inputValueText,
                        onValueChange = { viewModel.setInputValueText(it) },
                        label = { Text("Enter ${inputType.label} (${selectedUnit.symbolFor(inputType)})") },
                        placeholder = { Text("e.g. 10") },
                        isError = validationError != null,
                        supportingText = {
                            if (validationError != null) {
                                Text(
                                    text = validationError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "${inputType.label} input field"
                            }
                    )

                    // Unit Selector
                    Text(
                        text = "UNIT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    UnitSelector(
                        selectedUnit = selectedUnit,
                        inputType = inputType,
                        onSelectUnit = {
                            triggerHaptic()
                            viewModel.setUnit(it)
                        }
                    )

                    // Quick Presets
                    PresetsRow(
                        onPresetSelected = {
                            triggerHaptic()
                            viewModel.applyPreset(it)
                        }
                    )

                    // Diagram
                    if (settings.showDiagram) {
                        CircleDiagram(
                            result = calculationResult,
                            inputType = inputType,
                            unit = selectedUnit,
                            decimalPlaces = settings.decimalPlaces
                        )
                    }

                    // Circle Results Card
                    if (calculationResult != null) {
                        ResultsCard(
                            result = calculationResult!!,
                            selectedInputType = inputType,
                            unit = selectedUnit,
                            settings = settings
                        )

                        // Formula Breakdown
                        if (settings.showFormula) {
                            val parsed = try { BigDecimal(inputValueText.trim()) } catch (_: Exception) { null }
                            if (parsed != null && parsed > BigDecimal.ZERO) {
                                FormulaBreakdown(
                                    inputType = inputType,
                                    inputValue = parsed,
                                    unit = selectedUnit,
                                    decimalPlaces = settings.decimalPlaces,
                                    result = calculationResult!!
                                )
                            }
                        }
                    }

                    // Project Name
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = { viewModel.setProjectName(it) },
                        label = { Text("Project / Part Name (optional)") },
                        placeholder = { Text("e.g. Dining Table, Pipe, Round Window") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Action buttons: Copy, Share, Save, Reset
                    ActionButtonsRow(
                        result = calculationResult,
                        projectName = projectName,
                        unit = selectedUnit,
                        decimalPlaces = settings.decimalPlaces,
                        onCopy = { text ->
                            clipboardManager.setText(AnnotatedString(text))
                            triggerHaptic()
                            scope.launch {
                                snackbarHostState.showSnackbar("Result copied")
                            }
                        },
                        onShare = { text ->
                            triggerHaptic()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share CircleEdge Result")
                            context.startActivity(shareIntent)
                        },
                        onSave = {
                            triggerHaptic()
                            viewModel.saveCurrentCalculation {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Calculation saved to History")
                                }
                            }
                        },
                        onReset = {
                            triggerHaptic()
                            viewModel.resetCalculator()
                        }
                    )

                    DisclaimerNote()

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CalculatorHeader() {
    Column {
        Text(
            text = "CIRCLEEDGE",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Convert radius, diameter, circumference, and area.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionButtonsRow(
    result: com.example.circleedge.data.model.CircleResult?,
    projectName: String,
    unit: com.example.circleedge.data.model.MeasurementUnit,
    decimalPlaces: Int,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit
) {
    val isCalculationValid = result != null

    val shareText = remember(result, projectName, unit, decimalPlaces) {
        if (result != null) {
            CircleFormatter.formatShareText(projectName, result, unit, decimalPlaces)
        } else ""
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Save Button
            Button(
                onClick = onSave,
                enabled = isCalculationValid,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Save calculation" },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save")
            }

            // Copy Button
            OutlinedButton(
                onClick = { onCopy(shareText) },
                enabled = isCalculationValid,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Copy result" },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Share Button
            OutlinedButton(
                onClick = { onShare(shareText) },
                enabled = isCalculationValid,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Share result" },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share")
            }

            // Reset Button
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Reset calculator" },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset")
            }
        }
    }
}

@Composable
private fun DisclaimerNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Text(
            text = "Verify critical workshop measurements and material tolerances before fabrication.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(10.dp)
        )
    }
}
