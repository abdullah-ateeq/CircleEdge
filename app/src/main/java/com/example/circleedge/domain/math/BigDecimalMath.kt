package com.example.circleedge.domain.math

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {

    val MATH_CONTEXT: MathContext = MathContext(34, RoundingMode.HALF_UP)

    // High precision Pi constant (100+ digits)
    val PI: BigDecimal = BigDecimal(
        "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679"
    )

    val TWO: BigDecimal = BigDecimal("2")
    val FOUR: BigDecimal = BigDecimal("4")
    val TWO_PI: BigDecimal = TWO.multiply(PI, MATH_CONTEXT)
    val FOUR_PI: BigDecimal = FOUR.multiply(PI, MATH_CONTEXT)

    /**
     * High precision square root implementation for BigDecimal using Newton-Raphson.
     * Guaranteed to converge safely without NaN, Infinity, or overflow.
     * Handles normal, extremely small, and extremely large numbers safely.
     */
    fun sqrtBigDecimal(value: BigDecimal, mc: MathContext = MATH_CONTEXT): BigDecimal {
        if (value <= BigDecimal.ZERO) {
            throw IllegalArgumentException("Value must be greater than zero for square root: $value")
        }

        // Generate a safe initial guess
        val doubleVal = value.toDouble()
        var x: BigDecimal = if (doubleVal > 0.0 && !doubleVal.isInfinite() && !doubleVal.isNaN()) {
            BigDecimal(Math.sqrt(doubleVal), mc)
        } else {
            // For extreme numbers where Double overflows or underflows
            val power = (value.precision() - value.scale()) / 2
            if (power >= 0) {
                BigDecimal.TEN.pow(power, mc)
            } else {
                BigDecimal.ONE.divide(BigDecimal.TEN.pow(-power, mc), mc)
            }
        }

        if (x <= BigDecimal.ZERO) {
            x = BigDecimal.ONE
        }

        val two = BigDecimal(2)
        val tolerance = BigDecimal("1E-32")
        val maxIterations = 60

        for (i in 0 until maxIterations) {
            val nextX = x.add(value.divide(x, mc), mc).divide(two, mc)
            val diff = nextX.subtract(x, mc).abs()
            if (diff <= tolerance || nextX == x) {
                return nextX
            }
            x = nextX
        }

        return x
    }

    fun divideSafe(
        dividend: BigDecimal,
        divisor: BigDecimal,
        mc: MathContext = MATH_CONTEXT
    ): BigDecimal {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw ArithmeticException("Division by zero")
        }
        return dividend.divide(divisor, mc)
    }

    fun multiplySafe(
        a: BigDecimal,
        b: BigDecimal,
        mc: MathContext = MATH_CONTEXT
    ): BigDecimal {
        return a.multiply(b, mc)
    }
}
