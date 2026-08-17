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
import androidx.compose.ui.graphics.Color
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

// Fungsi bantuan untuk format angka dengan pemisah ribuan titik otomatis
fun formatRibuan(input: String): String {
    val cleanString = input.replace(".", "").replace(",", "").trim()
    if (cleanString.isEmpty()) return ""
    val parsed = cleanString.toLongOrNull() ?: return input
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale("in", "ID")))
    return formatter.format(parsed)
}

// Fungsi untuk membersihkan format titik sebelum dikirim ke ViewModel/Server
fun parseRibuanToCleanDouble(input: String): Double {
    val cleanString = input.replace(".", "").replace(",", "").trim()
    return cleanString.toDoubleOrNull() ?: 0.0
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

    LaunchedEffect(Unit) {
        viewModel.loadSummary()
    }
    // State lokal dengan format ribuan otomatis
    var biayaAdminText by remember { mutableStateOf("0") }
    var jumlahDibayarText by remember {
        mutableStateOf(formatRibuan(totalTagihan.substringBefore(".")))
    }
    var catatanText by remember { mutableStateOf("") }
    var expandedMetode by remember { mutableStateOf(false) }

    // Hitung matematis menggunakan angka bersih
    val cleanTotalTagihanStr = totalTagihan.substringBefore(".").replace(".", "").replace(",", "").trim()
    val rawTagihan = cleanTotalTagihanStr.toDoubleOrNull() ?: 0.0

    val biayaAdmin = parseRibuanToCleanDouble(biayaAdminText)
    val totalPembayaran = rawTagihan + biayaAdmin

    val jumlahDibayar = parseRibuanToCleanDouble(jumlahDibayarText)
    val kembalian = if (jumlahDibayar >= totalPembayaran) jumlahDibayar - totalPembayaran else 0.0

    // Status Pembayaran otomatis
    val statusPembayaran = when {
        jumlahDibayar == totalPembayaran -> "Pembayaran Pas"
        jumlahDibayar < totalPembayaran -> "Kurang Bayar"
        else -> "Lebih Bayar / Kembalian"
    }
    val statusColor = when {
        jumlahDibayar == totalPembayaran -> Color(0xFF00897B) // Tosca / Hijau
        jumlahDibayar < totalPembayaran -> Color(0xFFC62828)  // Merah
        else -> Color(0xFFE65100)                             // Oranye
    }

    // Tampilkan Toast jika ada pesan sukses / error dari server
    LaunchedEffect(uiState.message) {
        uiState.message?.let { pesan ->
            Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show()
            if (uiState.isSuccess) {
                navController.popBackStack()
            }
            viewModel.resetMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran Tagihan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
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
            // ==========================================
            // INFORMASI PELANGGAN & INVOICE
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Informasi Pelanggan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Nama Pelanggan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(
                                text = namaPelanggan.ifEmpty { "Pelanggan Umum" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Invoice", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(
                                text = invoiceNo.ifEmpty { "-" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ==========================================
            // FORM PEMBAYARAN (METODE & BIAYA ADMIN)
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Form Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)

                    // Pilihan Metode Pembayaran
                    ExposedDropdownMenuBox(
                        expanded = expandedMetode,
                        onExpandedChange = { expandedMetode = !expandedMetode }
                    ) {
                        OutlinedTextField(
                            value = uiState.metode.ifEmpty { "Cash" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Metode Pembayaran") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMetode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
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

                    // Biaya Admin dengan format titik otomatis
                    OutlinedTextField(
                        value = biayaAdminText,
                        onValueChange = { biayaAdminText = formatRibuan(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Biaya Admin (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // ==========================================
            // KARTU TOTAL PEMBAYARAN & KEMBALIAN
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Total Pembayaran", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = formatRupiah(totalPembayaran), style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Kembalian", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = formatRupiah(kembalian), style = MaterialTheme.typography.titleMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ==========================================
            // JUMLAH DIBAYAR & STATUS PEMBAYARAN
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Jumlah Dibayar dengan format titik otomatis ribuan
                    OutlinedTextField(
                        value = jumlahDibayarText,
                        onValueChange = { jumlahDibayarText = formatRibuan(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Jumlah Dibayar (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Status Pembayaran", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = statusColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = statusPembayaran,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // CATATAN PEMBAYARAN
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Catatan Pembayaran", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = catatanText,
                        onValueChange = { catatanText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("Tambahkan catatan pembayaran...") },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // TOMBOL PROSES PEMBAYARAN
            // ==========================================
            Button(
                onClick = {
                    // Ambil angka bersih yang diketik di kolom Jumlah Dibayar
                    val finalNominal = parseRibuanToCleanDouble(jumlahDibayarText)

                    // Pastikan ViewModel memperbarui nominal secara langsung sebelum dikirim ke server
                    viewModel.onNominalChange(finalNominal.toLong().toString())

                    // Kirim proses pembayaran
                    viewModel.submitPembayaran(tagihanId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading && jumlahDibayarText.isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Proses & Lunasi Pembayaran", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}