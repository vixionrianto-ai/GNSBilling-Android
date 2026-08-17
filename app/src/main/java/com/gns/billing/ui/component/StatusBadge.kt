package com.gns.billing.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status.uppercase()) {
        "PAID", "LUNAS" -> Pair(
            Color(0xFFD1FAE5),
            Color(0xFF047857)
        )
        "UNPAID", "PENDING", "BELUM BAYAR" -> Pair(
            Color(0xFFFEF3C7),
            Color(0xFFB45309)
        )
        else -> Pair(
            Color(0xFFFEE2E2),
            Color(0xFFB91C1C)
        )
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}