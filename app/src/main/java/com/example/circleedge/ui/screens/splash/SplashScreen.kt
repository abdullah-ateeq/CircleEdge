package com.example.circleedge.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.ui.theme.AreaPurple
import com.example.circleedge.ui.theme.CircumferenceTeal
import com.example.circleedge.ui.theme.DiameterAmber
import com.example.circleedge.ui.theme.RadiusBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
        delay(150)
        onAnimationComplete()
    }

    val p = progress.value
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Technical Animated Circle Geometry
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width * 0.38f

                    // Stage 5: Area fill (0.6 -> 0.9)
                    if (p > 0.6f) {
                        val areaAlpha = ((p - 0.6f) / 0.3f).coerceIn(0f, 1f) * 0.15f
                        drawCircle(
                            color = AreaPurple.copy(alpha = areaAlpha),
                            radius = radius,
                            center = center
                        )
                    }

                    // Stage 1: Circle outline draws (0.0 -> 0.4)
                    val circleSweep = (p / 0.4f).coerceIn(0f, 1f) * 360f
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = circleSweep,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Stage 4: Circumference highlight ring (0.5 -> 0.8)
                    if (p > 0.5f) {
                        val circAlpha = ((p - 0.5f) / 0.3f).coerceIn(0f, 1f) * 0.35f
                        drawCircle(
                            color = CircumferenceTeal.copy(alpha = circAlpha),
                            radius = radius + 3.dp.toPx(),
                            center = center,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // Center point
                    if (p > 0.2f) {
                        drawCircle(
                            color = onSurfaceColor,
                            radius = 3.5.dp.toPx(),
                            center = center
                        )
                    }

                    // Stage 2: Radius line draws (0.3 -> 0.6)
                    if (p > 0.3f) {
                        val radiusProgress = ((p - 0.3f) / 0.3f).coerceIn(0f, 1f)
                        val endX = center.x + radius * radiusProgress
                        drawLine(
                            color = RadiusBlue,
                            start = center,
                            end = Offset(endX, center.y),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Stage 3: Diameter line appears (0.5 -> 0.8)
                    if (p > 0.5f) {
                        val diamAlpha = ((p - 0.5f) / 0.3f).coerceIn(0f, 1f)
                        val leftX = center.x - radius * diamAlpha
                        drawLine(
                            color = DiameterAmber.copy(alpha = diamAlpha * 0.7f),
                            start = center,
                            end = Offset(leftX, center.y),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Measurement labels fading in (0.7 -> 1.0)
            if (p > 0.7f) {
                val labelAlpha = ((p - 0.7f) / 0.3f).coerceIn(0f, 1f)
                Text(
                    text = "r  •  d  •  C  •  A",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = primaryColor.copy(alpha = labelAlpha)
                )
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Subtitle fade in (0.8 -> 1.0)
            val textAlpha = ((p - 0.6f) / 0.4f).coerceIn(0f, 1f)

            Text(
                text = "CIRCLEEDGE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                ),
                color = onSurfaceColor.copy(alpha = textAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Convert circle measurements",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha)
            )
        }
    }
}
