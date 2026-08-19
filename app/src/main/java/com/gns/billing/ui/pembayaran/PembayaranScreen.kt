package com.gns.billing.ui.pembayaran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.PembayaranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranScreen(
    navController: NavHostController,
    tagihanId: Int,
    namaPelanggan: String = "",
    invoiceNo: String = "",
    totalTagihan: String = "",
    viewModel: PembayaranViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var jumlah by remember(totalTagihan) { mutableStateOf(totalTagihan) }
    var biayaAdmin by remember { mutableStateOf("0") }
    var keterangan by remember { mutableStateOf("") }
    var metode by remember { mutableStateOf("Cash") }
    var expanded by remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Informasi Tagihan", fontWeight = FontWeight.Bold)
                    Text("Pelanggan: ${namaPelanggan.ifBlank { "-" }}")
                    Text("Invoice: ${invoiceNo.ifBlank { "-" }}")
                    Text("Total: ${formatRupiah(totalTagihan.toDoubleOrNull() ?: 0.0)}", fontWeight = FontWeight.Bold)
                    Text("Nilai tagihan berasal dari Laravel.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Data Pembayaran", fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = metode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Metode") },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Cash", "Transfer Bank", "QRIS").forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = { metode = it; expanded = false })
                            }
                        }
                    }
                    OutlinedTextField(
                        value = jumlah,
                        onValueChange = { jumlah = it.filter(Char::isDigit) },
                        label = { Text("Jumlah Dibayar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = biayaAdmin,
                        onValueChange = { biayaAdmin = it.filter(Char::isDigit) },
                        label = { Text("Biaya Admin") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth(), minLines = 3
                    )
                }
            }

            uiState.serverResult?.let { result ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Hasil Transaksi", fontWeight = FontWeight.Bold)
                        Text("Invoice: ${result.invoice_no ?: "-"}")
                        Text("Status: ${result.status}")
                        Text("Total bayar: ${formatRupiah(result.total_bayar)}")
                        Text("Dibayar: ${formatRupiah(result.dibayar)}")
                        Text("Kembalian: ${formatRupiah(result.kembalian)}")
                    }
                }
            }

            uiState.message?.let {
                Text(it, color = if (uiState.isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    viewModel.onMetodeChange(metode)
                    viewModel.onNominalChange(jumlah)
                    viewModel.onBiayaAdminChange(biayaAdmin)
                    viewModel.onKeteranganChange(keterangan)
                    viewModel.submitPembayaran(tagihanId)
                },
                enabled = !uiState.isLoading && tagihanId > 0 && jumlah.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                else Text("Proses Pembayaran")
            }
        }
    }
}
