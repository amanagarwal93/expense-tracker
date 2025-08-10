package com.example.expensetracker.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Long>>,
    barColor: Color = MaterialTheme.colorScheme.primary,
    maxBarHeight: Dp = 160.dp,
    valueFormatter: (Long) -> String = { "₹$it" }
) {
    val max = (data.maxOfOrNull { it.second } ?: 1L).coerceAtLeast(minimumValue = 1L)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        data.forEach { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(weight = 1f)
            ) {
                Box(
                    modifier = Modifier
                        .height(maxBarHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val fraction =
                        (value.toFloat() / max.toFloat())
                            .coerceIn(
                                minimumValue = 0.02f,
                                maximumValue = 1f
                            )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.6f)
                            .fillMaxHeight(fraction)
                            .background(
                                color = barColor,
                                shape = RoundedCornerShape(size = 6.dp)
                            )
                    )
                }
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Text(
                    text = valueFormatter(value),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
