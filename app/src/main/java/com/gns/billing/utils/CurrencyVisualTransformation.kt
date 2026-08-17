package com.gns.billing.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {

    private val formatter = DecimalFormat(
        "#,###",
        DecimalFormatSymbols(Locale("id", "ID")).apply {
            groupingSeparator = '.'
        }
    )

    override fun filter(text: AnnotatedString): TransformedText {

        val original = text.text.filter { it.isDigit() }

        if (original.isEmpty()) {
            return TransformedText(
                AnnotatedString(""),
                OffsetMapping.Identity
            )
        }

        val formatted = formatter.format(original.toLong())

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return original.length
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}