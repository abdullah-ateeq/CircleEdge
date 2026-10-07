package com.example.circleedge

import com.example.circleedge.data.local.CalculationSerializer
import com.example.circleedge.data.model.AppSettings
import com.example.circleedge.data.model.AppThemeMode
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.data.model.SavedCalculation
import com.example.circleedge.domain.format.CircleFormatter
import com.example.circleedge.domain.math.BigDecimalMath
import com.example.circleedge.domain.math.CircleEdgeCalculator
import com.example.circleedge.domain.math.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CircleEdgeTests {

    private val tolerance = BigDecimal("0.001")

    private fun assertClose(expected: BigDecimal, actual: BigDecimal, message: String = "") {
        val diff = expected.subtract(actual).abs()
        assertTrue(
            "Expected $expected but was $actual (diff: $diff). $message",
            diff <= tolerance
        )
    }

    // 1. Calculate from radius
    // Radius = 10 cm (100 mm) -> D = 200 mm (20 cm), C ≈ 628.3185 mm (62.8319 cm), A ≈ 31415.9265 mm² (314.1593 cm²)
    @Test
    fun test1_calculateFromRadius() {
        val radiusMm = BigDecimal("100") // 10 cm
        val result = CircleEdgeCalculator.calculateFromRadius(radiusMm)

        val radiusCm = UnitConverter.fromMillimeters(result.radiusMm, MeasurementUnit.CENTIMETER)
        val diameterCm = UnitConverter.fromMillimeters(result.diameterMm, MeasurementUnit.CENTIMETER)
        val circCm = UnitConverter.fromMillimeters(result.circumferenceMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)

        assertClose(BigDecimal("10"), radiusCm)
        assertClose(BigDecimal("20"), diameterCm)
        assertClose(BigDecimal("62.8319"), circCm)
        assertClose(BigDecimal("314.1593"), areaCm2)
    }

    // 2. Calculate from diameter
    // Diameter = 20 cm (200 mm) -> r = 100 mm (10 cm), C ≈ 62.8319 cm, A ≈ 314.1593 cm²
    @Test
    fun test2_calculateFromDiameter() {
        val diameterMm = BigDecimal("200") // 20 cm
        val result = CircleEdgeCalculator.calculateFromDiameter(diameterMm)

        val radiusCm = UnitConverter.fromMillimeters(result.radiusMm, MeasurementUnit.CENTIMETER)
        val diameterCm = UnitConverter.fromMillimeters(result.diameterMm, MeasurementUnit.CENTIMETER)
        val circCm = UnitConverter.fromMillimeters(result.circumferenceMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)

        assertClose(BigDecimal("10"), radiusCm)
        assertClose(BigDecimal("20"), diameterCm)
        assertClose(BigDecimal("62.8319"), circCm)
        assertClose(BigDecimal("314.1593"), areaCm2)
    }

    // 3. Calculate from circumference
    // Circumference = 62.8319 cm -> r ≈ 10 cm, D ≈ 20 cm, A ≈ 314.1593 cm²
    @Test
    fun test3_calculateFromCircumference() {
        val circMm = UnitConverter.toMillimeters(BigDecimal("62.831853"), MeasurementUnit.CENTIMETER)
        val result = CircleEdgeCalculator.calculateFromCircumference(circMm)

        val radiusCm = UnitConverter.fromMillimeters(result.radiusMm, MeasurementUnit.CENTIMETER)
        val diameterCm = UnitConverter.fromMillimeters(result.diameterMm, MeasurementUnit.CENTIMETER)
        val areaCm2 = UnitConverter.fromSquareMillimeters(result.areaMm2, MeasurementUnit.CENTIMETER)

        assertClose(BigDecimal("10"), radiusCm)
        assertClose(BigDecimal("20"), diameterCm)
        assertClose(BigDecimal("314.1593"), areaCm2)
    }

    // 4. Calculate from area
    // Area = 314.1593 cm² -> r ≈ 10 cm, D ≈ 20 cm, C ≈ 62.8319 cm
    @Test
    fun test4_calculateFromArea() {
        val areaMm2 = UnitConverter.toSquareMillimeters(BigDecimal("314.159265"), MeasurementUnit.CENTIMETER)
        val result = CircleEdgeCalculator.calculateFromArea(areaMm2)

        val radiusCm = UnitConverter.fromMillimeters(result.radiusMm, MeasurementUnit.CENTIMETER)
        val diameterCm = UnitConverter.fromMillimeters(result.diameterMm, MeasurementUnit.CENTIMETER)
        val circCm = UnitConverter.fromMillimeters(result.circumferenceMm, MeasurementUnit.CENTIMETER)

        assertClose(BigDecimal("10"), radiusCm)
        assertClose(BigDecimal("20"), diameterCm)
        assertClose(BigDecimal("62.8319"), circCm)
    }

    // 5. Diameter calculation
    @Test
    fun test5_diameterCalculation() {
        val r = BigDecimal("45.5")
        val d = CircleEdgeCalculator.calculateDiameter(r)
        assertEquals(BigDecimal("91.0"), d.setScale(1, RoundingMode.HALF_UP))
    }

    // 6. Circumference calculation
    @Test
    fun test6_circumferenceCalculation() {
        val r = BigDecimal("50") // 50 mm
        val c = CircleEdgeCalculator.calculateCircumference(r)
        // 2 * PI * 50 = 100 * PI ≈ 314.159265
        assertClose(BigDecimal("314.159"), c.setScale(3, RoundingMode.HALF_UP))
    }

    // 7. Area calculation
    @Test
    fun test7_areaCalculation() {
        val r = BigDecimal("10")
        val a = CircleEdgeCalculator.calculateArea(r)
        // PI * 10^2 = 100 * PI ≈ 314.159265
        assertClose(BigDecimal("314.159"), a.setScale(3, RoundingMode.HALF_UP))
    }

    // 8. Radius recovery
    // Given area -> recover radius -> calculate area -> match original
    @Test
    fun test8_radiusRecovery() {
        val originalR = BigDecimal("25.75")
        val area = CircleEdgeCalculator.calculateArea(originalR)
        val recoveredR = CircleEdgeCalculator.calculateRadiusFromArea(area)
        assertClose(originalR, recoveredR)
    }

    // 9. Diameter recovery
    // Given radius -> calculate diameter -> recover radius -> match original
    @Test
    fun test9_diameterRecovery() {
        val originalR = BigDecimal("33.333")
        val d = CircleEdgeCalculator.calculateDiameter(originalR)
        val recoveredR = CircleEdgeCalculator.calculateRadiusFromDiameter(d)
        assertClose(originalR, recoveredR)
    }

    // 10. Circumference recovery
    // Given radius -> calculate circumference -> recover radius -> match original
    @Test
    fun test10_circumferenceRecovery() {
        val originalR = BigDecimal("18.42")
        val c = CircleEdgeCalculator.calculateCircumference(originalR)
        val recoveredR = CircleEdgeCalculator.calculateRadiusFromCircumference(c)
        assertClose(originalR, recoveredR)
    }

    // 11. Area recovery
    // Start with arbitrary area -> calculateFromArea -> check result.areaMm2 matches
    @Test
    fun test11_areaRecovery() {
        val initialArea = BigDecimal("1234.5678")
        val result = CircleEdgeCalculator.calculateFromArea(initialArea)
        assertClose(initialArea, result.areaMm2)
    }

    // 12. mm conversion
    @Test
    fun test12_mmConversion() {
        val v = BigDecimal("123.45")
        assertEquals(v, UnitConverter.toMillimeters(v, MeasurementUnit.MILLIMETER))
        assertEquals(v, UnitConverter.fromMillimeters(v, MeasurementUnit.MILLIMETER))
    }

    // 13. cm conversion
    @Test
    fun test13_cmConversion() {
        val cm = BigDecimal("15.5")
        val mm = UnitConverter.toMillimeters(cm, MeasurementUnit.CENTIMETER)
        assertClose(BigDecimal("155.0"), mm)
        val backToCm = UnitConverter.fromMillimeters(mm, MeasurementUnit.CENTIMETER)
        assertClose(cm, backToCm)
    }

    // 14. m conversion
    @Test
    fun test14_mConversion() {
        val m = BigDecimal("2.5")
        val mm = UnitConverter.toMillimeters(m, MeasurementUnit.METER)
        assertClose(BigDecimal("2500"), mm)
        val backToM = UnitConverter.fromMillimeters(mm, MeasurementUnit.METER)
        assertClose(m, backToM)
    }

    // 15. inch conversion
    // 1 in = 25.4 mm
    @Test
    fun test15_inchConversion() {
        val inches = BigDecimal("10")
        val mm = UnitConverter.toMillimeters(inches, MeasurementUnit.INCH)
        assertClose(BigDecimal("254.0"), mm)
        val backToInches = UnitConverter.fromMillimeters(mm, MeasurementUnit.INCH)
        assertClose(inches, backToInches)
    }

    // 16. foot conversion
    // 1 ft = 304.8 mm
    @Test
    fun test16_footConversion() {
        val feet = BigDecimal("3")
        val mm = UnitConverter.toMillimeters(feet, MeasurementUnit.FOOT)
        assertClose(BigDecimal("914.4"), mm)
        val backToFeet = UnitConverter.fromMillimeters(mm, MeasurementUnit.FOOT)
        assertClose(feet, backToFeet)
    }

    // 17. Area unit conversion
    // 1 cm² = 100 mm², 1 m² = 1,000,000 mm², 1 in² = 645.16 mm², 1 ft² = 92903.04 mm²
    @Test
    fun test17_areaUnitConversion() {
        val oneCm2 = BigDecimal("1")
        assertClose(BigDecimal("100"), UnitConverter.toSquareMillimeters(oneCm2, MeasurementUnit.CENTIMETER))

        val oneM2 = BigDecimal("1")
        assertClose(BigDecimal("1000000"), UnitConverter.toSquareMillimeters(oneM2, MeasurementUnit.METER))

        val oneIn2 = BigDecimal("1")
        assertClose(BigDecimal("645.16"), UnitConverter.toSquareMillimeters(oneIn2, MeasurementUnit.INCH))

        val oneFt2 = BigDecimal("1")
        assertClose(BigDecimal("92903.04"), UnitConverter.toSquareMillimeters(oneFt2, MeasurementUnit.FOOT))
    }

    // 18. Decimal values
    @Test
    fun test18_decimalValues() {
        val r = BigDecimal("0.123456")
        val result = CircleEdgeCalculator.calculateFromRadius(r)
        assertNotNull(result)
        assertTrue(result.diameterMm > BigDecimal.ZERO)
        assertTrue(result.circumferenceMm > BigDecimal.ZERO)
        assertTrue(result.areaMm2 > BigDecimal.ZERO)
    }

    // 19. Very small values
    @Test
    fun test19_verySmallValues() {
        val smallR = BigDecimal("0.000000001")
        val result = CircleEdgeCalculator.calculateFromRadius(smallR)
        assertTrue(result.radiusMm > BigDecimal.ZERO)
        assertTrue(result.diameterMm > BigDecimal.ZERO)
        val recoveredR = CircleEdgeCalculator.calculateRadiusFromArea(result.areaMm2)
        assertClose(smallR, recoveredR)
    }

    // 20. Very large values
    @Test
    fun test20_veryLargeValues() {
        val largeR = BigDecimal("100000000000")
        val result = CircleEdgeCalculator.calculateFromRadius(largeR)
        assertTrue(result.radiusMm > BigDecimal.ZERO)
        assertTrue(result.diameterMm > BigDecimal.ZERO)
        assertTrue(result.circumferenceMm > BigDecimal.ZERO)
        assertTrue(result.areaMm2 > BigDecimal.ZERO)
    }

    // 21. Zero input
    @Test
    fun test21_zeroInput() {
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromRadius(BigDecimal.ZERO)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromDiameter(BigDecimal.ZERO)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromCircumference(BigDecimal.ZERO)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromArea(BigDecimal.ZERO)
        }
    }

    // 22. Negative input
    @Test
    fun test22_negativeInput() {
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromRadius(BigDecimal("-5"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromDiameter(BigDecimal("-10"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromCircumference(BigDecimal("-20"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CircleEdgeCalculator.calculateFromArea(BigDecimal("-50"))
        }
    }

    // 23. Invalid numeric input
    @Test
    fun test23_invalidNumericInput() {
        val invalidInputs = listOf("abc", "12.34.56", "", "   ", "--1", "1e-e")
        for (input in invalidInputs) {
            val parsed = try {
                BigDecimal(input.trim())
            } catch (_: Exception) {
                null
            }
            val isValid = parsed != null && parsed > BigDecimal.ZERO
            assertFalse("Input '$input' should be invalid", isValid)
        }
    }

    // 24. BigDecimal precision
    @Test
    fun test24_bigDecimalPrecision() {
        // Ensure calculations do not lose precision like Double does
        val highPrecisionR = BigDecimal("10.12345678901234567890")
        val d = CircleEdgeCalculator.calculateDiameter(highPrecisionR)
        assertEquals(
            "20.24691357802469135780",
            d.setScale(20, RoundingMode.HALF_UP).toPlainString()
        )
    }

    // 25. History serialization
    @Test
    fun test25_historySerialization() {
        val calc1 = SavedCalculation(
            id = "test-1",
            projectName = "Round Table",
            inputType = CircleInputType.RADIUS,
            inputValue = BigDecimal("60"),
            unit = MeasurementUnit.CENTIMETER,
            radiusMm = BigDecimal("600"),
            diameterMm = BigDecimal("1200"),
            circumferenceMm = BigDecimal("3769.911"),
            areaMm2 = BigDecimal("1130973.355"),
            timestampMillis = 1700000000000L,
            isFavorite = true
        )
        val calc2 = SavedCalculation(
            id = "test-2",
            projectName = "Pipe Section",
            inputType = CircleInputType.DIAMETER,
            inputValue = BigDecimal("20"),
            unit = MeasurementUnit.CENTIMETER,
            radiusMm = BigDecimal("100"),
            diameterMm = BigDecimal("200"),
            circumferenceMm = BigDecimal("628.318"),
            areaMm2 = BigDecimal("31415.926"),
            timestampMillis = 1700000001000L,
            isFavorite = false
        )

        val json = CalculationSerializer.serializeList(listOf(calc1, calc2))
        val deserialized = CalculationSerializer.deserializeList(json)

        assertEquals(2, deserialized.size)
        assertEquals("test-1", deserialized[0].id)
        assertEquals("Round Table", deserialized[0].projectName)
        assertEquals(CircleInputType.RADIUS, deserialized[0].inputType)
        assertTrue(deserialized[0].isFavorite)

        assertEquals("test-2", deserialized[1].id)
        assertEquals("Pipe Section", deserialized[1].projectName)
        assertEquals(CircleInputType.DIAMETER, deserialized[1].inputType)
        assertFalse(deserialized[1].isFavorite)
    }

    // 26. Settings persistence model
    @Test
    fun test26_settingsPersistenceModel() {
        val defaultSettings = AppSettings()
        assertEquals(AppThemeMode.SYSTEM, defaultSettings.themeMode)
        assertTrue(defaultSettings.dynamicColor)
        assertEquals(CircleInputType.RADIUS, defaultSettings.defaultInputType)
        assertEquals(MeasurementUnit.CENTIMETER, defaultSettings.defaultUnit)
        assertEquals(2, defaultSettings.decimalPlaces)
        assertTrue(defaultSettings.showDiagram)
        assertTrue(defaultSettings.showFormula)

        val custom = defaultSettings.copy(
            themeMode = AppThemeMode.DARK,
            decimalPlaces = 4,
            defaultUnit = MeasurementUnit.INCH
        )
        assertEquals(AppThemeMode.DARK, custom.themeMode)
        assertEquals(4, custom.decimalPlaces)
        assertEquals(MeasurementUnit.INCH, custom.defaultUnit)
    }

    // 27. Favorites
    @Test
    fun test27_favorites() {
        val item = SavedCalculation(
            id = "fav-1",
            projectName = "Flange",
            inputValue = BigDecimal("15"),
            radiusMm = BigDecimal("150"),
            diameterMm = BigDecimal("300"),
            circumferenceMm = BigDecimal("942.477"),
            areaMm2 = BigDecimal("70685.834"),
            isFavorite = false
        )

        val toggled = item.copy(isFavorite = !item.isFavorite)
        assertTrue(toggled.isFavorite)

        val toggledBack = toggled.copy(isFavorite = !toggled.isFavorite)
        assertFalse(toggledBack.isFavorite)
    }
}
