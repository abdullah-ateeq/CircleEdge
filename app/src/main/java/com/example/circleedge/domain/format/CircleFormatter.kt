package com.example.circleedge.domain.format

import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.CircleResult
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.domain.math.CircleEdgeCalculator
import com.example.circleedge.domain.math.UnitConverter
import java.math.BigDecimal
import java.math.RoundingMode

data class FormulaStep(
    val title: String,
    val formulaTemplate: String,
    val calculation: String,
    val result: String
)

object CircleFormatter {

    fun formatNumber(value: BigDecimal, decimalPlaces: Int): String {
        val safeScale = decimalPlaces.coerceIn(0, 4)
        return value.setScale(safeScale, RoundingMode.HALF_UP).toPlainString()
    }

    fun formatDimension(valueMm: BigDecimal, unit: MeasurementUnit, decimalPlaces: Int): String {
        val inUnit = UnitConverter.fromMillimeters(valueMm, unit)
        return "${formatNumber(inUnit, decimalPlaces)} ${unit.linearSymbol}"
    }

    fun formatArea(valueMm2: BigDecimal, unit: MeasurementUnit, decimalPlaces: Int): String {
        val inUnit = UnitConverter.fromSquareMillimeters(valueMm2, unit)
        return "${formatNumber(inUnit, decimalPlaces)} ${unit.areaSymbol}"
    }

    fun generateFormulaSteps(
        inputType: CircleInputType,
        inputValue: BigDecimal,
        unit: MeasurementUnit,
        decimalPlaces: Int,
        result: CircleResult
    ): List<FormulaStep> {
        val inputStr = formatNumber(inputValue, decimalPlaces)
        val radiusInUnit = UnitConverter.fromMillimeters(result.radiusMm, unit)
        val diameterInUnit = UnitConverter.fromMillimeters(result.diameterMm, unit)
        val circInUnit = UnitConverter.fromMillimeters(result.circumferenceMm, unit)
        val areaInUnit = UnitConverter.fromSquareMillimeters(result.areaMm2, unit)

        val rStr = formatNumber(radiusInUnit, decimalPlaces)
        val dStr = formatNumber(diameterInUnit, decimalPlaces)
        val cStr = formatNumber(circInUnit, decimalPlaces)
        val aStr = formatNumber(areaInUnit, decimalPlaces)

        val uLin = unit.linearSymbol
        val uArea = unit.areaSymbol

        return when (inputType) {
            CircleInputType.RADIUS -> listOf(
                FormulaStep(
                    title = "Diameter",
                    formulaTemplate = "D = 2 × r",
                    calculation = "2 × $inputStr",
                    result = "$dStr $uLin"
                ),
                FormulaStep(
                    title = "Circumference",
                    formulaTemplate = "C = 2 × π × r",
                    calculation = "2 × π × $inputStr",
                    result = "$cStr $uLin"
                ),
                FormulaStep(
                    title = "Area",
                    formulaTemplate = "A = π × r²",
                    calculation = "π × $inputStr²",
                    result = "$aStr $uArea"
                )
            )

            CircleInputType.DIAMETER -> listOf(
                FormulaStep(
                    title = "Radius",
                    formulaTemplate = "r = D ÷ 2",
                    calculation = "$inputStr ÷ 2",
                    result = "$rStr $uLin"
                ),
                FormulaStep(
                    title = "Circumference",
                    formulaTemplate = "C = π × D",
                    calculation = "π × $inputStr",
                    result = "$cStr $uLin"
                ),
                FormulaStep(
                    title = "Area",
                    formulaTemplate = "A = π × (D ÷ 2)²",
                    calculation = "π × ($inputStr ÷ 2)²",
                    result = "$aStr $uArea"
                )
            )

            CircleInputType.CIRCUMFERENCE -> listOf(
                FormulaStep(
                    title = "Radius",
                    formulaTemplate = "r = C ÷ (2π)",
                    calculation = "$inputStr ÷ (2π)",
                    result = "≈ $rStr $uLin"
                ),
                FormulaStep(
                    title = "Diameter",
                    formulaTemplate = "D = 2 × r",
                    calculation = "2 × $rStr",
                    result = "$dStr $uLin"
                ),
                FormulaStep(
                    title = "Area",
                    formulaTemplate = "A = π × r²",
                    calculation = "π × $rStr²",
                    result = "$aStr $uArea"
                )
            )

            CircleInputType.AREA -> listOf(
                FormulaStep(
                    title = "Radius",
                    formulaTemplate = "r = √(A ÷ π)",
                    calculation = "√($inputStr ÷ π)",
                    result = "≈ $rStr $uLin"
                ),
                FormulaStep(
                    title = "Diameter",
                    formulaTemplate = "D = 2 × r",
                    calculation = "2 × $rStr",
                    result = "$dStr $uLin"
                ),
                FormulaStep(
                    title = "Circumference",
                    formulaTemplate = "C = 2 × π × r",
                    calculation = "2 × π × $rStr",
                    result = "$cStr $uLin"
                )
            )
        }
    }

    /**
     * Creates formatted share / copy text.
     */
    fun formatShareText(
        projectName: String,
        result: CircleResult,
        unit: MeasurementUnit,
        decimalPlaces: Int
    ): String {
        val nameHeader = if (projectName.isNotBlank()) "$projectName\n" else ""
        val r = formatDimension(result.radiusMm, unit, decimalPlaces)
        val d = formatDimension(result.diameterMm, unit, decimalPlaces)
        val c = formatDimension(result.circumferenceMm, unit, decimalPlaces)
        val a = formatArea(result.areaMm2, unit, decimalPlaces)

        return "${nameHeader}CircleEdge\n\nRadius: $r\nDiameter: $d\nCircumference: $c\nArea: $a"
    }
}
