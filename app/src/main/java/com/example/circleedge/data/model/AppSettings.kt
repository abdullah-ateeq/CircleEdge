package com.example.circleedge.data.model

enum class AppThemeMode(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val defaultInputType: CircleInputType = CircleInputType.RADIUS,
    val defaultUnit: MeasurementUnit = MeasurementUnit.CENTIMETER,
    val decimalPlaces: Int = 2,
    val showDiagram: Boolean = true,
    val showFormula: Boolean = true,
    val showSecondaryDetails: Boolean = true,
    val showRadius: Boolean = true,
    val showDiameter: Boolean = true,
    val showCircumference: Boolean = true,
    val showArea: Boolean = true,
    val hapticFeedback: Boolean = true,
    val confirmDelete: Boolean = true
)
