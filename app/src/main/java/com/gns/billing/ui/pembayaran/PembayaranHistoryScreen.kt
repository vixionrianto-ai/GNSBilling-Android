package com.gns.billing.ui.pembayaran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.model.PembayaranItem
import com.gns.billing.utils.formatRupiah
import com.gns.billing.viewmodel.PembayaranHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranHistoryScreen(
    navController: NavHostController,
    viewModel: PembayaranHistoryViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.load(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pembayaran", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") } },
                actions = { IconButton(onClick = { viewModel.refresh() }) { Icon(Icons.Default.Refresh, "Refresh") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it; viewModel.load(true, it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                label = { Text("Cari invoice / pelanggan") },
                singleLine = true
            )
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp)) }

            if (loading && items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
                    items(items, key = { it.id }) { item -> PaymentCard(item) { navController.navigate("pembayaran/${item.id}") } }
                    if (items.isNotEmpty() && !loading) {
                        item {
                            OutlinedButton(onClick = { viewModel.nextPage() }, modifier = Modifier.fillMaxWidth()) { Text("Muat halaman berikutnya") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(item: PembayaranItem, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(item.invoice_no ?: "Tanpa invoice", fontWeight = FontWeight.Bold)
            Text(item.tagihan?.pelanggan?.nama ?: "Pelanggan -")
            Text("${item.metode ?: "-"} • ${item.tanggal_bayar ?: "-"}", style = MaterialTheme.typography.bodySmall)
            Text(formatRupiah(item.dibayar), fontWeight = FontWeight.Bold)
            Text(item.status ?: "-", style = MaterialTheme.typography.bodySmall)
        }
    }
}
