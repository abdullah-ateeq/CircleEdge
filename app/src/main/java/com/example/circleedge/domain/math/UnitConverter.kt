package com.example.circleedge.domain.math

import com.example.circleedge.data.model.MeasurementUnit
import java.math.BigDecimal

object UnitConverter {

    /**
     * Converts a linear measurement from the specified unit to millimeters.
     */
    fun toMillimeters(value: BigDecimal, unit: MeasurementUnit): BigDecimal {
        return BigDecimalMath.multiplySafe(value, unit.linearFactorMm)
    }

    /**
     * Converts a linear measurement from millimeters to the specified unit.
     */
    fun fromMillimeters(valueMm: BigDecimal, unit: MeasurementUnit): BigDecimal {
        return BigDecimalMath.divideSafe(valueMm, unit.linearFactorMm)
    }

    /**
     * Converts an area measurement from the specified unit to square millimeters.
     */
    fun toSquareMillimeters(value: BigDecimal, unit: MeasurementUnit): BigDecimal {
        return BigDecimalMath.multiplySafe(value, unit.areaFactorMm2)
    }

    /**
     * Converts an area measurement from square millimeters to the specified unit.
     */
    fun fromSquareMillimeters(valueMm2: BigDecimal, unit: MeasurementUnit): BigDecimal {
        return BigDecimalMath.divideSafe(valueMm2, unit.areaFactorMm2)
    }

    /**
     * Converts a linear value directly between two units.
     */
    fun convertLinear(value: BigDecimal, fromUnit: MeasurementUnit, toUnit: MeasurementUnit): BigDecimal {
        if (fromUnit == toUnit) return value
        val mm = toMillimeters(value, fromUnit)
        return fromMillimeters(mm, toUnit)
    }

    /**
     * Converts an area value directly between two units.
     */
    fun convertArea(value: BigDecimal, fromUnit: MeasurementUnit, toUnit: MeasurementUnit): BigDecimal {
        if (fromUnit == toUnit) return value
        val mm2 = toSquareMillimeters(value, fromUnit)
        return fromSquareMillimeters(mm2, toUnit)
    }
}
