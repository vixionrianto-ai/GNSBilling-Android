package com.gns.billing.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun KpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp), // Reduced padding
            verticalArrangement = Arrangement.spacedBy(2.dp) // Reduced spacing
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall, // Smaller title font
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium, // Smaller value font
                fontWeight = FontWeight.Bold
            )
        }
    }
}
