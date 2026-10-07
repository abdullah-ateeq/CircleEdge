package com.example.circleedge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.CircleResult
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.domain.format.CircleFormatter
import com.example.circleedge.ui.theme.AreaPurple
import com.example.circleedge.ui.theme.AreaPurpleDark
import com.example.circleedge.ui.theme.CircumferenceTeal
import com.example.circleedge.ui.theme.CircumferenceTealDark
import com.example.circleedge.ui.theme.DiameterAmber
import com.example.circleedge.ui.theme.DiameterAmberDark
import com.example.circleedge.ui.theme.RadiusBlue
import com.example.circleedge.ui.theme.RadiusBlueDark
import kotlin.math.min

@Composable
fun CircleDiagram(
    result: CircleResult?,
    inputType: CircleInputType,
    unit: MeasurementUnit,
    decimalPlaces: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val isDark = isSystemInDarkTheme()

    val colorScheme = MaterialTheme.colorScheme
    val radiusColor = if (isDark) RadiusBlueDark else RadiusBlue
    val diameterColor = if (isDark) DiameterAmberDark else DiameterAmber
    val circColor = if (isDark) CircumferenceTealDark else CircumferenceTeal
    val areaColor = if (isDark) AreaPurpleDark else AreaPurple

    // Subtle animated emphasis when changing input type
    val animatedRadiusAlpha by animateFloatAsState(
        targetValue = if (inputType == CircleInputType.RADIUS) 1f else 0.75f,
        animationSpec = tween(300),
        label = "radiusAlpha"
    )
    val animatedAreaAlpha by animateFloatAsState(
        targetValue = if (inputType == CircleInputType.AREA) 0.22f else 0.06f,
        animationSpec = tween(300),
        label = "areaAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val minDim = min(size.width, size.height)
            // Ensure circle stays perfectly circular with padding for labels
            val visualRadius = minDim * 0.36f

            val gridColor = colorScheme.outline.copy(alpha = 0.2f)
            val strokeDashed = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Technical Center Crosshairs
            drawLine(
                color = gridColor,
                start = Offset(center.x - visualRadius * 1.25f, center.y),
                end = Offset(center.x + visualRadius * 1.25f, center.y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = strokeDashed.pathEffect
            )
            drawLine(
                color = gridColor,
                start = Offset(center.x, center.y - visualRadius * 1.25f),
                end = Offset(center.x, center.y + visualRadius * 1.25f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = strokeDashed.pathEffect
            )

            // Area fill
            drawCircle(
                color = if (inputType == CircleInputType.AREA) areaColor.copy(alpha = animatedAreaAlpha)
                else colorScheme.primary.copy(alpha = animatedAreaAlpha),
                radius = visualRadius,
                center = center
            )

            // Circumference halo if circumference is active
            if (inputType == CircleInputType.CIRCUMFERENCE) {
                drawCircle(
                    color = circColor.copy(alpha = 0.22f),
                    radius = visualRadius + 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            // Circle perimeter outline
            val perimeterColor = if (inputType == CircleInputType.CIRCUMFERENCE) circColor
            else colorScheme.outline.copy(alpha = 0.8f)
            val perimeterWidth = if (inputType == CircleInputType.CIRCUMFERENCE) 3.dp.toPx() else 2.dp.toPx()

            drawCircle(
                color = perimeterColor,
                radius = visualRadius,
                center = center,
                style = Stroke(width = perimeterWidth)
            )

            // Center point
            drawCircle(
                color = colorScheme.onSurface,
                radius = 3.5.dp.toPx(),
                center = center
            )

            // Label text styles
            val technicalTextStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )

            // Dimension drawing
            if (inputType == CircleInputType.DIAMETER) {
                // Diameter full line through center (horizontal)
                val left = Offset(center.x - visualRadius, center.y)
                val right = Offset(center.x + visualRadius, center.y)

                drawLine(
                    color = diameterColor,
                    start = left,
                    end = right,
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // End tick marks
                val tickH = 6.dp.toPx()
                drawLine(
                    color = diameterColor,
                    start = Offset(left.x, left.y - tickH),
                    end = Offset(left.x, left.y + tickH),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = diameterColor,
                    start = Offset(right.x, right.y - tickH),
                    end = Offset(right.x, right.y + tickH),
                    strokeWidth = 2.dp.toPx()
                )

                // Diameter label
                if (result != null) {
                    val dText = "d = ${CircleFormatter.formatDimension(result.diameterMm, unit, decimalPlaces)}"
                    val measured = textMeasurer.measure(dText, technicalTextStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = dText,
                        style = technicalTextStyle.copy(color = diameterColor),
                        topLeft = Offset(center.x - measured.size.width / 2f, center.y - measured.size.height - 6.dp.toPx())
                    )
                }
            } else {
                // Radius line from Center to Right Edge
                val edgePoint = Offset(center.x + visualRadius, center.y)
                val activeRadiusColor = if (inputType == CircleInputType.RADIUS) radiusColor else radiusColor.copy(alpha = 0.7f)

                drawLine(
                    color = activeRadiusColor,
                    start = center,
                    end = edgePoint,
                    strokeWidth = if (inputType == CircleInputType.RADIUS) 2.5.dp.toPx() else 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Radius edge tick
                val tickH = 5.dp.toPx()
                drawLine(
                    color = activeRadiusColor,
                    start = Offset(edgePoint.x, edgePoint.y - tickH),
                    end = Offset(edgePoint.x, edgePoint.y + tickH),
                    strokeWidth = 2.dp.toPx()
                )

                // Radius label
                if (result != null) {
                    val rText = "r = ${CircleFormatter.formatDimension(result.radiusMm, unit, decimalPlaces)}"
                    val measured = textMeasurer.measure(rText, technicalTextStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = rText,
                        style = technicalTextStyle.copy(
                            color = if (inputType == CircleInputType.RADIUS) radiusColor else colorScheme.onSurface
                        ),
                        topLeft = Offset(center.x + (visualRadius - measured.size.width) / 2f, center.y - measured.size.height - 5.dp.toPx())
                    )
                }
            }

            // Top Circumference or Area label indicator when active or space allows
            if (result != null) {
                if (inputType == CircleInputType.CIRCUMFERENCE) {
                    val cText = "C = ${CircleFormatter.formatDimension(result.circumferenceMm, unit, decimalPlaces)}"
                    val measured = textMeasurer.measure(cText, technicalTextStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = cText,
                        style = technicalTextStyle.copy(color = circColor),
                        topLeft = Offset(center.x - measured.size.width / 2f, center.y - visualRadius - measured.size.height - 8.dp.toPx())
                    )
                } else if (inputType == CircleInputType.AREA) {
                    val aText = "A = ${CircleFormatter.formatArea(result.areaMm2, unit, decimalPlaces)}"
                    val measured = textMeasurer.measure(aText, technicalTextStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = aText,
                        style = technicalTextStyle.copy(color = areaColor),
                        topLeft = Offset(center.x - measured.size.width / 2f, center.y + visualRadius / 2.5f)
                    )
                }
            }
        }
    }
}
