package com.gns.billing.ui.pembayaran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.DetailPembayaranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    navController: NavHostController,
    paymentId: Int,
    viewModel: DetailPembayaranViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(paymentId) {
        viewModel.fetchDetailPembayaran(paymentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pembayaran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
                uiState.detail != null -> {
                    val detail = uiState.detail!!
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Total Dibayar", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    formatRupiah(detail.dibayar),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    color = if (detail.status?.equals("Berhasil", true) == true || detail.status?.equals("Lunas", true) == true) Color(0xFF00897B) else Color.Gray,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        detail.status.orEmpty().ifEmpty { "Berhasil" },
                                        Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        Card(
                            Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Informasi Transaksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                DetailRow("No. Invoice", detail.invoice_no ?: detail.tagihan?.invoice ?: "-")
                                DetailRow("Nama Pelanggan", detail.tagihan?.pelanggan?.nama ?: "-")
                                DetailRow("Metode Pembayaran", detail.metode ?: "-")
                                DetailRow("Tanggal & Waktu", detail.tanggal_bayar ?: "-")
                                DetailRow("Keterangan", detail.keterangan ?: "-")
                            }
                        }
                    }
                }
                else -> Text("Data detail tidak ditemukan", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}
