package com.gns.billing.ui.pembayaran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.PembayaranDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranDetailScreen(
    navController: NavHostController,
    pembayaranId: Int,
    viewModel: PembayaranDetailViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(pembayaranId) { viewModel.load(pembayaranId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Detail Pembayaran", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") } }
        )
    }) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        } else if (error != null) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load(pembayaranId) }) { Text("Coba Lagi") }
            }
        } else {
            data?.let { item ->
                Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(item.invoice_no ?: "Tanpa Invoice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Status: ${item.status ?: "-"}")
                            HorizontalDivider()
                            Text("Pelanggan: ${item.tagihan?.pelanggan?.nama ?: "-"}")
                            Text("Invoice Tagihan: ${item.tagihan?.invoice ?: "-"}")
                            Text("Metode: ${item.metode ?: "-"}")
                            Text("Tanggal: ${item.tanggal_bayar ?: "-"}")
                            Text("Dibayar: ${formatRupiah(item.dibayar)}", fontWeight = FontWeight.Bold)
                            Text("Total Bayar: ${formatRupiah(item.total_bayar)}")
                            Text("Keterangan: ${item.keterangan ?: "-"}")
                            Text("Operator: ${item.user?.name ?: "-"}")
                        }
                    }
                    Text("Data di atas berasal dari server GNS.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
