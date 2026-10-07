package com.example.circleedge.data.model

import java.math.BigDecimal

enum class MeasurementUnit(
    val linearSymbol: String,
    val areaSymbol: String,
    val label: String,
    val linearFactorMm: BigDecimal,
    val areaFactorMm2: BigDecimal
) {
    MILLIMETER(
        linearSymbol = "mm",
        areaSymbol = "mm²",
        label = "Millimeters",
        linearFactorMm = BigDecimal("1"),
        areaFactorMm2 = BigDecimal("1")
    ),
    CENTIMETER(
        linearSymbol = "cm",
        areaSymbol = "cm²",
        label = "Centimeters",
        linearFactorMm = BigDecimal("10"),
        areaFactorMm2 = BigDecimal("100")
    ),
    METER(
        linearSymbol = "m",
        areaSymbol = "m²",
        label = "Meters",
        linearFactorMm = BigDecimal("1000"),
        areaFactorMm2 = BigDecimal("1000000")
    ),
    INCH(
        linearSymbol = "in",
        areaSymbol = "in²",
        label = "Inches",
        linearFactorMm = BigDecimal("25.4"),
        areaFactorMm2 = BigDecimal("645.16")
    ),
    FOOT(
        linearSymbol = "ft",
        areaSymbol = "ft²",
        label = "Feet",
        linearFactorMm = BigDecimal("304.8"),
        areaFactorMm2 = BigDecimal("92903.04")
    );

    fun symbolFor(inputType: CircleInputType): String {
        return if (inputType == CircleInputType.AREA) areaSymbol else linearSymbol
    }
}
