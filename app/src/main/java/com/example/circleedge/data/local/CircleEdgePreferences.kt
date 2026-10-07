package com.example.circleedge.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.circleedge.data.model.AppSettings
import com.example.circleedge.data.model.AppThemeMode
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.data.model.SavedCalculation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "circle_edge_prefs")

class CircleEdgePreferences(private val context: Context) {

    companion object {
        const val MAX_SAVED_CALCULATIONS = 50

        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val KEY_DEFAULT_INPUT_TYPE = stringPreferencesKey("default_input_type")
        val KEY_DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val KEY_DECIMAL_PLACES = intPreferencesKey("decimal_places")
        val KEY_SHOW_DIAGRAM = booleanPreferencesKey("show_diagram")
        val KEY_SHOW_FORMULA = booleanPreferencesKey("show_formula")
        val KEY_SHOW_SECONDARY_DETAILS = booleanPreferencesKey("show_secondary_details")
        val KEY_SHOW_RADIUS = booleanPreferencesKey("show_radius")
        val KEY_SHOW_DIAMETER = booleanPreferencesKey("show_diameter")
        val KEY_SHOW_CIRCUMFERENCE = booleanPreferencesKey("show_circumference")
        val KEY_SHOW_AREA = booleanPreferencesKey("show_area")
        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val KEY_CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")
        val KEY_SAVED_CALCULATIONS = stringPreferencesKey("saved_calculations")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = AppThemeMode.valueOf(prefs[KEY_THEME_MODE] ?: AppThemeMode.SYSTEM.name),
            dynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: true,
            defaultInputType = CircleInputType.valueOf(prefs[KEY_DEFAULT_INPUT_TYPE] ?: CircleInputType.RADIUS.name),
            defaultUnit = MeasurementUnit.valueOf(prefs[KEY_DEFAULT_UNIT] ?: MeasurementUnit.CENTIMETER.name),
            decimalPlaces = (prefs[KEY_DECIMAL_PLACES] ?: 2).coerceIn(0, 4),
            showDiagram = prefs[KEY_SHOW_DIAGRAM] ?: true,
            showFormula = prefs[KEY_SHOW_FORMULA] ?: true,
            showSecondaryDetails = prefs[KEY_SHOW_SECONDARY_DETAILS] ?: true,
            showRadius = prefs[KEY_SHOW_RADIUS] ?: true,
            showDiameter = prefs[KEY_SHOW_DIAMETER] ?: true,
            showCircumference = prefs[KEY_SHOW_CIRCUMFERENCE] ?: true,
            showArea = prefs[KEY_SHOW_AREA] ?: true,
            hapticFeedback = prefs[KEY_HAPTIC_FEEDBACK] ?: true,
            confirmDelete = prefs[KEY_CONFIRM_DELETE] ?: true
        )
    }

    val savedCalculationsFlow: Flow<List<SavedCalculation>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_SAVED_CALCULATIONS] ?: "[]"
        CalculationSerializer.deserializeList(json)
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = settings.themeMode.name
            prefs[KEY_DYNAMIC_COLOR] = settings.dynamicColor
            prefs[KEY_DEFAULT_INPUT_TYPE] = settings.defaultInputType.name
            prefs[KEY_DEFAULT_UNIT] = settings.defaultUnit.name
            prefs[KEY_DECIMAL_PLACES] = settings.decimalPlaces
            prefs[KEY_SHOW_DIAGRAM] = settings.showDiagram
            prefs[KEY_SHOW_FORMULA] = settings.showFormula
            prefs[KEY_SHOW_SECONDARY_DETAILS] = settings.showSecondaryDetails
            prefs[KEY_SHOW_RADIUS] = settings.showRadius
            prefs[KEY_SHOW_DIAMETER] = settings.showDiameter
            prefs[KEY_SHOW_CIRCUMFERENCE] = settings.showCircumference
            prefs[KEY_SHOW_AREA] = settings.showArea
            prefs[KEY_HAPTIC_FEEDBACK] = settings.hapticFeedback
            prefs[KEY_CONFIRM_DELETE] = settings.confirmDelete
        }
    }

    suspend fun resetSettings() {
        updateSettings(AppSettings())
    }

    suspend fun saveCalculation(calculation: SavedCalculation) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: "[]"
            val currentList = CalculationSerializer.deserializeList(json).toMutableList()

            // Insert newest at the beginning
            currentList.add(0, calculation)

            // Keep maximum 50 records, drop oldest
            val trimmedList = if (currentList.size > MAX_SAVED_CALCULATIONS) {
                currentList.take(MAX_SAVED_CALCULATIONS)
            } else {
                currentList
            }

            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.serializeList(trimmedList)
        }
    }

    suspend fun duplicateCalculation(calculation: SavedCalculation) {
        val duplicated = calculation.copy(
            id = UUID.randomUUID().toString(),
            projectName = "Copy of ${calculation.displayProjectName}",
            timestampMillis = System.currentTimeMillis()
        )
        saveCalculation(duplicated)
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: "[]"
            val list = CalculationSerializer.deserializeList(json).map { item ->
                if (item.id == id) {
                    item.copy(isFavorite = !item.isFavorite)
                } else {
                    item
                }
            }
            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.serializeList(list)
        }
    }

    suspend fun deleteCalculation(id: String) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SAVED_CALCULATIONS] ?: "[]"
            val list = CalculationSerializer.deserializeList(json).filter { it.id != id }
            prefs[KEY_SAVED_CALCULATIONS] = CalculationSerializer.serializeList(list)
        }
    }

    suspend fun clearAllCalculations() {
        context.dataStore.edit { prefs ->
            prefs[KEY_SAVED_CALCULATIONS] = "[]"
        }
    }
}
