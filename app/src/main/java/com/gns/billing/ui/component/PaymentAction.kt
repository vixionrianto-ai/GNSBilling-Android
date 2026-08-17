package com.gns.billing.ui.component

import androidx.compose.runtime.Composable

@Composable
fun PaymentAction(
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {

    PrimaryButton(

        text = if (loading)
            "Menyimpan..."
        else
            "Simpan Pembayaran",

        enabled = enabled,

        onClick = onClick

    )

}