package com.example.circleedge.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

@Composable
fun PresetsRow(
    onPresetSelected: (BigDecimal) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(
        Pair("1 cm", BigDecimal("1")),
        Pair("5 cm", BigDecimal("5")),
        Pair("10 cm", BigDecimal("10")),
        Pair("25 cm", BigDecimal("25")),
        Pair("50 cm", BigDecimal("50")),
        Pair("100 cm", BigDecimal("100"))
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Presets:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp, end = 2.dp)
        )
        presets.forEach { (label, value) ->
            AssistChip(
                onClick = { onPresetSelected(value) },
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Quick preset $label"
                }
            )
        }
    }
}
