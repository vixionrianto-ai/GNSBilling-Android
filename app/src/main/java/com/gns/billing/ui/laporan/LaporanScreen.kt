package com.gns.billing.ui.laporan

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.PembayaranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    navController: NavController,
    viewModel: PembayaranViewModel = viewModel()
) {
    val historyList by viewModel.historyList.collectAsState()
    val isLoading by viewModel.loadingHistory.collectAsState()
    val context = LocalContext.current
    var showFilterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Keuangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Filter Tanggal")
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "Fitur Download Laporan diproses...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading && historyList.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Riwayat Transaksi Masuk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Data transaksi diambil langsung dari server Laravel.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (historyList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada data laporan transaksi.")
                            }
                        }
                    } else {
                        items(historyList) { item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.tagihan?.pelanggan?.nama ?: "Pelanggan -", fontWeight = FontWeight.Bold)
                                        Text("INV: ${item.invoice_no ?: item.tagihan?.invoice ?: "-"}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(formatRupiah(item.dibayar), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Laporan") },
            text = { Text("Filter laporan akan mengikuti parameter dan data yang disediakan API server.") },
            confirmButton = { TextButton(onClick = { showFilterDialog = false }) { Text("Tutup") } }
        )
    }
}
