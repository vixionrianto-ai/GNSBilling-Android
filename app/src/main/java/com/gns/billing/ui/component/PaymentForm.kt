package com.gns.billing.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.gns.billing.utils.CurrencyVisualTransformation

@Composable
fun PaymentForm(
    nominal: String,
    onNominalChange: (String) -> Unit,
    metode: String,
    onMetodeChange: (String) -> Unit,
    biayaAdmin: String,
    onBiayaAdminChange: (String) -> Unit,
    keterangan: String,
    onKeteranganChange: (String) -> Unit
) {

    OutlinedTextField(

        value = nominal,
        visualTransformation = CurrencyVisualTransformation(),

        onValueChange = {

            onNominalChange(
                it.filter { char -> char.isDigit() }
            )

        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),

        label = {
            Text("Nominal Bayar")
        },

        modifier = Modifier.fillMaxWidth()

    )

    PaymentMethodDropdown(
        selected = metode,
        onSelected = onMetodeChange
    )

    OutlinedTextField(
        value = biayaAdmin,
        onValueChange = onBiayaAdminChange,
        label = { Text("Biaya Admin") },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = keterangan,
        onValueChange = onKeteranganChange,
        label = { Text("Keterangan") },
        modifier = Modifier.fillMaxWidth()
    )

}