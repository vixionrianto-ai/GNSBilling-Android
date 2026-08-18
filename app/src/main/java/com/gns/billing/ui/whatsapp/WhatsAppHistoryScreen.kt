package com.gns.billing.ui.whatsapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gns.billing.model.WhatsAppLogItem
import com.gns.billing.model.WhatsAppStatistics
import com.gns.billing.viewmodel.WhatsAppHistoryViewModel

@Composable
fun WhatsAppHistoryScreen(
    navController: NavHostController,
    viewModel: WhatsAppHistoryViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val stats by viewModel.statistics.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val search by viewModel.search.collectAsState()
    val status by viewModel.status.collectAsState()
    val jenis by viewModel.jenis.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat WhatsApp") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::load) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatisticsRow(stats) }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = viewModel::setSearch,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Cari nama, nomor, atau invoice") }
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = status == null,
                        onClick = { viewModel.setStatus(null) },
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = status == "success",
                        onClick = { viewModel.setStatus("success") },
                        label = { Text("Berhasil") }
                    )
                    FilterChip(
                        selected = status == "failed",
                        onClick = { viewModel.setStatus("failed") },
                        label = { Text("Gagal") }
                    )
                    FilterChip(
                        selected = status == "pending",
                        onClick = { viewModel.setStatus("pending") },
                        label = { Text("Pending") }
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = jenis == null,
                        onClick = { viewModel.setJenis(null) },
                        label = { Text("Semua Jenis") }
                    )
                    FilterChip(
                        selected = jenis == "tagihan",
                        onClick = { viewModel.setJenis("tagihan") },
                        label = { Text("Tagihan") }
                    )
                    FilterChip(
                        selected = jenis == "pembayaran",
                        onClick = { viewModel.setJenis("pembayaran") },
                        label = { Text("Pembayaran") }
                    )
                }
            }

            if (loading) {
                item { CircularProgressIndicator() }
            }

            error?.let { message ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = viewModel::load) { Text("Coba Lagi") }
                        }
                    }
                }
            }

            if (!loading && logs.isEmpty() && error == null) {
                item {
                    Text("Belum ada riwayat WhatsApp.", style = MaterialTheme.typography.bodyLarge)
                }
            }

            items(logs, key = { it.id }) { log ->
                WhatsAppLogCard(log)
            }
        }
    }
}

@Composable
private fun StatisticsRow(stats: WhatsAppStatistics) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Statistik WhatsApp", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Total", stats.total)
            StatCard("Berhasil", stats.success)
            StatCard("Gagal", stats.failed)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Pending", stats.pending)
            StatCard("Hari ini", stats.today)
        }
    }
}

@Composable
private fun StatCard(label: String, value: Int) {
    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun WhatsAppLogCard(log: WhatsAppLogItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.pelanggan?.nama ?: log.nomor ?: "-", fontWeight = FontWeight.Bold)
                AssistChip(onClick = {}, label = { Text(log.status ?: "-") })
            }
            Text("Jenis: ${log.jenis ?: "-"}")
            Text("Invoice: ${log.tagihan?.invoiceNo ?: "-"}")
            Text("Waktu: ${log.sentAt ?: "-"}", style = MaterialTheme.typography.bodySmall)
            Text(log.pesan.orEmpty(), style = MaterialTheme.typography.bodySmall)
        }
    }
}