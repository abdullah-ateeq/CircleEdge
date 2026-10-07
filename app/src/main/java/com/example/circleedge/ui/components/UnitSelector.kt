package com.example.circleedge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.MeasurementUnit

@Composable
fun UnitSelector(
    selectedUnit: MeasurementUnit,
    inputType: CircleInputType,
    onSelectUnit: (MeasurementUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val units = MeasurementUnit.values()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        units.forEach { unit ->
            val isSelected = unit == selectedUnit
            val symbol = unit.symbolFor(inputType)

            FilterChip(
                selected = isSelected,
                onClick = { onSelectUnit(unit) },
                label = {
                    Text(
                        text = symbol,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = "Unit ${unit.label} ($symbol)"
                    },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}
