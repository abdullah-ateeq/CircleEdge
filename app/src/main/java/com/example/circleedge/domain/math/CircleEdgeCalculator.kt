package com.example.circleedge.domain.math

import com.example.circleedge.data.model.CircleResult
import java.math.BigDecimal

object CircleEdgeCalculator {

    /**
     * Calculates all circle properties given a radius in millimeters.
     */
    fun calculateFromRadius(radiusMm: BigDecimal): CircleResult {
        require(radiusMm > BigDecimal.ZERO) { "Value must be greater than zero." }

        val diameter = calculateDiameter(radiusMm)
        val circumference = calculateCircumference(radiusMm)
        val area = calculateArea(radiusMm)

        return CircleResult(
            radiusMm = radiusMm,
            diameterMm = diameter,
            circumferenceMm = circumference,
            areaMm2 = area
        )
    }

    /**
     * Calculates all circle properties given a diameter in millimeters.
     * r = D / 2
     * C = πD
     * A = π(D / 2)²
     */
    fun calculateFromDiameter(diameterMm: BigDecimal): CircleResult {
        require(diameterMm > BigDecimal.ZERO) { "Value must be greater than zero." }

        val radius = calculateRadiusFromDiameter(diameterMm)
        val circumference = calculateCircumference(radius)
        val area = calculateArea(radius)

        return CircleResult(
            radiusMm = radius,
            diameterMm = diameterMm,
            circumferenceMm = circumference,
            areaMm2 = area
        )
    }

    /**
     * Calculates all circle properties given a circumference in millimeters.
     * r = C / (2π)
     * D = 2r
     * A = πr²
     */
    fun calculateFromCircumference(circumferenceMm: BigDecimal): CircleResult {
        require(circumferenceMm > BigDecimal.ZERO) { "Value must be greater than zero." }

        val radius = calculateRadiusFromCircumference(circumferenceMm)
        val diameter = calculateDiameter(radius)
        val area = calculateArea(radius)

        return CircleResult(
            radiusMm = radius,
            diameterMm = diameter,
            circumferenceMm = circumferenceMm,
            areaMm2 = area
        )
    }

    /**
     * Calculates all circle properties given an area in square millimeters.
     * r = √(A / π)
     * D = 2r
     * C = 2πr
     */
    fun calculateFromArea(areaMm2: BigDecimal): CircleResult {
        require(areaMm2 > BigDecimal.ZERO) { "Value must be greater than zero." }

        val radius = calculateRadiusFromArea(areaMm2)
        val diameter = calculateDiameter(radius)
        val circumference = calculateCircumference(radius)

        return CircleResult(
            radiusMm = radius,
            diameterMm = diameter,
            circumferenceMm = circumference,
            areaMm2 = areaMm2
        )
    }

    fun calculateDiameter(radiusMm: BigDecimal): BigDecimal {
        require(radiusMm > BigDecimal.ZERO) { "Value must be greater than zero." }
        return BigDecimalMath.multiplySafe(BigDecimalMath.TWO, radiusMm)
    }

    fun calculateCircumference(radiusMm: BigDecimal): BigDecimal {
        require(radiusMm > BigDecimal.ZERO) { "Value must be greater than zero." }
        return BigDecimalMath.multiplySafe(BigDecimalMath.TWO_PI, radiusMm)
    }

    fun calculateArea(radiusMm: BigDecimal): BigDecimal {
        require(radiusMm > BigDecimal.ZERO) { "Value must be greater than zero." }
        val rSquared = BigDecimalMath.multiplySafe(radiusMm, radiusMm)
        return BigDecimalMath.multiplySafe(BigDecimalMath.PI, rSquared)
    }

    fun calculateRadiusFromDiameter(diameterMm: BigDecimal): BigDecimal {
        require(diameterMm > BigDecimal.ZERO) { "Value must be greater than zero." }
        return BigDecimalMath.divideSafe(diameterMm, BigDecimalMath.TWO)
    }

    fun calculateRadiusFromCircumference(circumferenceMm: BigDecimal): BigDecimal {
        require(circumferenceMm > BigDecimal.ZERO) { "Value must be greater than zero." }
        return BigDecimalMath.divideSafe(circumferenceMm, BigDecimalMath.TWO_PI)
    }

    fun calculateRadiusFromArea(areaMm2: BigDecimal): BigDecimal {
        require(areaMm2 > BigDecimal.ZERO) { "Value must be greater than zero." }
        val dividedByPi = BigDecimalMath.divideSafe(areaMm2, BigDecimalMath.PI)
        return BigDecimalMath.sqrtBigDecimal(dividedByPi)
    }
}
