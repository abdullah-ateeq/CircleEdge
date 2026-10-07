package com.example.circleedge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.circleedge.data.local.CircleEdgePreferences
import com.example.circleedge.data.model.AppSettings
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.CircleResult
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.data.model.SavedCalculation
import com.example.circleedge.domain.format.CircleFormatter
import com.example.circleedge.domain.math.CircleEdgeCalculator
import com.example.circleedge.domain.math.UnitConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CircleEdgeViewModel(
    private val preferences: CircleEdgePreferences
) : ViewModel() {

    val settings: StateFlow<AppSettings> = preferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    private val _rawHistory = preferences.savedCalculationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered history: favorites first, then newest, matching query
    val historyList: StateFlow<List<SavedCalculation>> = combine(
        _rawHistory,
        _searchQuery
    ) { list, query ->
        val filtered = if (query.isBlank()) {
            list
        } else {
            val q = query.trim().lowercase()
            list.filter { item ->
                item.projectName.lowercase().contains(q) ||
                        item.inputType.label.lowercase().contains(q) ||
                        item.unit.linearSymbol.lowercase().contains(q) ||
                        item.unit.label.lowercase().contains(q)
            }
        }

        // Sort: Favorites first, then timestamp descending
        filtered.sortedWith(
            compareByDescending<SavedCalculation> { it.isFavorite }
                .thenByDescending { it.timestampMillis }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _inputType = MutableStateFlow(CircleInputType.RADIUS)
    val inputType: StateFlow<CircleInputType> = _inputType.asStateFlow()

    private val _inputValueText = MutableStateFlow("10")
    val inputValueText: StateFlow<String> = _inputValueText.asStateFlow()

    private val _selectedUnit = MutableStateFlow(MeasurementUnit.CENTIMETER)
    val selectedUnit: StateFlow<MeasurementUnit> = _selectedUnit.asStateFlow()

    private val _projectName = MutableStateFlow("")
    val projectName: StateFlow<String> = _projectName.asStateFlow()

    private val _calculationResult = MutableStateFlow<CircleResult?>(null)
    val calculationResult: StateFlow<CircleResult?> = _calculationResult.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    init {
        // Initial calculation with default 10 cm Radius
        performCalculation("10", CircleInputType.RADIUS, MeasurementUnit.CENTIMETER)
    }

    fun setInputType(newType: CircleInputType) {
        if (_inputType.value == newType) return

        val currentResult = _calculationResult.value
        val currentUnit = _selectedUnit.value
        val decPlaces = settings.value.decimalPlaces

        _inputType.value = newType

        // Seamless transition: populate input with the corresponding calculated value if valid
        if (currentResult != null) {
            val newValue = when (newType) {
                CircleInputType.RADIUS -> UnitConverter.fromMillimeters(currentResult.radiusMm, currentUnit)
                CircleInputType.DIAMETER -> UnitConverter.fromMillimeters(currentResult.diameterMm, currentUnit)
                CircleInputType.CIRCUMFERENCE -> UnitConverter.fromMillimeters(currentResult.circumferenceMm, currentUnit)
                CircleInputType.AREA -> UnitConverter.fromSquareMillimeters(currentResult.areaMm2, currentUnit)
            }
            val formatted = CircleFormatter.formatNumber(newValue, decPlaces)
            _inputValueText.value = formatted
            performCalculation(formatted, newType, currentUnit)
        } else {
            performCalculation(_inputValueText.value, newType, currentUnit)
        }
    }

    fun setInputValueText(text: String) {
        _inputValueText.value = text
        performCalculation(text, _inputType.value, _selectedUnit.value)
    }

    fun setUnit(newUnit: MeasurementUnit) {
        val oldUnit = _selectedUnit.value
        if (oldUnit == newUnit) return

        val text = _inputValueText.value.trim()
        val decPlaces = settings.value.decimalPlaces

        // Preserve physical measurement when switching units
        if (text.isNotEmpty()) {
            val parsed = try { BigDecimal(text) } catch (_: Exception) { null }
            if (parsed != null && parsed > BigDecimal.ZERO) {
                val converted = if (_inputType.value == CircleInputType.AREA) {
                    UnitConverter.convertArea(parsed, oldUnit, newUnit)
                } else {
                    UnitConverter.convertLinear(parsed, oldUnit, newUnit)
                }
                val newText = CircleFormatter.formatNumber(converted, decPlaces)
                _inputValueText.value = newText
                _selectedUnit.value = newUnit
                performCalculation(newText, _inputType.value, newUnit)
                return
            }
        }

        _selectedUnit.value = newUnit
        performCalculation(_inputValueText.value, _inputType.value, newUnit)
    }

    fun setProjectName(name: String) {
        _projectName.value = name
    }

    fun applyPreset(presetCm: BigDecimal) {
        val currentUnit = _selectedUnit.value
        val inputType = _inputType.value
        val decPlaces = settings.value.decimalPlaces

        // Convert the preset measurement in cm to the currently active unit
        val convertedValue = if (inputType == CircleInputType.AREA) {
            // If Area, interpret preset as circular area of radius (presetCm)
            // Area in cm2 = PI * presetCm^2
            val areaCm2 = CircleEdgeCalculator.calculateArea(
                UnitConverter.toMillimeters(presetCm, MeasurementUnit.CENTIMETER)
            )
            val inSquareMm = areaCm2
            UnitConverter.fromSquareMillimeters(inSquareMm, currentUnit)
        } else {
            val mm = UnitConverter.toMillimeters(presetCm, MeasurementUnit.CENTIMETER)
            UnitConverter.fromMillimeters(mm, currentUnit)
        }

        val text = CircleFormatter.formatNumber(convertedValue, decPlaces)
        _inputValueText.value = text
        performCalculation(text, inputType, currentUnit)
    }

    fun resetCalculator() {
        val defaultType = settings.value.defaultInputType
        val defaultUnit = settings.value.defaultUnit
        _inputType.value = defaultType
        _selectedUnit.value = defaultUnit
        _projectName.value = ""
        val defaultVal = "10"
        _inputValueText.value = defaultVal
        performCalculation(defaultVal, defaultType, defaultUnit)
    }

    private fun performCalculation(
        rawText: String,
        type: CircleInputType,
        unit: MeasurementUnit
    ) {
        val text = rawText.trim()
        if (text.isEmpty()) {
            _validationError.value = "Enter a value to calculate."
            _calculationResult.value = null
            return
        }

        val parsed = try {
            BigDecimal(text)
        } catch (_: Exception) {
            _validationError.value = "Enter a valid number."
            _calculationResult.value = null
            return
        }

        if (parsed <= BigDecimal.ZERO) {
            _validationError.value = "Value must be greater than zero."
            _calculationResult.value = null
            return
        }

        try {
            val result = when (type) {
                CircleInputType.RADIUS -> {
                    val radiusMm = UnitConverter.toMillimeters(parsed, unit)
                    CircleEdgeCalculator.calculateFromRadius(radiusMm)
                }
                CircleInputType.DIAMETER -> {
                    val diameterMm = UnitConverter.toMillimeters(parsed, unit)
                    CircleEdgeCalculator.calculateFromDiameter(diameterMm)
                }
                CircleInputType.CIRCUMFERENCE -> {
                    val circMm = UnitConverter.toMillimeters(parsed, unit)
                    CircleEdgeCalculator.calculateFromCircumference(circMm)
                }
                CircleInputType.AREA -> {
                    val areaMm2 = UnitConverter.toSquareMillimeters(parsed, unit)
                    CircleEdgeCalculator.calculateFromArea(areaMm2)
                }
            }
            _validationError.value = null
            _calculationResult.value = result
        } catch (e: Exception) {
            _validationError.value = e.message ?: "Invalid calculation"
            _calculationResult.value = null
        }
    }

    fun saveCurrentCalculation(onSaved: () -> Unit = {}) {
        val result = _calculationResult.value ?: return
        val text = _inputValueText.value.trim()
        val parsed = try { BigDecimal(text) } catch (_: Exception) { return }

        val calc = SavedCalculation(
            projectName = _projectName.value.trim().ifEmpty { "Untitled Circle" },
            inputType = _inputType.value,
            inputValue = parsed,
            unit = _selectedUnit.value,
            radiusMm = result.radiusMm,
            diameterMm = result.diameterMm,
            circumferenceMm = result.circumferenceMm,
            areaMm2 = result.areaMm2,
            timestampMillis = System.currentTimeMillis()
        )

        viewModelScope.launch {
            preferences.saveCalculation(calc)
            onSaved()
        }
    }

    fun restoreCalculation(saved: SavedCalculation) {
        _inputType.value = saved.inputType
        _selectedUnit.value = saved.unit
        _projectName.value = saved.projectName
        val formatted = CircleFormatter.formatNumber(saved.inputValue, settings.value.decimalPlaces)
        _inputValueText.value = formatted
        performCalculation(formatted, saved.inputType, saved.unit)
    }

    fun duplicateCalculation(saved: SavedCalculation) {
        viewModelScope.launch {
            preferences.duplicateCalculation(saved)
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            preferences.toggleFavorite(id)
        }
    }

    fun deleteCalculation(id: String) {
        viewModelScope.launch {
            preferences.deleteCalculation(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            preferences.clearAllCalculations()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            preferences.updateSettings(newSettings)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            preferences.resetSettings()
        }
    }
}
