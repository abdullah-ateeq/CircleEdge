package com.example.circleedge.data.model

import java.math.BigDecimal
import java.util.UUID

data class SavedCalculation(
    val id: String = UUID.randomUUID().toString(),
    val projectName: String = "Untitled Circle",
    val inputType: CircleInputType = CircleInputType.RADIUS,
    val inputValue: BigDecimal,
    val unit: MeasurementUnit = MeasurementUnit.CENTIMETER,
    val radiusMm: BigDecimal,
    val diameterMm: BigDecimal,
    val circumferenceMm: BigDecimal,
    val areaMm2: BigDecimal,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val displayProjectName: String
        get() = projectName.ifBlank { "Untitled Circle" }
}
