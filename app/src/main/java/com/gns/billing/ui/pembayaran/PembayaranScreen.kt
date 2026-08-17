package com.gns.billing.ui.pembayaran

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.PembayaranViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private fun formatRibuan(input: String): String {
    val clean = input.replace(".", "").replace(",", "").trim()
    if (clean.isEmpty()) return ""
    val number = clean.toLongOrNull() ?: return input
    return DecimalFormat("#,###", DecimalFormatSymbols(Locale("in", "ID"))).format(number)
}

private fun parseRibuan(input: String): Double {
    return input.replace(".", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranScreen(
    navController: NavHostController,
    tagihanId: Int = 0,
    namaPelanggan: String = "",
    invoiceNo: String = "",
    totalTagihan: String = "",
    viewModel: PembayaranViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var biayaAdminText by remember { mutableStateOf("0") }
    var jumlahDibayarText by remember { mutableStateOf(formatRibuan(totalTagihan.substringBefore("."))) }
    var catatanText by remember { mutableStateOf("") }
    var expandedMetode by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { pesan ->
            Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show()
            if (uiState.isSuccess) navController.popBackStack()
            viewModel.resetMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran Tagihan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Informasi Tagihan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()
                    Text("Pelanggan: ${namaPelanggan.ifEmpty { "Pelanggan Umum" }}")
                    Text("Invoice: ${invoiceNo.ifEmpty { "-" }}")
                    Text("Total tagihan: ${formatRupiah(parseRibuan(totalTagihan))}", fontWeight = FontWeight.Bold)
                    Text("Nilai tagihan berasal dari server.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Form Pembayaran", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()

                    ExposedDropdownMenuBox(
                        expanded = expandedMetode,
                        onExpandedChange = { expandedMetode = !expandedMetode }
                    ) {
                        OutlinedTextField(
                            value = uiState.metode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Metode Pembayaran") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMetode) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedMetode,
                            onDismissRequest = { expandedMetode = false }
                        ) {
                            listOf("Cash", "Transfer Bank", "QRIS").forEach { metode ->
                                DropdownMenuItem(
                                    text = { Text(metode) },
                                    onClick = {
                                        viewModel.onMetodeChange(metode)
                                        expandedMetode = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = biayaAdminText,
                        onValueChange = { biayaAdminText = formatRibuan(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Biaya Admin (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = jumlahDibayarText,
                        onValueChange = { jumlahDibayarText = formatRibuan(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Jumlah Dibayar (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = catatanText,
                        onValueChange = { catatanText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Catatan Pembayaran") },
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            uiState.serverResult?.let { result ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Hasil dari Server", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        HorizontalDivider()
                        Text("Status: ${result.status}")
                        Text("Total bayar: ${formatRupiah(result.total_bayar)}")
                        Text("Dibayar: ${formatRupiah(result.dibayar)}")
                        Text("Kembalian: ${formatRupiah(result.kembalian)}")
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.onNominalChange(parseRibuan(jumlahDibayarText).toLong().toString())
                    viewModel.onBiayaAdminChange(parseRibuan(biayaAdminText).toLong().toString())
                    viewModel.onKeteranganChange(catatanText)
                    viewModel.submitPembayaran(tagihanId)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading && jumlahDibayarText.isNotBlank() && tagihanId > 0
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Proses Pembayaran", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
