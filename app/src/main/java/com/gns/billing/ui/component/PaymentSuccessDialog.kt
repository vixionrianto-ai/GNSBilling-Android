package com.gns.billing.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun PaymentSuccessDialog(
    show: Boolean,
    invoice: String,
    pelanggan: String,
    nominal: String,
    onBackDetail: () -> Unit,
    onBackList: () -> Unit
) {

    if (!show) return

    AlertDialog(

        onDismissRequest = {},

        icon = {
            Surface(
                shape = CircleShape,
                color = Color(0xFF4CAF50)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .height(42.dp)
                )
            }
        },

        title = {
            Text(
                text = "Pembayaran Berhasil",
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column {

                Text("Invoice")
                Text(invoice)

                Spacer(modifier = Modifier.height(10.dp))

                Text("Pelanggan")
                Text(pelanggan)

                Spacer(modifier = Modifier.height(10.dp))

                Text("Nominal Dibayar")
                Text(nominal)

            }
        },

        confirmButton = {
            Button(
                onClick = onBackDetail
            ) {
                Text("Kembali ke Detail")
            }
        },

        dismissButton = {
            OutlinedButton(
                onClick = onBackList
            ) {
                Text("Daftar Tagihan")
            }
        }

    )
}