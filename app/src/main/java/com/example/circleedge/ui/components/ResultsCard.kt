package com.example.circleedge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.data.model.AppSettings
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.CircleResult
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.domain.format.CircleFormatter

@Composable
fun ResultsCard(
    result: CircleResult,
    selectedInputType: CircleInputType,
    unit: MeasurementUnit,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CIRCLE RESULTS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "HIGH PRECISION",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Build items to display
            val items = buildList {
                if (settings.showRadius) {
                    add(
                        ResultItemData(
                            type = CircleInputType.RADIUS,
                            name = "RADIUS",
                            symbol = "r",
                            formattedValue = CircleFormatter.formatDimension(result.radiusMm, unit, settings.decimalPlaces),
                            isKnown = selectedInputType == CircleInputType.RADIUS
                        )
                    )
                }
                if (settings.showDiameter) {
                    add(
                        ResultItemData(
                            type = CircleInputType.DIAMETER,
                            name = "DIAMETER",
                            symbol = "D",
                            formattedValue = CircleFormatter.formatDimension(result.diameterMm, unit, settings.decimalPlaces),
                            isKnown = selectedInputType == CircleInputType.DIAMETER
                        )
                    )
                }
                if (settings.showCircumference) {
                    add(
                        ResultItemData(
                            type = CircleInputType.CIRCUMFERENCE,
                            name = "CIRCUMFERENCE",
                            symbol = "C",
                            formattedValue = CircleFormatter.formatDimension(result.circumferenceMm, unit, settings.decimalPlaces),
                            isKnown = selectedInputType == CircleInputType.CIRCUMFERENCE
                        )
                    )
                }
                if (settings.showArea) {
                    add(
                        ResultItemData(
                            type = CircleInputType.AREA,
                            name = "AREA",
                            symbol = "A",
                            formattedValue = CircleFormatter.formatArea(result.areaMm2, unit, settings.decimalPlaces),
                            isKnown = selectedInputType == CircleInputType.AREA
                        )
                    )
                }
            }

            // Adaptive layout: 2x2 grid if width >= 340dp, vertical stack otherwise
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 320.dp

                if (isWide && items.size >= 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in items.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ResultItemTile(item = items[i])
                                }
                                if (i + 1 < items.size) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ResultItemTile(item = items[i + 1])
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items.forEach { item ->
                            ResultItemTile(item = item)
                        }
                    }
                }
            }
        }
    }
}

private data class ResultItemData(
    val type: CircleInputType,
    val name: String,
    val symbol: String,
    val formattedValue: String,
    val isKnown: Boolean
)

@Composable
private fun ResultItemTile(
    item: ResultItemData,
    modifier: Modifier = Modifier
) {
    val borderColor = if (item.isKnown) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    val backgroundColor = if (item.isKnown) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${item.name} ${item.formattedValue}, ${if (item.isKnown) "known input" else "calculated value"}"
            },
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(if (item.isKnown) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.name} (${item.symbol})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = if (item.isKnown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (item.isKnown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (item.isKnown) "KNOWN" else "CALCULATED",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = if (item.isKnown) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.formattedValue,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
