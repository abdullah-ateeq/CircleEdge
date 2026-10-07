package com.example.circleedge.data.model

import java.math.BigDecimal

data class CircleInput(
    val projectName: String = "",
    val inputType: CircleInputType = CircleInputType.RADIUS,
    val inputValue: BigDecimal? = null,
    val unit: MeasurementUnit = MeasurementUnit.CENTIMETER
)

data class CircleResult(
    val radiusMm: BigDecimal,
    val diameterMm: BigDecimal,
    val circumferenceMm: BigDecimal,
    val areaMm2: BigDecimal
)
